package com.michaelgrenon.chargerks;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AskDataQuestion implements Question {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private NeoGraph query;

	private String contextName;

	private int resultLimit;

	public AskDataQuestion(String contextName, NeoGraph query, int resultLimit) {
        this.query = query;
        this.contextName = contextName;
        this.resultLimit = resultLimit;
    }

	@Override
	public String toCypher() {
        List<NeoActorBinding> actors = query.getActors();

        Set<NeoConceptBinding> visitedConcepts = new HashSet<NeoConceptBinding>();
        Set<String> variables = new HashSet<String>();

        //MATCH relPattern1 WITH visitedConcepts, ... 
        Iterator<NeoRelationBinding> relItr = query.getRelations().iterator();
        StringBuilder builder = new StringBuilder();       
        while (relItr.hasNext()) {
            //MATCH relPattern
            builder.append("MATCH ");
            NeoRelationBinding relationBinding = relItr.next();
            variables.add(relationBinding.getVariable());

            NeoRelation relation = relationBinding.getRelation();
            NeoConceptBinding cBinding1 = relation.getConcept1();
            NeoConceptBinding cBinding2 = relation.getConcept2();

            if (!visitedConcepts.contains(cBinding1)) {
                visitedConcepts.add(cBinding1);
                variables.add(cBinding1.getVariable());
                builder.append(cBinding1.toCypherWithoutContext());
            } else {
                builder.append(String.format("(%s)", cBinding1.getVariable()));
            }

            builder.append(String.format("-[:matches*0..9]-()-[%s:`%s`]->()-[:matches*0..9]-", relationBinding.getVariable(), relation.getLabel()));

            if (!visitedConcepts.contains(cBinding2)) {
                visitedConcepts.add(cBinding2);
                variables.add(cBinding2.getVariable());
                builder.append(cBinding2.toCypherWithoutContext());
            } else {
                builder.append(String.format("(%s)", cBinding2.getVariable()));
            }
            builder.append(NEW_LINE);

            //WITH concept1, concept2, ...
            builder.append("WITH DISTINCT ");
            Iterator<NeoConceptBinding> visIterator = visitedConcepts.iterator();
            while (visIterator.hasNext()) {
                NeoConceptBinding bnd = visIterator.next();
                builder.append(bnd.getVariable());
                if (visIterator.hasNext()) {
                    builder.append(", ");
                }
            }

            builder.append(NEW_LINE);
        }

        //WHERE (var1.contextType = "STORE" OR var1.contextName = ctxOfUse) AND (var2 ...
        builder.append("WHERE ");
        builder.append(NEW_LINE);
        Iterator<String> varItr = variables.iterator();
        while (varItr.hasNext()) {
            String var = varItr.next();
            builder.append("(");
            builder.append(var);
            builder.append(".contextType = '");
            builder.append(ContextType.STORE.name());
            builder.append("' OR ");
            builder.append(var);
            builder.append(".contextName = '");
            builder.append(contextName);
            builder.append("') ");

            if (varItr.hasNext()) {
                builder.append("AND");
            }
            builder.append(NEW_LINE);
        }


        //WITH DISTINCT x1.referent AS x1r, ...
        builder.append("WITH DISTINCT ");
        Iterator<NeoConceptBinding> visIterator = visitedConcepts.iterator();
        while (visIterator.hasNext()) {
            NeoConceptBinding c = visIterator.next();
            builder.append(c.getVariable());
            builder.append(".referent AS ");
            builder.append(c.getReferentVariable());
            if (visIterator.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append(NEW_LINE);

        //sort actors by input and output dependencies, cycles not supported
        //actors output cannot be used for anything other than input to other actors
        Collections.sort(actors);

        //WITH *, actorOneExpression AS actorOneVariable
        //WITH *, actorTwoExpression(may use actorOneVariable) AS actorTwoVariable
        for (NeoActorBinding actorBinding : actors) {
            ActorLambda lambda = new ActorLambda(actorBinding, visitedConcepts);
            builder.append("WITH ");
            builder.append(lambda.toCypherExpressionOrProcedure());
            builder.append(" AS ");
            builder.append(lambda.getVariable());
            builder.append(NEW_LINE);
        }

        //WHERE criteria1 AND criteria2, ...., AND NOT criteran, ...
        List<ActorLambda> constraints = actors.stream().map(a -> new ActorLambda(a, visitedConcepts)).filter(lam -> lam.isConstraint()).collect(Collectors.toList());
        Iterator<ActorLambda> lamItr = constraints.iterator();
        if (lamItr.hasNext()) builder.append("WHERE ");
        while(lamItr.hasNext()) {
            ActorLambda lambda = lamItr.next();
            if (lambda.isNegativeContraint()) builder.append(" NOT ");
            builder.append(lambda.getVariable());
            if (lamItr.hasNext()) {
                builder.append(" AND ");
            }
        }
        builder.append(NEW_LINE);

        builder.append("RETURN * LIMIT ");
        builder.append(resultLimit);

        return builder.toString();
	}

	@Override
	public Answer getAnswer() {
		return new PatternMatchAnswer(query);
	}
}
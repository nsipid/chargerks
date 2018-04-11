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

	private int maxVariableExpansion;

	private boolean maintainContextualInfo;

	public AskDataQuestion(String contextName, NeoGraph query, int resultLimit, int maxVariableExpansion, boolean maintainContextualInfo) {
        this.query = query;
        this.contextName = contextName;
        this.resultLimit = resultLimit;
        this.maxVariableExpansion = maxVariableExpansion;
        this.maintainContextualInfo = maintainContextualInfo;
    }

	@Override
	public String toCypher() {
        NeoActorDag actorDag = query.getActors();

        Set<NeoConceptBinding> visitedConcepts = new HashSet<NeoConceptBinding>();
        Set<NeoRelationBinding> visitedRelations = new HashSet<NeoRelationBinding>();

        Set<String> variables = new HashSet<String>();

        //MATCH relPattern1 WITH visitedConcepts, ... , visitedRelations
        Iterator<NeoRelationBinding> relItr = query.getRelations().iterator();
        StringBuilder builder = new StringBuilder();       
        while (relItr.hasNext()) {
            //MATCH relPattern
            builder.append("MATCH ");
            NeoRelationBinding relationBinding = relItr.next();
            visitedRelations.add(relationBinding);
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

            builder.append(String.format("-[:matches*0..%d]-()-[%s:`%s`]->()-[:matches*0..%1$d]-", maxVariableExpansion, relationBinding.getVariable(), relation.getLabel()));

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
            Iterator<String> vIterator = variables.iterator();
            while (vIterator.hasNext()) {
                String v = vIterator.next();
                builder.append(v);
                if (vIterator.hasNext()) {
                    builder.append(", ");
                }
            }

            builder.append(NEW_LINE);
        }

        //WHERE (var1.contextType = "STORE" OR var1.contextName = ctxOfUse) AND ((var1.invalid_for_ctxOfUseName IS NULL) OR (NOT var1.invalid_for_ctxOfUseName)) AND (var2 ...
        builder.append("WHERE ");
        builder.append(NEW_LINE);
        Set<String> conceptVars = visitedConcepts.stream().map(c -> c.getVariable()).collect(Collectors.toSet());
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

            // if its a node we must make sure the context of use did not decide it was invalid
            if (conceptVars.contains(var)) {
                builder.append("AND ((");
                builder.append(var);
                builder.append(".invalid_for_");
                builder.append(contextName);
                builder.append(" IS NULL) OR (NOT ");
                builder.append(var);
                builder.append(".invalid_for_");
                builder.append(contextName);
                builder.append("))");
            }

            if (varItr.hasNext()) {
                builder.append(" AND");
            }
            builder.append(NEW_LINE);
        }


        //WITH DISTINCT x1.referent AS x1r, ...
        builder.append("WITH DISTINCT ");
        Iterator<NeoConceptBinding> visIterator = visitedConcepts.iterator();
        while (visIterator.hasNext()) {
            NeoConceptBinding c = visIterator.next();
            builder.append(c.getVariable());
            if (!maintainContextualInfo) {
                builder.append(".referent AS ");
                builder.append(c.referToReferentAsRecord());
            }
            if (visIterator.hasNext()) {
                builder.append(",");
            }
        }

        if (maintainContextualInfo) {
            Iterator<NeoRelationBinding> relIterator = visitedRelations.iterator();

            while (relIterator.hasNext()) {
                builder.append(", ");
                NeoRelationBinding c = relIterator.next();
                builder.append(c.getVariable());
            }
        }
        builder.append(NEW_LINE);

        //sort actors by input and output dependencies, cycles not supported
        //actors output cannot be used for anything other than input to other actors
        List<NeoActorBinding> actors = actorDag.topoSort();

        //WITH *, actorOneExpression AS actorOneVariable
        //WITH *, actorTwoExpression(may use actorOneVariable) AS actorTwoVariable
        for (NeoActorBinding actorBinding : actors) {
            ActorLambda lambda = new ActorLambda(actorBinding, visitedConcepts, !maintainContextualInfo);
            builder.append("WITH *, ");
            builder.append(lambda.toCypherExpressionOrProcedure());
            builder.append(" AS ");
            builder.append(lambda.getVariable());
            builder.append(NEW_LINE);
        }

        //WHERE criteria1 AND criteria2, ...., AND NOT criteran, ...
        List<ActorLambda> constraints = actors.stream().map(a -> new ActorLambda(a, visitedConcepts, !maintainContextualInfo)).filter(lam -> lam.isConstraint()).collect(Collectors.toList());
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

        builder.append("RETURN *");
        if (resultLimit > 0) {
            builder.append(" LIMIT ");
            builder.append(resultLimit);
        }
    
        return builder.toString();
	}

	@Override
	public Answer getAnswer() {
		return maintainContextualInfo ? new ContextualPatternMatchAnswer(query) : new PatternMatchAnswer(query);
	}
}
package com.michaelgrenon.chargerks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
        Set<NeoConceptBinding> visitedConcepts = new HashSet<NeoConceptBinding>();
        Set<String> variables = new HashSet<String>();

        //MATCH relPattern1, relPattern2, ...
        Iterator<NeoRelationBinding> relItr = query.getRelations().iterator();
        StringBuilder builder = new StringBuilder();
        builder.append("MATCH ");
        builder.append(NEW_LINE);
        while (relItr.hasNext()) {
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

            builder.append(String.format("-[:matches*0..9]-()-[%s:`%s`]-()-[:matches*0..9]-", relationBinding.getVariable(), relation.getLabel()));

            if (!visitedConcepts.contains(cBinding2)) {
                visitedConcepts.add(cBinding2);
                variables.add(cBinding2.getVariable());
                builder.append(cBinding2.toCypherWithoutContext());
            } else {
                builder.append(String.format("(%s)", cBinding2.getVariable()));
            }

            if (relItr.hasNext()) {
                builder.append(",");
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

        builder.append("RETURN * LIMIT ");
        builder.append(resultLimit);

        return builder.toString();
	}

	@Override
	public Answer getAnswer() {
		return new PatternMatchAnswer(query);
	}
}
package com.michaelgrenon.chargerks;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;

public class ApplyContextOfIntentCommand implements Command {
    private static final String NEW_LINE = System.getProperty("line.separator");
	private NeoGraph contextOfUse;
	private String csvPath;

    public ApplyContextOfIntentCommand(NeoGraph contextOfIntent, String csvPath) {
        this.contextOfUse = contextOfIntent;
        this.csvPath = csvPath;
    }

    @Override
    public String toCypher() {
        StringBuilder builder = new StringBuilder();
        builder.append("LOAD CSV WITH HEADERS FROM ");
        builder.append(csvPath);
        builder.append(" as line");
        builder.append(NEW_LINE);
        builder.append("CALL apoc.convert.toJson(line) YIELD jsonline");
        builder.append(NEW_LINE);

        for (NeoRelation relation : contextOfUse.getRelations()) {
            buildNodeString(builder, relation.getConcept1());
            buildNodeString(builder, relation.getConcept2());

            builder.append(relation.toCypherExplicit());
            builder.append(NEW_LINE);
        }

        return builder.toString();
    }

    private void buildNodeString(StringBuilder builder, NeoConceptBinding template) {
        ContextInfo instanceContext = new ContextInfo(ContextType.STORE, template.getConcept().getContext().getName());
        String instanceReferent;

        if (template.getConcept().getType().toUpperCase().equals("RECORD")) {
            instanceReferent = "jsonline";
        } else {
            instanceReferent = "line.`" + template.getConcept().getReferent() + "`";
        }
            
        NeoConceptBinding instanceConcept = new NeoConceptBinding(template.getVariable(), new NeoConcept(template.getConcept().getType(), instanceReferent, instanceContext));
        
        builder.append("MERGE ");
        builder.append(instanceConcept.toCypher());
        builder.append(NEW_LINE);
    }
}
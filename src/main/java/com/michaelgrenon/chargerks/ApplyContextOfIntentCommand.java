package com.michaelgrenon.chargerks;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;

public class ApplyContextOfIntentCommand implements Command {
    private static final String NEW_LINE = System.getProperty("line.separator");
	private NeoGraph contextOfIntent;
	private String csvPath;

    public ApplyContextOfIntentCommand(NeoGraph contextOfIntent, String csvPath) {
        this.contextOfIntent = contextOfIntent;
        this.csvPath = csvPath;
    }

    @Override
    public String toCypher() {
        StringBuilder builder = new StringBuilder();
        builder.append("LOAD CSV WITH HEADERS FROM ");
        builder.append(csvPath);
        builder.append(" as line");
        builder.append(NEW_LINE);
        builder.append("CALL apoc.convert.toJson(line) YIELD jsonLine");
        builder.append(NEW_LINE);

        for (NeoRelation relation : contextOfIntent.getRelations()) {
            NeoConceptBinding c1 = appendNodeString(builder, relation.getConcept1());
            NeoConceptBinding c2 = appendNodeString(builder, relation.getConcept2());

            appendRelationString(builder, c1, c2, relation.getLabel());
        }

        return builder.toString();
    }

    private NeoConceptBinding appendNodeString(StringBuilder builder, NeoConceptBinding template) {
        ContextInfo instanceContext = new ContextInfo(ContextType.STORE, template.getConcept().getContext().getName());
        String instanceReferent = null;
        String operation = "MERGE ";

        if (template.getConcept().getType().toUpperCase().equals("RECORD")) {
            instanceReferent = "jsonLine";
        } else if (!template.getConcept().getReferent().isPresent()) { 
            operation = "CREATE ";
        } else {
            instanceReferent = "line.`" + template.getConcept().getReferent() + "`";
        }
            
        NeoConceptBinding instanceConcept = new NeoConceptBinding(template.getVariable(), new NeoConcept(template.getConcept().getType(), instanceReferent, instanceContext));
        
        builder.append(operation);
        builder.append(instanceConcept.toCypher());
        builder.append(NEW_LINE);

        return instanceConcept;
    }

    private void appendRelationString(StringBuilder builder, NeoConceptBinding concept1, NeoConceptBinding concept2, String label) {     
        NeoRelation instanceRelation = new NeoRelation(concept1, concept2, concept1.getConcept().getContext(), label);
        builder.append(instanceRelation.toCypherExplicit());
        builder.append(NEW_LINE);
    }
}
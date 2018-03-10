package com.michaelgrenon.chargerks.dataspace;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoGraph;

public class CsvConceptualGraphLoader {
    private static final String NEW_LINE = System.getProperty("line.separator");

    public String generateCypherFromMetadata(NeoGraph contextOfUse, String csvPath) {
        StringBuilder builder = new StringBuilder();
        builder.append("LOAD CSV WITH HEADERS FROM ");
        builder.append(csvPath);
        builder.append(" as line");
        builder.append(NEW_LINE);
        builder.append("CALL apoc.convert.toJson(line) YIELD jsonline");
        builder.append(NEW_LINE);

        for (NeoConcept concept : contextOfUse.getConcepts()) {
            ContextInfo instanceContext = new ContextInfo(ContextType.STORE, concept.getContext().getName());
            String instanceReferent;

            if (concept.getType().toUpperCase().equals("RECORD")) {
                instanceReferent = "jsonline";
            } else {
                instanceReferent = "line.`" + concept.getReferent() + "`";
            }
             
            NeoConcept instanceConcept = new NeoConcept("", concept.getType(), instanceReferent, instanceContext);
            
            builder.append("MERGE ");
            builder.append(instanceConcept.toCypher());
        }
    }
}
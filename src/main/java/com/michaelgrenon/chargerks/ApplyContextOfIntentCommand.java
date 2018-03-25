package com.michaelgrenon.chargerks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

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
        final String coalesceTemplate = "COALESCE(line.%1$s, '') AS %1$s";
        HashSet<NeoConceptBinding> visitedConcepts = new HashSet<NeoConceptBinding>();
        Set<String> headings = contextOfIntent.getConcepts().stream().filter(c -> c.getConcept().getReferent().isPresent()).map(c -> String.format("`%s`",c.getConcept().getReferent().get())).collect(Collectors.toSet());

        StringBuilder builder = new StringBuilder();
        builder.append("LOAD CSV WITH HEADERS FROM '");
        builder.append(csvPath);
        builder.append("' as line");
        builder.append(NEW_LINE);
        builder.append("WITH line,");
        builder.append(NEW_LINE);
        Iterator<String> itr = headings.iterator();
        
        while (itr.hasNext()) {
            String heading = itr.next();
            builder.append(String.format(coalesceTemplate, heading));
            if (itr.hasNext()) {
                builder.append(",");
            }
            builder.append(NEW_LINE);
        }

        for (NeoRelation relation : contextOfIntent.getRelations()) {
            NeoConceptBinding c1 = appendNodeString(builder, visitedConcepts, relation.getConcept1());
            NeoConceptBinding c2 = appendNodeString(builder, visitedConcepts, relation.getConcept2());

            appendRelationString(builder, c1, c2, relation.getLabel());
        }

        return builder.toString();
    }

    private NeoConceptBinding appendNodeString(StringBuilder builder, Set<NeoConceptBinding> visitedConcepts, NeoConceptBinding template) {
        ContextInfo instanceContext = new ContextInfo(ContextType.STORE, template.getConcept().getContext().getName());
        String instanceReferent = null;
        
        boolean doMerge = true;

        if (template.getConcept().getType().toUpperCase().equals("RECORD")) {
            instanceReferent = "apoc.convert.toJson(line)";
        } else if (!template.getConcept().getReferent().isPresent()) { 
            doMerge = false;
        } else {
            instanceReferent = "`" + template.getConcept().getReferent().get() + "`";
        }
            
        NeoConceptBinding instanceConcept = new NeoConceptBinding(template.getVariable(), new NeoConcept(template.getConcept().getType(), instanceReferent, instanceContext));
        
        if (!visitedConcepts.contains(instanceConcept)) {
            visitedConcepts.add(instanceConcept);
            if (doMerge) {
                builder.append("MERGE ");
            } else {
                builder.append("CREATE ");
            }

            builder.append(instanceConcept.toCypherWithSpecialReferent());
            builder.append(NEW_LINE);
        }

        return instanceConcept;
    }

    private void appendRelationString(StringBuilder builder, NeoConceptBinding concept1, NeoConceptBinding concept2, String label) {     
        NeoRelation instanceRelation = new NeoRelation(concept1, concept2, concept1.getConcept().getContext(), label);
        builder.append("MERGE ");
        builder.append(instanceRelation.toCypherExplicit());
        builder.append(NEW_LINE);
    }
}
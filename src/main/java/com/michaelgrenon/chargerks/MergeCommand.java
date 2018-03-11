/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class MergeCommand implements Command {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private NeoGraph graph;
    
    public MergeCommand(NeoGraph graph) {
        this.graph = graph;
    }
    
    @Override
    public String toCypher() {
        StringBuilder builder = new StringBuilder();
        for (NeoConcept concept : graph.getConcepts()) {
            builder.append("MERGE ");
            builder.append(concept.toCypher());
            builder.append(NEW_LINE);
        }
        
        for (NeoRelation relation : graph.getRelations()) {
            builder.append("MERGE ");
            builder.append(relation.toCypherExplicit());
            builder.append(NEW_LINE);
        }
        return builder.toString();
    }
}

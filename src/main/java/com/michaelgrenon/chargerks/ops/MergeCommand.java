/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.ops;

import java.util.concurrent.TimeUnit;

import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;
import com.michaelgrenon.chargerks.NeoRelationBinding;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.summary.SummaryCounters;

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
        for (NeoConceptBinding binding : graph.getConcepts()) {
            builder.append("MERGE ");
            builder.append(binding.toCypher());
            builder.append(NEW_LINE);
        }
        
        for (NeoRelationBinding relationBinding : graph.getRelations()) {
            NeoRelation relation = relationBinding.getRelation();
            builder.append("MERGE ");
            builder.append(relation.toCypherExplicit());
            builder.append(NEW_LINE);
        }
        return builder.toString();
    }

    @Override
	public String getSummary(StatementResult result) {
        ResultSummary summary = result.consume();
        SummaryCounters counts = summary.counters();
        return String.format("Created %d nodes and %d relationships in %d ms.", counts.nodesCreated(), counts.relationshipsCreated(), summary.resultAvailableAfter(TimeUnit.MILLISECONDS));
    }
}

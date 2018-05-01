/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.ops;

import java.util.concurrent.TimeUnit;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.summary.SummaryCounters;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class DeleteCommand implements Command {
    String contextName;

    public DeleteCommand(String contextName) {
        this.contextName = contextName;
    }

	@Override
	public String toCypher() {
        StringBuilder builder = new StringBuilder();
        builder.append("MATCH ");
        if (contextName != null) {
            builder.append("(n {contextName: '");
            builder.append(contextName);
            builder.append("'}) ");
        } else {
            builder.append("(n) ");
        }

        builder.append("DETACH DELETE n");
        return builder.toString();
    }
    
    @Override
    public String getSummary(StatementResult result) {
        ResultSummary summary = result.consume();
        SummaryCounters counts = summary.counters();
        return String.format("Deleted %d nodes and %d relationships in %d ms.", counts.nodesDeleted(), counts.relationshipsDeleted(), summary.resultAvailableAfter(TimeUnit.MILLISECONDS));
    }
}
package com.michaelgrenon.chargerks;

import charger.obj.Concept;
import charger.obj.Graph;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

public class SubgraphAnswer implements Answer {
    
    @Override
    public Graph fromResult(StatementResult result) {
        Record record = result.peek();
        boolean foundMatch = record.get("foundMatch").asBoolean();
        Graph graph = new Graph();
        Concept resultConcept = new Concept();
        resultConcept.setTypeLabel("Answer");
        resultConcept.setReferent(foundMatch ? "Yes" : "No");
        resultConcept.resizeIfNecessary();
        graph.insertObject(resultConcept);
        return graph;
    }
}

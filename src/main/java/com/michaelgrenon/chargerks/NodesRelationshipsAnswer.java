package com.michaelgrenon.chargerks;

import charger.obj.Arrow;
import charger.obj.Concept;
import charger.obj.GEdge;
import charger.obj.Graph;
import charger.obj.Relation;
import java.util.HashMap;
import java.util.List;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

public class NodesRelationshipsAnswer implements Answer {

    @Override
    public NeoGraph fromResult(StatementResult result) {
        Record record = result.single();

        Value nodesValue = record.get("nodes");
        Value relationshipsValue = record.get("relationships");

        List<Node> nodes = nodesValue.asList(n -> n.asNode());
        List<Relationship> relationships = relationshipsValue.asList(r -> r.asRelationship());

        return new NeoGraph(nodes, relationships);
    }
}

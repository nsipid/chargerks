package com.michaelgrenon.chargerks.ops;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoActorDag;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;
import com.michaelgrenon.chargerks.NeoRelationBinding;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import cgif.generate.NameGenerator;

public class NodesRelationshipsAnswer implements Answer {

    @Override
    public Collection<NeoGraph> fromResult(StatementResult result) {
        Record record = result.single();

        Value nodesValue = record.get("nodes");
        Value relationshipsValue = record.get("relationships");

        List<Node> nodes = nodesValue.asList(n -> n.asNode());
        List<Relationship> relationships = relationshipsValue.asList(r -> r.asRelationship());

        return Collections.singletonList(fromNodesRels(nodes, relationships));
    }

    private NeoGraph fromNodesRels(List<Node> nodes, List<Relationship> relationships) {
        HashMap<Long, NeoConceptBinding> neoConcepts = new HashMap<Long, NeoConceptBinding>();
        HashMap<Long, NeoRelationBinding> neoRelations = new HashMap<Long, NeoRelationBinding>();
        NameGenerator generator = new NameGenerator();

        for (Node node : nodes) {
            Long id = node.id();
            if (!neoConcepts.containsKey(id)) {
                Value referentValue = node.get("referent");
                Value contextTypeValue = node.get("contextType");
                Value contextNameValue = node.get("contextName");
                
                String type = "T";
                Iterator<String> itr = node.labels().iterator();
                if (itr.hasNext()) {
                    type = itr.next();
                }

                String referent = referentValue.isNull() ? "" : referentValue.asString();
                ContextType contextType = contextTypeValue.isNull() ? ContextType.UNIVERSE : ContextType.valueOf(contextTypeValue.asString());
                String contextName = contextNameValue.isNull() ? "" : contextNameValue.asString();

                NeoConceptBinding neoConcept = new NeoConceptBinding(generator.generateName(), new NeoConcept(type, referent, new ContextInfo(contextType, contextName)));
                neoConcepts.put(id, neoConcept);
            }
        }

        for (Relationship relationship : relationships) {
            Long id = relationship.id();
            if (!neoRelations.containsKey(id)) {
                Value contextTypeValue = relationship.get("contextType");
                Value contextNameValue = relationship.get("contextName");

                ContextType contextType = contextTypeValue.isNull() ? ContextType.UNIVERSE : ContextType.valueOf(contextTypeValue.asString());
                String contextName = contextNameValue.isNull() ? "" : contextNameValue.asString();

                NeoConceptBinding startNode = neoConcepts.get(relationship.startNodeId());
                NeoConceptBinding endNode = neoConcepts.get(relationship.endNodeId());

                NeoRelationBinding neoRelation = new NeoRelationBinding(generator.generateName(), new NeoRelation(startNode, endNode, new ContextInfo(contextType, contextName), relationship.type()));
                neoRelations.put(id, neoRelation);
            }
        }

        return new NeoGraph(neoConcepts.values(), neoRelations.values(), new NeoActorDag());
    }
}

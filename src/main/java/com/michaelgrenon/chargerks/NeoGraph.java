/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import cgif.generate.NameGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

/**
 *
 * @author GrenonMP
 */
public class NeoGraph {
    private Collection<NeoConcept> concepts;
    private Collection<NeoRelation> relations;
    
    public NeoGraph(Collection<NeoConcept> concepts, Collection<NeoRelation> relations) {
        this.concepts = new ArrayList<NeoConcept>(concepts);
        this.relations = new ArrayList<NeoRelation>(relations);
    }

    public NeoGraph(List<Node> nodes, List<Relationship> relationships) {
        HashMap<Long, NeoConcept> neoConcepts = new HashMap<Long, NeoConcept>();
        HashMap<Long, NeoRelation> neoRelations = new HashMap<Long, NeoRelation>();
        NameGenerator generator = new NameGenerator();

        for (Node node : nodes) {
            Long id = node.id();
            if (!neoConcepts.containsKey(id)) {
                Value referentValue = node.get("properties").get("referent");
                Value contextTypeValue = node.get("properties").get("contextType");
                Value contextNameValue = node.get("properties").get("contextName");
                
                String type = "T";
                Iterator<String> itr = node.labels().iterator();
                if (itr.hasNext()) {
                    type = itr.next();
                }

                String referent = referentValue.isNull() ? "" : referentValue.asString();
                ContextType contextType = contextTypeValue.isNull() ? ContextType.UNIVERSE : ContextType.valueOf(contextTypeValue.asString());
                String contextName = contextNameValue.isNull() ? "" : contextNameValue.asString();

                NeoConcept neoConcept = new NeoConcept(generator.generateName(), type, referent, new ContextInfo(contextType, contextName));
                neoConcepts.put(id, neoConcept);
            }
        }

        for (Relationship relationship : relationships) {
            Long id = relationship.id();
            if (!neoRelations.containsKey(id)) {
                Value contextTypeValue = relationship.get("properties").get("contextType");
                Value contextNameValue = relationship.get("properties").get("contextName");

                ContextType contextType = contextTypeValue.isNull() ? ContextType.UNIVERSE : ContextType.valueOf(contextTypeValue.asString());
                String contextName = contextNameValue.isNull() ? "" : contextNameValue.asString();

                NeoConcept startNode = neoConcepts.get(relationship.startNodeId());
                NeoConcept endNode = neoConcepts.get(relationship.endNodeId());

                NeoRelation neoRelation = new NeoRelation(startNode, endNode, new ContextInfo(contextType, contextName), relationship.type());
                neoRelations.put(id, neoRelation);
            }
        }

        this.concepts = neoConcepts.values();
        this.relations = neoRelations.values();
    }
    
    public List<NeoConcept> getConcepts() {
        return concepts.stream().collect(Collectors.toList());
    }
    
    public List<NeoRelation> getRelations() {
        return relations.stream().collect(Collectors.toList());
    }
}

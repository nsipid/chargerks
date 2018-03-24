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
    private Collection<NeoConceptBinding> concepts;
    private Collection<NeoRelation> relations;
    
    public NeoGraph(Collection<NeoConceptBinding> concepts, Collection<NeoRelation> relations) {
        this.concepts = new ArrayList<NeoConceptBinding>(concepts);
        this.relations = new ArrayList<NeoRelation>(relations);
    }

    public NeoGraph(List<Node> nodes, List<Relationship> relationships) {
        HashMap<Long, NeoConceptBinding> neoConcepts = new HashMap<Long, NeoConceptBinding>();
        HashMap<Long, NeoRelation> neoRelations = new HashMap<Long, NeoRelation>();
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

                NeoRelation neoRelation = new NeoRelation(startNode, endNode, new ContextInfo(contextType, contextName), relationship.type());
                neoRelations.put(id, neoRelation);
            }
        }

        this.concepts = neoConcepts.values();
        this.relations = neoRelations.values();
    }
    
    public List<NeoConceptBinding> getConcepts() {
        return concepts.stream().collect(Collectors.toList());
    }
    
    public List<NeoRelation> getRelations() {
        return relations.stream().collect(Collectors.toList());
    }
}

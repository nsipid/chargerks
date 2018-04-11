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
    private Collection<NeoRelationBinding> relations;
    private NeoActorDag actors;
    
    public NeoGraph(Collection<NeoConceptBinding> concepts, Collection<NeoRelationBinding> relations, NeoActorDag actors) {
        this.concepts = new ArrayList<NeoConceptBinding>(concepts);
        this.relations = new ArrayList<NeoRelationBinding>(relations);
        this.actors = actors;
    }
    
    public List<NeoConceptBinding> getConcepts() {
        return concepts.stream().collect(Collectors.toList());
    }
    
    public List<NeoRelationBinding> getRelations() {
        return relations.stream().collect(Collectors.toList());
    }

    public NeoActorDag getActors() {
        return actors;
    }
}

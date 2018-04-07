/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.cg;

import static charger.EditToolbar.Mode.Concept;

import cgif.generate.NameGenerator;
import charger.obj.Concept;
import charger.obj.Coref;
import charger.obj.DeepIterator;
import charger.obj.GEdge;
import charger.obj.GEdge.Direction;
import charger.obj.GNode;
import charger.obj.Graph;
import charger.obj.GraphObject;
import charger.obj.GraphObjectIterator;
import charger.obj.Relation;
import charger.obj.ShallowIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoConcept;

/**
 *
 * @author GrenonMP
 */
public class ContextCrossReferences {
    private class Key {
        public Key(ContextInfo to, ContextInfo from) {
            this.to = to;
            this.from = from;
        }
        private ContextInfo from;
        private ContextInfo to;
        @Override
        public int hashCode() {
            int hash = 5;
            return hash;
        }
    
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Key other = (Key) obj;
            if (!Objects.equals(this.from, other.from)) {
                return false;
            }
            if (!Objects.equals(this.to, other.to)) {
                return false;
            }
            return true;
        }
    }

    Graph universe;
    HashMap<Key, ContextCrossReference> crossReferences = new HashMap<>();
    HashSet<String> insertedObjectIds = new HashSet<>();
    NameGenerator nameGenerator;
    
    public ContextCrossReferences(Graph universe) {
        this.universe = universe;
        extractCrossReferences();
    }
    
    public ContextCrossReference getOrAddCrossReference(
            ContextInfo from, ContextInfo to, Graph fromGraph, Graph toGraph) {
        Key key = new Key(to, from);
        if (!crossReferences.containsKey(key)) {
            ContextCrossReference crossRef = new ContextCrossReference(from, to, fromGraph, toGraph);
            crossReferences.put(key, crossRef);
            crossRef.insertCoref();
        }
        
        return crossReferences.get(key);
    }

    public Set<NeoConcept> getReferencedConcepts () {
        return crossReferences.values().stream().map(refs -> refs.getReferencedConcepts())
            .flatMap(m -> m.stream())
            .collect(Collectors.toSet());
    }

    public NeoConcept getReferencedConcept(String cloneId) {
        for (ContextCrossReference cRef : crossReferences.values()) {
            NeoConcept referenced = cRef.getReferencedConcept(cloneId);
            if (referenced != null) {
                return referenced;
            }
        }

        return null;
    }
    
    public Set<String> getAllIds() {
        return crossReferences.values().stream()
                .flatMap(c -> c.getAllIds().stream())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private void extractCrossReferences() {
        GraphObjectIterator crossRefItr = new DeepIterator(universe, new Relation());
        while(crossRefItr.hasNext()) {
            Relation relation = (Relation) crossRefItr.next();
            if (relation.getTextLabel().toUpperCase().equals("IN")) {
                Concept corefConcept = null;
                for (Object node : relation.getLinkedNodes(GEdge.Direction.TO)) {
                    if (node instanceof Concept) {
                        corefConcept = (Concept) node;
                        break;
                    }
                }
                if (corefConcept != null) {
                    Graph fromGraph = corefConcept.ownerGraph;
                    ContextInfo from = new ContextInfo(ContextType.valueOf(fromGraph.getTypeLabel().toUpperCase(Locale.US)), fromGraph.getReferent());
                    ContextInfo to = new ContextInfo(ContextType.valueOf(corefConcept.getTypeLabel().toUpperCase(Locale.US)), corefConcept.getReferent());
                    Key key = new Key(to, from);

                    // creating a new crossreference will backtrack the "in" relations, so only do it once
                    if (!crossReferences.containsKey(key)) {
                        ContextCrossReference crossReference = new ContextCrossReference(corefConcept);
                        crossReferences.put(key, crossReference);
                    }
                }
            }
        }
    }
}

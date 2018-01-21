/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import static charger.EditToolbar.Mode.Concept;

import cgif.generate.NameGenerator;
import charger.obj.Concept;
import charger.obj.Coref;
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
    
    public ContextCrossReferences(Graph universe, NameGenerator nameGenerator) {
        this.universe = universe;
        this.nameGenerator = nameGenerator;
        extractCrossReferences();
    }

    public ContextCrossReferences(Graph universe) {
        this(universe, new NameGenerator());
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

    public Map<String, Concept> getCloneMap () {
        return crossReferences.values().stream().map(refs -> refs.getCloneMap())
            .flatMap(m -> m.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    public Set<String> getAllIds() {
        return crossReferences.values().stream()
                .flatMap(c -> c.getAllIds().stream())
                .collect(Collectors.toCollection(HashSet::new));
    }
    
    private void extractCrossReferences() {
        GraphObjectIterator corefItr = new ShallowIterator(universe, new Coref());
        while(corefItr.hasNext()) {
            Coref coref = (Coref) corefItr.next();
            ContextCrossReference crossReference = new ContextCrossReference(coref, nameGenerator);
            crossReferences.put(new Key(crossReference.getTo(), crossReference.getFrom()), crossReference);
        }
    }
}

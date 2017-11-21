/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import static charger.EditToolbar.Mode.Concept;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author GrenonMP
 */
public class ContextCrossReferences {
    Graph universe;
    HashSet<ContextCrossReference> crossReferences = new HashSet<>();
    HashSet<String> insertedObjectIds = new HashSet<>();
    
    public ContextCrossReferences(Graph universe) {
        this.universe = universe;
        extractCrossReferences();
    }
    
    public ContextCrossReference getOrAddCrossReference(
            ContextInfo from, ContextInfo to, Graph fromGraph, Graph toGraph) {
        
        ContextCrossReference crossRef = new ContextCrossReference(from, to, fromGraph, toGraph);
        if (!crossReferences.contains(crossRef)) {
            crossReferences.add(crossRef);
            crossRef.insertCoref();
        }
        
        return crossRef;
    }
    
    public Set<String> getAllIds() {
        return crossReferences.stream()
                .flatMap(c -> c.getAllIds().stream())
                .collect(Collectors.toCollection(HashSet::new));
    }
    
    private void extractCrossReferences() {
        GraphObjectIterator corefItr = new ShallowIterator(universe, new Coref());
        while(corefItr.hasNext()) {
            Coref coref = (Coref) corefItr.next();
            ContextCrossReference crossReference = new ContextCrossReference(coref);
            crossReferences.add(crossReference);
        }
    }
}

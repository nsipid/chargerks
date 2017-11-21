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
    
    public ContextCrossReferences(Graph universe) {
        this.universe = universe;
    }
    
    public ContextCrossReference getOrAddCrossReference(
            ContextInfo from, ContextInfo to, Graph fromGraph, Graph toGraph) {
        
        ContextCrossReference crossRef = new ContextCrossReference(from, to, fromGraph, toGraph);
        if (!crossReferences.contains(crossRef)) {
            crossReferences.add(crossRef);
            Graph chargerContext = crossRef.getFromGraph();
            
            crossRef.getCorefConcept().setCenter(fromGraph.getCenter());
            chargerContext.insertObject(crossRef.getCorefConcept());
            Coref coref = new Coref(crossRef.getCorefConcept(), crossRef.getToGraph());
            universe.insertObject(coref);
        }
        
        return crossRef;
    }
    
    private void extractCrossReferences() {
        GraphObjectIterator corefItr = new ShallowIterator(universe, new Coref());
        while(corefItr.hasNext()) {
            Coref coref = (Coref) corefItr.next();
            
            Concept corefConcept = (Concept) coref.fromObj;
            Graph toGraph = (Graph) coref.toObj;
            Graph fromGraph = corefConcept.ownerGraph;
            ContextInfo to = new ContextInfo(ContextType.valueOf(toGraph.getTypeLabel().toUpperCase(Locale.US)), toGraph.getReferent());
            ContextInfo from = new ContextInfo(ContextType.valueOf(fromGraph.getTypeLabel().toUpperCase(Locale.US)), fromGraph.getReferent());
            
            ArrayList<GNode> linkedNodes = corefConcept.getLinkedNodes(Direction.FROM);
            
            List<Concept> crossReferenceConcepts = linkedNodes.stream()
                    .filter(Relation.class::isInstance)
                    .filter(r -> r.getTextLabel().toUpperCase() == "IN")
                    .flatMap(node -> node.getLinkedNodes(Direction.FROM).stream())
                    .filter(Concept.class::isInstance)
                    .map(Concept.class::cast)
                    .collect(Collectors::toList);
            
            ContextCrossReference crossReference = new ContextCrossReference(from, to, fromGraph, toGraph, crossReferenceConcepts);
            crossReferences.add(crossReference);
            
        }
    }
}

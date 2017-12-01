/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import charger.obj.Arrow;
import charger.obj.Concept;
import charger.obj.Coref;
import charger.obj.GEdge;
import charger.obj.GNode;
import charger.obj.Graph;
import charger.obj.GraphObject;
import charger.obj.Relation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class ContextCrossReference {
    private ContextInfo from;
    private Graph fromGraph;

    public Graph getFromGraph() {
        return fromGraph;
    }

    public Graph getToGraph() {
        return toGraph;
    }
    private Graph toGraph;
    
    private Concept corefConcept;
    
    private Coref coref;

    public Concept getCorefConcept() {
        return corefConcept;
    }
    private ArrayList<Concept> referencedConcepts = new ArrayList<Concept>();
    private HashSet<String> insertedIds = new HashSet<String>();
    
    public Concept referenceConcept(Concept conceptToClone) {
        Optional<Concept> match = referencedConcepts.stream()
                .filter(c -> c.getReferent() == conceptToClone.getReferent() && c.getTypeLabel() == c.getTypeLabel()).findAny();
        if (!match.isPresent()) {
            Concept clone = new Concept();
            clone.setReferent(conceptToClone.getReferent());
            clone.setTypeLabel(conceptToClone.getTypeLabel());
            Relation relation = new Relation();
            relation.setTextLabel("in");
            
            referencedConcepts.add(clone);
            
            GEdge edge1 = new Arrow(clone, relation);
            GEdge edge2 = new Arrow(relation, corefConcept);

            clone.setCenter(fromGraph.getCenter());
            relation.setCenter(fromGraph.getCenter());
            edge1.setCenter(fromGraph.getCenter());
            edge2.setCenter(fromGraph.getCenter());

            insertObject(fromGraph, clone);
            insertObject(fromGraph, relation);
            insertObject(fromGraph, edge1);
            insertObject(fromGraph, edge2); 
            
            return clone;
        } else {
            return match.get();
        }
    }
    
    public Set<String> getAllIds() {
        return insertedIds;
    }
    
    public void insertCoref() {
        corefConcept.setCenter(fromGraph.getCenter()); 
        insertObject(fromGraph, corefConcept);
        insertObject(fromGraph.ownerGraph, coref);
    }
    
    private void insertObject(Graph graph, GraphObject obj) {
        String id = obj.objectID.toString();
        if (!insertedIds.contains(id)) {
            insertedIds.add(id);
            graph.insertObject(obj);
        }
    }
    
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
        final ContextCrossReference other = (ContextCrossReference) obj;
        if (!Objects.equals(this.from, other.from)) {
            return false;
        }
        if (!Objects.equals(this.to, other.to)) {
            return false;
        }
        return true;
    }

    public ContextInfo getFrom() {
        return from;
    }

    public ContextInfo getTo() {
        return to;
    }
    private ContextInfo to;
    
    public ContextCrossReference(ContextInfo from, ContextInfo to, Graph fromGraph, Graph toGraph) {
        this.from = from;
        this.to = to;
        
        this.fromGraph = fromGraph;
        this.toGraph = toGraph;
        
        this.corefConcept = new Concept();
        this.corefConcept.setReferent(this.to.getName());
        this.corefConcept.setTypeLabel(this.to.getType().toString());

        this.coref = new Coref(this.corefConcept, toGraph);
    }
    
    public ContextCrossReference(Coref existingCoref) {
        this.coref = existingCoref;
        corefConcept = (Concept) coref.fromObj;
        toGraph = (Graph) coref.toObj;
        fromGraph = corefConcept.ownerGraph;
        to = new ContextInfo(ContextType.valueOf(toGraph.getTypeLabel().toUpperCase(Locale.US)), toGraph.getReferent());
        from = new ContextInfo(ContextType.valueOf(fromGraph.getTypeLabel().toUpperCase(Locale.US)), fromGraph.getReferent());

        ArrayList<GNode> linkedNodes = corefConcept.getLinkedNodes(GEdge.Direction.FROM);
        insertedIds.add(corefConcept.objectID.toString());

        List<Relation> relations = linkedNodes.stream()
                .filter(Relation.class::isInstance)
                .filter(rel -> "IN".equals(rel.getTextLabel().toUpperCase(Locale.US)))
                .map(Relation.class::cast)
                .collect(Collectors.toList());
        
        for (Relation inRel : relations) {
            insertedIds.add(inRel.objectID.toString());
 
            for (Object node : inRel.getLinkedNodes(GEdge.Direction.FROM)) {
                if (node instanceof Concept) {
                    Concept concept = (Concept) node;
                    insertedIds.add(concept.objectID.toString());
                    referencedConcepts.add(concept);
                }
            }
        }
    }
    
}

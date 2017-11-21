/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import charger.obj.Arrow;
import charger.obj.Concept;
import charger.obj.GEdge;
import charger.obj.Graph;
import charger.obj.Relation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public Concept getCorefConcept() {
        return corefConcept;
    }
    private ArrayList<Concept> crossReferenceConcepts = new ArrayList<Concept>();
    
    public Concept getOrAddReferencedConcept(Concept conceptToClone) {
        Optional<Concept> match = crossReferenceConcepts.stream()
                .filter(c -> c.getReferent() == conceptToClone.getReferent() && c.getTypeLabel() == c.getTypeLabel()).findAny();
        if (!match.isPresent()) {
            Concept clone = new Concept();
            clone.setReferent(conceptToClone.getReferent());
            clone.setTypeLabel(conceptToClone.getTypeLabel());
            crossReferenceConcepts.add(clone);
            
            Relation relation = new Relation();
            relation.setTextLabel("in");
            GEdge edge1 = new Arrow(clone, relation);
            GEdge edge2 = new Arrow(relation, corefConcept);
            
            clone.setCenter(fromGraph.getCenter());
            relation.setCenter(fromGraph.getCenter());
            edge1.setCenter(fromGraph.getCenter());
            edge2.setCenter(fromGraph.getCenter());
            
            fromGraph.insertObject(clone);
            fromGraph.insertObject(relation);
            fromGraph.insertObject(edge1);
            fromGraph.insertObject(edge2);
            
            return clone;
        } else {
            return match.get();
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
        this.corefConcept = new Concept();
        this.corefConcept.setReferent(this.to.getName());
        this.corefConcept.setTypeLabel(this.to.getType().toString());
    }
    
    public ContextCrossReference(ContextInfo from, ContextInfo to, Graph fromGraph, Graph toGraph, List<Concept> existingCrossReferenceConcepts) {
        this(from, to, fromGraph, toGraph);
        
        this.crossReferenceConcepts = new ArrayList<Concept>(existingCrossReferenceConcepts);
    }
    
}

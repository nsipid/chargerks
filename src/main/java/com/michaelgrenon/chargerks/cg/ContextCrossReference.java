/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.cg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoConcept;

import charger.obj.Arrow;
import charger.obj.Concept;
import charger.obj.Coref;
import charger.obj.GEdge;
import charger.obj.GNode;
import charger.obj.Graph;
import charger.obj.GraphObject;
import charger.obj.Relation;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class ContextCrossReference {
    private ContextInfo from;
    private Graph fromGraph;
    
    private Concept corefConcept;
    
    private Coref coref;

    public Concept getCorefConcept() {
        return corefConcept;
    }

    public Set<NeoConcept> getReferencedConcepts() {
        return neoConceptToClone.keySet();
    }

    public NeoConcept getReferencedConcept(String cloneId) {
        return cloneIdToNeoConcept.get(cloneId);
    }

    private HashMap<NeoConcept, Concept> neoConceptToClone = new HashMap<NeoConcept, Concept>();
    private HashMap<String, NeoConcept> cloneIdToNeoConcept = new HashMap<String, NeoConcept>();

    private HashSet<String> insertedIds = new HashSet<String>();

    public Concept referenceConcept(NeoConcept conceptToClone) {
        if (!neoConceptToClone.containsKey(conceptToClone))  {
            Concept clone = new Concept();
            clone.setReferent(conceptToClone.getReferent().orElse(""));          
            clone.setTypeLabel(conceptToClone.getType());

            Relation relation = new Relation();
            relation.setTextLabel("in");
            
            neoConceptToClone.put(conceptToClone, clone);
            cloneIdToNeoConcept.put(clone.objectID.toString(), conceptToClone);
            
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
            return neoConceptToClone.get(conceptToClone);
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
        
        this.corefConcept = new Concept();
        this.corefConcept.setReferent(this.to.getName());
        this.corefConcept.setTypeLabel(this.to.getType().toString());

        this.coref = new Coref(this.corefConcept, toGraph);
    }

    public ContextCrossReference(Concept corefConcept) {
        this.fromGraph = corefConcept.ownerGraph;
        this.from = new ContextInfo(ContextType.valueOf(fromGraph.getTypeLabel().toUpperCase(Locale.US)), fromGraph.getReferent());
        this.to = new ContextInfo(ContextType.valueOf(corefConcept.getTypeLabel().toUpperCase(Locale.US)), corefConcept.getReferent());

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
                    Concept clone = (Concept) node;
                    NeoConcept neoConcept = new NeoConcept(clone.getTypeLabel(), clone.getReferent(), to);

                    neoConceptToClone.put(neoConcept, clone);
                    cloneIdToNeoConcept.put(clone.objectID.toString(), neoConcept);
                    insertedIds.add(clone.objectID.toString());
                }
            }
        }
    }   
}

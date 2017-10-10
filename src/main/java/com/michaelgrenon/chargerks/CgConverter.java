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
import charger.obj.Graph;
import charger.obj.GraphObject;
import charger.obj.Relation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author GrenonMP
 */
public class CgConverter {
    public static NeoGraph chargerToNeo(Graph charger) {
        return null;
    }
    
    public static Graph neoToCharger(NeoGraph neo) {
       Graph universeGraph = new Graph();
       Map<NeoConcept, Concept> conceptLookup = 
               neo.getConcepts().stream()
                       .collect(Collectors.toMap(n -> n, CgConverter::neoConceptToCharger));
       
       Map<ContextInfo, Graph> chargerContexts = 
               neo.getConcepts().stream().collect(
                       Collectors.collectingAndThen(
                               Collectors.groupingBy(NeoConcept::getContext),
                               n -> neoContextsToCharger(n, conceptLookup)));
       
       chargerContexts.values().stream().forEach(c -> universeGraph.insertObject(c));
       
       addNeoRelationsToCharger(neo.getRelations(), universeGraph, chargerContexts, conceptLookup);
       
       return universeGraph;
    }
    
    private static Concept neoConceptToCharger(NeoConcept neo) {
        Concept concept = new Concept();
        concept.setTypeLabel(neo.getType());
        concept.setReferent(neo.getReferent().orElse(""));
        concept.resizeIfNecessary();
        return concept;
    }
    
    private static void addNeoRelationsToCharger(List<NeoRelation> neoRelations,
            Graph universe, Map<ContextInfo, Graph> chargerContexts,
            Map<NeoConcept, Concept> conceptLookup) {
        
        HashSet<ContextCrossReference> crossReferences = new HashSet<>();
        
        for (NeoRelation neo : neoRelations) {
            Concept conceptA = conceptLookup.get(neo.getConcept1());
            Concept conceptB = conceptLookup.get(neo.getConcept2());

            ContextInfo ctxA = neo.getConcept1().getContext();
            ContextInfo ctxB = neo.getConcept2().getContext();
            ContextInfo ctxRel = neo.getContext();
            
            Graph relCtxGraph = chargerContexts.get(neo.getContext());
            Graph ctxAGraph = chargerContexts.get(ctxA);
            Graph ctxBGraph = chargerContexts.get(ctxA);
            
            if (ctxA.equals(ctxB) && ctxB.equals(ctxRel)) {
                //if relation and concepts all in the same context
                addRelationToGraph(conceptA, conceptB, neo.getLabel(), relCtxGraph);
            }  else if (ctxRel.equals(ctxA) && !ctxA.equals(ctxB)) {
                //else if relation and concept 1 in same context but not concept 2
                ContextCrossReference relToB = getOrAddCrossReference(universe, crossReferences, chargerContexts, ctxRel, ctxB, relCtxGraph, ctxBGraph);
                Concept cloneB = relToB.getOrAddReferencedConcept(conceptB);
                
                addRelationToGraph(conceptA, cloneB, neo.getLabel(), relCtxGraph);
            } else if (ctxRel.equals(ctxB) && !ctxB.equals(ctxA)) {
                //else if relation and concept 2 in same context but not concept 1
                ContextCrossReference relToA = getOrAddCrossReference(universe, crossReferences, chargerContexts, ctxRel, ctxA, relCtxGraph, ctxAGraph);
                Concept cloneA = relToA.getOrAddReferencedConcept(conceptA);
                addRelationToGraph(cloneA, conceptB, neo.getLabel(), relCtxGraph);
            } else {
                //else if relation and concepts all in different contexts
                //else if concept 1 and 2 in the same context but not with the relation
                ContextCrossReference relToA = getOrAddCrossReference(universe, crossReferences, chargerContexts, ctxRel, ctxA, relCtxGraph, ctxAGraph);
                ContextCrossReference relToB = getOrAddCrossReference(universe, crossReferences, chargerContexts, ctxRel, ctxB, relCtxGraph, ctxBGraph);
                
                Concept cloneA = relToA.getOrAddReferencedConcept(conceptA);
                Concept cloneB = relToB.getOrAddReferencedConcept(conceptB);
                
                addRelationToGraph(cloneA, cloneB, neo.getLabel(), relCtxGraph);
            }
        }
    }
    
    private static void addRelationToGraph(Concept conceptA, Concept conceptB, String relationLabel, Graph graph) {
        Relation relation = new Relation();
        relation.setTextLabel(relationLabel);

        GEdge edge1 = new Arrow(conceptA, relation);
        GEdge edge2 = new Arrow(relation, conceptB);

        graph.insertObject(relation);
        graph.insertObject(edge1);
        graph.insertObject(edge2);
    }
    
    private static ContextCrossReference getOrAddCrossReference(Graph universe, 
            Set<ContextCrossReference> crossReferences, 
            Map<ContextInfo, Graph> chargerContexts, 
            ContextInfo to, ContextInfo from, Graph fromGraph, Graph toGraph) {
        
        ContextCrossReference crossRef = new ContextCrossReference(from, to, fromGraph, toGraph);
        if (!crossReferences.contains(crossRef)) {
            crossReferences.add(crossRef);
            Graph chargerContext = chargerContexts.get(crossRef.getFrom());
            
            chargerContext.insertObject(crossRef.getCorefConcept());
            Coref coref = new Coref(crossRef.getCorefConcept(), chargerContexts.get(crossRef.getTo()));
            universe.insertObject(coref);
        }
        
        return crossRef;
    }
    
    private static Map<ContextInfo, Graph> neoContextsToCharger(
            Map<ContextInfo, List<NeoConcept>> conceptMap, 
            Map<NeoConcept, Concept> conceptLookup) {
        
        return conceptMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, 
                e -> createChargerContextFromConcepts(e.getKey(), e.getValue(), conceptLookup)));
    }
    
    private static Graph createChargerContextFromConcepts(ContextInfo contextInfo, 
            List<NeoConcept> neoConcepts, 
            Map<NeoConcept, Concept> conceptLookup) {
        
        Graph context = new Graph();
        context.setTextLabel(contextInfo.getName());
        context.setReferent(contextInfo.getType().toString());
        neoConcepts.stream().map(n -> conceptLookup.get(n))
                .forEach(c -> context.insertObject(c));
        return context;
    }
    
}

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
import java.awt.geom.Point2D;
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
       universeGraph.setDim(3000,3000);
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
        
        ContextCrossReferences crossReferences = new ContextCrossReferences(universe);
        
        for (NeoRelation neo : neoRelations) {
            // contexts without any concepts (only relations) have not been captured
            // until this point
            Graph relCtxGraph;
            ContextInfo ctxRel = neo.getContext();
            if (!chargerContexts.containsKey(ctxRel)) {
                relCtxGraph = createChargerContextFromConcepts(ctxRel, 
                        new ArrayList<NeoConcept>(), conceptLookup);
                chargerContexts.put(ctxRel, relCtxGraph);
                universe.insertObject(relCtxGraph);
            } else {
                relCtxGraph = chargerContexts.get(neo.getContext());
            }
            
            Concept conceptA = conceptLookup.get(neo.getConcept1());
            Concept conceptB = conceptLookup.get(neo.getConcept2());

            ContextInfo ctxA = neo.getConcept1().getContext();
            ContextInfo ctxB = neo.getConcept2().getContext();
            
            Graph ctxAGraph = chargerContexts.get(ctxA);
            Graph ctxBGraph = chargerContexts.get(ctxA);
            
            if (ctxA.equals(ctxB) && ctxB.equals(ctxRel)) {
                //if relation and concepts all in the same context
                addRelationToGraph(conceptA, conceptB, neo.getLabel(), relCtxGraph);
            }  else if (ctxRel.equals(ctxA) && !ctxA.equals(ctxB)) {
                //else if relation and concept 1 in same context but not concept 2
                ContextCrossReference relToB = crossReferences.getOrAddCrossReference(ctxRel, ctxB, relCtxGraph, ctxBGraph);
                Concept cloneB = relToB.referenceConcept(conceptB);
                
                addRelationToGraph(conceptA, cloneB, neo.getLabel(), relCtxGraph);
            } else if (ctxRel.equals(ctxB) && !ctxB.equals(ctxA)) {
                //else if relation and concept 2 in same context but not concept 1
                ContextCrossReference relToA = crossReferences.getOrAddCrossReference(ctxRel, ctxA, relCtxGraph, ctxAGraph);
                Concept cloneA = relToA.referenceConcept(conceptA);
                addRelationToGraph(cloneA, conceptB, neo.getLabel(), relCtxGraph);
            } else {
                //else if relation and concepts all in different contexts
                //else if concept 1 and 2 in the same context but not with the relation
                ContextCrossReference relToA = crossReferences.getOrAddCrossReference(ctxRel, ctxA, relCtxGraph, ctxAGraph);
                ContextCrossReference relToB = crossReferences.getOrAddCrossReference(ctxRel, ctxB, relCtxGraph, ctxBGraph);
                
                Concept cloneA = relToA.referenceConcept(conceptA);
                Concept cloneB = relToB.referenceConcept(conceptB);
                
                addRelationToGraph(cloneA, cloneB, neo.getLabel(), relCtxGraph);
            }
        }
    }
    
    private static void addRelationToGraph(Concept conceptA, Concept conceptB, 
            String relationLabel, Graph graph) {
        Relation relation = new Relation();
        relation.setTextLabel(relationLabel);

        GEdge edge1 = new Arrow(conceptA, relation);
        GEdge edge2 = new Arrow(relation, conceptB);
        
        relation.setCenter(graph.getCenter());
        edge1.setCenter(graph.getCenter());
        edge2.setCenter(graph.getCenter());
        
        graph.insertObject(relation);
        graph.insertObject(edge1);
        graph.insertObject(edge2);
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
        context.moveGraph(new Point2D.Double(640,480));
        context.setDim(640,480);
        
        context.setTypeLabel(contextInfo.getType().toString());
        context.setReferent(contextInfo.getName());
        neoConcepts.stream().map(n -> conceptLookup.get(n))
                .forEach(c -> {
                    c.setCenter(context.getCenter());
                    context.insertObject(c);
                    
                        });
        
        context.resizeForContents(null);

        return context;
    }
    
}

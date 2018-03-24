/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import cgif.generate.NameGenerator;
import charger.obj.Arrow;
import charger.obj.Concept;
import charger.obj.DeepIterator;
import charger.obj.GEdge;
import charger.obj.Graph;
import charger.obj.GraphObject;
import charger.obj.GraphObject.Kind;
import charger.obj.Relation;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author GrenonMP
 */
public class CgConverter {
    public static NeoGraph chargerToNeo(Graph charger) {
        NameGenerator generator = new NameGenerator();
        ContextCrossReferences crossReferences = new ContextCrossReferences(charger);
        Set<String> ignoredIds = crossReferences.getAllIds();

        Map<NeoConcept, NeoConceptBinding> unboundToBound = 
            crossReferences.getReferencedConcepts().stream().collect(Collectors.toMap(c -> c, c -> new NeoConceptBinding(generator.generateName(), c)));

        HashMap<String, NeoRelation> relations = new HashMap<String, NeoRelation>();

        Function<GraphObject, NeoConceptBinding> visitConcept = obj -> {
            NeoConcept unbound = null;
            NeoConceptBinding bound = null;
            if (obj instanceof Concept && !(obj instanceof Graph)) {
                if (ignoredIds.contains(obj.objectID.toString())) {
                    unbound = crossReferences.getReferencedConcept(obj.objectID.toString());
                } else {
                    unbound = chargerConceptToNeo((Concept) obj);
                }

                if (unbound != null) {
                    if(!unboundToBound.containsKey(unbound)) {
                        unboundToBound.put(unbound, new NeoConceptBinding(generator.generateName(), unbound));
                    }
                    bound = unboundToBound.get(unbound);
                }
            }

            return bound;
        };

        Consumer<GraphObject> visitRelation = obj -> {
            if (obj instanceof Relation && !relations.containsKey(obj.objectID.toString()) && !ignoredIds.contains(obj.objectID.toString())) {
                Relation relation = (Relation) obj;
                
                List<Concept> linkedConcepts = relation.getLinkedNodes().stream()
                        .map(Concept.class::cast)
                        .limit(2)
                        .collect(Collectors.toList());

                Concept concept1 = linkedConcepts.size() > 0 ? linkedConcepts.get(0) : null;
                Concept concept2 = linkedConcepts.size() > 1 ? linkedConcepts.get(1) : null;

                NeoConceptBinding boundConcept1 = visitConcept.apply(concept1);
                NeoConceptBinding boundConcept2 = visitConcept.apply(concept2);
                NeoRelation neoRelation = chargerRelationToNeo(relation, boundConcept1, boundConcept2);
                relations.put(obj.objectID.toString(), neoRelation);
            }
        };
                       
        DeepIterator itr = new DeepIterator(charger, Kind.GNODE);
        while (itr.hasNext()) {
            GraphObject next = itr.next();
            
            visitConcept.apply(next);
            visitRelation.accept(next);
        }
        
        return new NeoGraph(unboundToBound.values(), relations.values());
    }
    
    public static Graph neoToCharger(NeoGraph neo) {
       Graph universeGraph = new Graph();
       universeGraph.setDim(3000,3000);
       Map<NeoConceptBinding, Concept> conceptLookup = 
               neo.getConcepts().stream()
                       .collect(Collectors.toMap(n -> n, CgConverter::neoConceptToCharger, (c1, c2) -> c1));
       
       Map<ContextInfo, Graph> chargerContexts = 
               neo.getConcepts().stream().collect(
                       Collectors.collectingAndThen(
                               Collectors.groupingBy(b -> b.getConcept().getContext()),
                               n -> neoContextsToCharger(n, conceptLookup)));
       
       chargerContexts.values().stream().forEach(c -> universeGraph.insertObject(c));
       
       addNeoRelationsToCharger(neo.getRelations(), universeGraph, chargerContexts, conceptLookup);
       
       return universeGraph;
    }
    
    private static NeoConcept chargerConceptToNeo(Concept concept) {
        Graph owner = concept.getOwnerGraph();
        ContextInfo info = new ContextInfo(ContextType.valueOf(owner.getTypeLabel().toUpperCase(Locale.US)), owner.getReferent());
        NeoConcept neoConcept = new NeoConcept(concept.getTypeLabel(), concept.getReferent(), info);
        return neoConcept;
    }
    
    private static NeoRelation chargerRelationToNeo(Relation relation, NeoConceptBinding concept1, NeoConceptBinding concept2) {
        Graph owner = relation.getOwnerGraph();
        ContextInfo info = new ContextInfo(ContextType.valueOf(owner.getTypeLabel().toUpperCase(Locale.US)), owner.getReferent());
        NeoRelation neoRelation = new NeoRelation(concept1, concept2, info, relation.getTextLabel());
        return neoRelation;
    }
    
    private static Concept neoConceptToCharger(NeoConceptBinding neo) {
        Concept concept = new Concept();
        concept.setTypeLabel(neo.getConcept().getType());
        concept.setReferent(neo.getConcept().getReferent().orElse(""));
        concept.resizeIfNecessary();
        return concept;
    }
    
    private static void addNeoRelationsToCharger(List<NeoRelation> neoRelations,
            Graph universe, Map<ContextInfo, Graph> chargerContexts,
            Map<NeoConceptBinding, Concept> conceptLookup) {
        
        ContextCrossReferences crossReferences = new ContextCrossReferences(universe);
        
        for (NeoRelation neo : neoRelations) {
            // contexts without any concepts (only relations) have not been captured
            // until this point
            Graph relCtxGraph;
            ContextInfo ctxRel = neo.getContext();
            if (!chargerContexts.containsKey(ctxRel)) {
                relCtxGraph = createChargerContextFromConcepts(ctxRel, 
                        new ArrayList<NeoConceptBinding>(), conceptLookup);
                chargerContexts.put(ctxRel, relCtxGraph);
                universe.insertObject(relCtxGraph);
            } else {
                relCtxGraph = chargerContexts.get(neo.getContext());
            }
            
            Concept conceptA = conceptLookup.get(neo.getConcept1());
            Concept conceptB = conceptLookup.get(neo.getConcept2());

            NeoConcept neoConceptA = neo.getConcept1().getConcept();
            NeoConcept neoConceptB = neo.getConcept2().getConcept();

            ContextInfo ctxA = neoConceptA.getContext();
            ContextInfo ctxB = neoConceptB.getContext();
            
            Graph ctxAGraph = chargerContexts.get(ctxA);
            Graph ctxBGraph = chargerContexts.get(ctxB);
            
            if (ctxA.equals(ctxB) && ctxB.equals(ctxRel)) {
                //if relation and concepts all in the same context
                addRelationToGraph(conceptA, conceptB, neo.getLabel(), relCtxGraph);
            }  else if (ctxRel.equals(ctxA) && !ctxA.equals(ctxB)) {
                //else if relation and concept 1 in same context but not concept 2
                ContextCrossReference relToB = crossReferences.getOrAddCrossReference(ctxRel, ctxB, relCtxGraph, ctxBGraph);
                Concept cloneB = relToB.referenceConcept(neoConceptB);
                
                addRelationToGraph(conceptA, cloneB, neo.getLabel(), relCtxGraph);
            } else if (ctxRel.equals(ctxB) && !ctxB.equals(ctxA)) {
                //else if relation and concept 2 in same context but not concept 1
                ContextCrossReference relToA = crossReferences.getOrAddCrossReference(ctxRel, ctxA, relCtxGraph, ctxAGraph);
                Concept cloneA = relToA.referenceConcept(neoConceptA);
                addRelationToGraph(cloneA, conceptB, neo.getLabel(), relCtxGraph);
            } else {
                //else if relation and concepts all in different contexts
                //else if concept 1 and 2 in the same context but not with the relation
                ContextCrossReference relToA = crossReferences.getOrAddCrossReference(ctxRel, ctxA, relCtxGraph, ctxAGraph);
                ContextCrossReference relToB = crossReferences.getOrAddCrossReference(ctxRel, ctxB, relCtxGraph, ctxBGraph);
                
                Concept cloneA = relToA.referenceConcept(neoConceptA);
                Concept cloneB = relToB.referenceConcept(neoConceptB);
                
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
            Map<ContextInfo, List<NeoConceptBinding>> conceptMap, 
            Map<NeoConceptBinding, Concept> conceptLookup) {
        
        return conceptMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, 
                e -> createChargerContextFromConcepts(e.getKey(), e.getValue(), conceptLookup)));
    }
    
    private static Graph createChargerContextFromConcepts(ContextInfo contextInfo, 
            List<NeoConceptBinding> neoConcepts, 
            Map<NeoConceptBinding, Concept> conceptLookup) {
        
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

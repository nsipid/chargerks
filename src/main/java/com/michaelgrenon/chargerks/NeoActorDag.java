package com.michaelgrenon.chargerks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedList;

public class NeoActorDag implements Iterable<NeoActorBinding> {
    private Map<NeoConceptBinding, Set<NeoActorBinding>> conceptToActorAdj = new HashMap<NeoConceptBinding, Set<NeoActorBinding>>();
    private Map<NeoActorBinding, Set<NeoConceptBinding>> actorToConceptAdj = new HashMap<NeoActorBinding, Set<NeoConceptBinding>>();

    public void addEdge(NeoConceptBinding start, NeoActorBinding end) {
        if (!conceptToActorAdj.containsKey(start)) {
            conceptToActorAdj.put(start,  new HashSet<NeoActorBinding>());
        }
        
        conceptToActorAdj.get(start).add(end);
    }

    public void addEdge(NeoActorBinding start, NeoConceptBinding end) {
        if (!actorToConceptAdj.containsKey(start)) {
            actorToConceptAdj.put(start, new HashSet<NeoConceptBinding>());
        }
        
        actorToConceptAdj.get(start).add(end);
    }

    public List<NeoActorBinding> topoSort() {
        LinkedList<NeoActorBinding> sortedActors = new LinkedList<NeoActorBinding>();
        LinkedList<NeoConceptBinding> sortedConcepts = new LinkedList<NeoConceptBinding>();

        Set<NeoActorBinding> unmarkedActs = new HashSet<NeoActorBinding>(actorToConceptAdj.keySet());
        Set<NeoActorBinding> permMarkedActs = new HashSet<NeoActorBinding>();
        Set<NeoActorBinding> tempMarkedActs = new HashSet<NeoActorBinding>();

        Set<NeoConceptBinding> unmarkedCons = new HashSet<NeoConceptBinding>(conceptToActorAdj.keySet());
        Set<NeoConceptBinding> permMarkedCons = new HashSet<NeoConceptBinding>();
        Set<NeoConceptBinding> tempMarkedCons = new HashSet<NeoConceptBinding>();


        Iterator<NeoActorBinding> actsIterator = unmarkedActs.iterator();
        while (actsIterator.hasNext()) {
            NeoActorBinding taken = actsIterator.next();
            actsIterator.remove();
            traverse(actorToConceptAdj, conceptToActorAdj, permMarkedActs, tempMarkedActs, permMarkedCons, tempMarkedCons, sortedActors, sortedConcepts, taken);
        }

        Iterator<NeoConceptBinding> consIterator = unmarkedCons.iterator();
        while (consIterator.hasNext()) {
            NeoConceptBinding taken = consIterator.next();
            consIterator.remove();
            traverse(conceptToActorAdj, actorToConceptAdj, permMarkedCons, tempMarkedCons, permMarkedActs, tempMarkedActs, sortedConcepts, sortedActors, taken);
        }

        return sortedActors;
    }

    private <T, K> void traverse (Map<T,Set<K>> adjList1, Map<K,Set<T>> adjList2, Set<T> permMarked1, Set<T> tempMarked1, Set<K> permMarked2, Set<K> tempMarked2, LinkedList<T> sorted1, LinkedList<K> sorted2, T node) {
        if (permMarked1.contains(node)) {
            return;
        }
        if (tempMarked1.contains(node)) {
            throw new UnsupportedOperationException("Actor cycles are not supported.");
        } 
        tempMarked1.add(node);
        if (adjList1.containsKey(node)) {
            Iterator<K> fromNodeItr = adjList1.get(node).iterator();
            while (fromNodeItr.hasNext()) {
                traverse(adjList2, adjList1, permMarked2, tempMarked2, permMarked1, tempMarked1, sorted2, sorted1, fromNodeItr.next());
            }
        }
        permMarked1.add(node);
        sorted1.addFirst(node);
    }

	@Override
	public Iterator<NeoActorBinding> iterator() {
		return topoSort().iterator();
    }

}
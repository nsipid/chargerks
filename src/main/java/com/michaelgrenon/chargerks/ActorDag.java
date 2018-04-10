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

public class ActorDag implements Iterable<NeoActor> {
    private LinkedList<NeoActorBinding> sorted = new LinkedList<>();

    public ActorDag(Collection<NeoActorBinding> actors) {
        Map<NeoConceptBinding, Set<NeoActorBinding>> conceptToActorEdges = new HashMap<NeoConceptBinding, Set<NeoActorBinding>>();

        for (NeoActorBinding actor : actors) {
            
        }
        
    }

	@Override
	public Iterator<NeoActor> iterator() {
		return null;
    }

}
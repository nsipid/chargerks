package com.michaelgrenon.chargerks.cg;

import java.util.ArrayList;
import java.util.List;

import charger.obj.Actor;
import charger.obj.Concept;
import charger.obj.GEdge;

public class ActorInfo {
    private ArrayList<Concept> inputs = new ArrayList<Concept>();
    private ArrayList<Concept> outputs = new ArrayList<Concept>();
    private String label;

    public List<Concept> getInputs() {
        return inputs;
    }

    public List<Concept> getOutputs() {
        return outputs;
    }

    public String getLabel() {
        return label;
    }
    
    public ActorInfo(Actor actor) {
        this.label = actor.getTextLabel();
        ArrayList<GEdge> edges = actor.getEdges();

        //charger orders edges by their text label, this order must be maintained
        edges.stream().forEachOrdered(ge -> {
            if ( ge.fromObj == actor ) {
                outputs.add((Concept) ge.toObj);
            }
            if ( ge.toObj == actor ) {
                inputs.add((Concept) ge.fromObj);
            }
        });
    }
}
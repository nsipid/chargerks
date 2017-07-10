/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author GrenonMP
 */
public class NeoGraph {
    private ArrayList<NeoConcept> concepts;
    private ArrayList<NeoRelation> relations;
    
    public NeoGraph(List<NeoConcept> concepts, List<NeoRelation> relations) {
        this.concepts = new ArrayList<NeoConcept>(concepts);
        this.relations = new ArrayList<NeoRelation>(relations);
    }
    
    public List<NeoConcept> getConcepts() {
        return (List<NeoConcept>) concepts.clone();
    }
    
    public List<NeoRelation> getRelations() {
        return (List<NeoRelation>) relations.clone();
    }
}

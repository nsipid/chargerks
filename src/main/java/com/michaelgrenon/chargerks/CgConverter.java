/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import charger.obj.Concept;
import charger.obj.Graph;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author GrenonMP
 */
public class CgConverter {
    public static NeoGraph chargerToNeo(Graph charger) {

    }
    
    public static Graph neoToCharger(NeoGraph neo) {
       List<Graph> chargerContexts = new List<Graph>();
       HashMap<String,Concept> chargerConcepts = new HashMap<String,Concept>(); 
       Map<ContextInfo, List<NeoConcept>> contextMap = 
               neo.getConcepts().stream().collect(Collectors.groupingBy(NeoConcept::getContext));
       
       contextMap.stream()
    }
}

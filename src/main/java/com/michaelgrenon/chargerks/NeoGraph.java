/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import cgif.generate.NameGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;

/**
 *
 * @author GrenonMP
 */
public class NeoGraph {
    private ArrayList<NeoConcept> concepts;
    private ArrayList<NeoRelation> relations;
    
    public NeoGraph(Collection<NeoConcept> concepts, Collection<NeoRelation> relations) {
        this.concepts = new ArrayList<NeoConcept>(concepts);
        this.relations = new ArrayList<NeoRelation>(relations);
    }
    
    public List<NeoConcept> getConcepts() {
        return (List<NeoConcept>) concepts.clone();
    }
    
    public List<NeoRelation> getRelations() {
        return (List<NeoRelation>) relations.clone();
    }
    
    public static NeoGraph fromResult(StatementResult result) {
        HashMap<Number, NeoConcept> neoIdToConcept = new HashMap<Number, NeoConcept>();
        ArrayList<NeoRelation> neoRelations = new ArrayList<NeoRelation>();
        NameGenerator generator = new NameGenerator();
        while(result.hasNext()) {
            Record r = result.next();
            
            Number ida = r.get("id(a)").asNumber();
            if (!neoIdToConcept.containsKey(ida)) {
                Value referentValue = r.get("a.referent");
                Value typeValue = r.get("labels(a)");
                Value context = r.get("a.context");
                Value catalog = r.get("a.catalog");
                ContextType contextType = context.isNull() ? ContextType.INTENT : ContextType.USE;
                String contextName = context.isNull() ? catalog.asString() : context.asString();
                String referent = referentValue.isNull() ? "" : referentValue.asString();
                String type = typeValue.asList().get(0).toString();
                NeoConcept concept = new NeoConcept(generator.generateName(), type, referent, new ContextInfo(contextType, contextName));

                neoIdToConcept.put(ida, concept);
            }
            NeoConcept conceptA = neoIdToConcept.get(ida);
            
            Number idb = r.get("id(b)").asNumber();
            if (!neoIdToConcept.containsKey(idb)) {
                Value referentValue = r.get("b.referent");
                Value typeValue = r.get("labels(b)");
                Value context = r.get("b.context");
                Value catalog = r.get("b.catalog");
                ContextType contextType = context.isNull() ? ContextType.INTENT : ContextType.USE;
                String contextName = context.isNull() ? catalog.asString() : context.asString();
                String referent = referentValue.isNull() ? "" : referentValue.asString();
                String type = typeValue.asList().get(0).toString();
                NeoConcept concept = new NeoConcept(generator.generateName(), type, referent, new ContextInfo(contextType, contextName));
                neoIdToConcept.put(idb, concept);
            }
            NeoConcept conceptB = neoIdToConcept.get(idb);
            
            Value typeValue = r.get("type(r)");
            NeoRelation relation = new NeoRelation(conceptA, conceptB, typeValue.asString());
            neoRelations.add(relation);
        }
        
        return new NeoGraph(neoIdToConcept.values(), neoRelations);
    }
}

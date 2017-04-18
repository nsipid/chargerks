package com.michaelgrenon.chargerks;

import charger.obj.Arrow;
import charger.obj.Concept;
import charger.obj.GEdge;
import charger.obj.Graph;
import charger.obj.Relation;
import java.util.HashMap;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;

public class CatalogAnswer implements Answer {

    @Override
    public Graph fromResult(StatementResult result) {
        HashMap<Number, Concept> neoIdToConcept = new HashMap<Number, Concept>();
        Graph graph = new Graph();
        while(result.hasNext()) {
            Record r = result.next();
            
            Number ida = r.get("id(a)").asNumber();
            if (!neoIdToConcept.containsKey(ida)) {
                Concept concept = new Concept();
                Value referentValue = r.get("a.referent");
                Value typeValue = r.get("labels(a)");
                String referent = referentValue.isNull() ? "" : referentValue.asString();
                String type = typeValue.asList().get(0).toString();
                concept.setTypeLabel(type);
                concept.setReferent(referent);
                concept.resizeIfNecessary();
                neoIdToConcept.put(ida, concept);
                graph.insertObject(concept);
            }
            Concept conceptA = neoIdToConcept.get(ida);
            
            Number idb = r.get("id(b)").asNumber();
            if (!neoIdToConcept.containsKey(idb)) {
                Concept concept = new Concept();
                Value referentValue = r.get("b.referent");
                Value typeValue = r.get("labels(b)");
                String referent = referentValue.isNull() ? "" : referentValue.asString();
                String type = typeValue.asList().get(0).toString();
                concept.setTypeLabel(type);
                concept.setReferent(referent);
                concept.resizeIfNecessary();
                neoIdToConcept.put(idb, concept);
                graph.insertObject(concept);
            }
            Concept conceptB = neoIdToConcept.get(idb);
            
            Relation relation = new Relation();
            Value typeValue = r.get("type(r)");
            relation.setTextLabel(typeValue.asString());
            
            GEdge edge1 = new Arrow(conceptA, relation);
            GEdge edge2 = new Arrow(relation, conceptB);
            
            graph.insertObject(relation);
            graph.insertObject(edge1);
            graph.insertObject(edge2);
        }
        
        return graph;
    }
}

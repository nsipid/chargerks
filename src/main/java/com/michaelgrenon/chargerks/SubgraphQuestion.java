package com.michaelgrenon.chargerks;

import cgif.generate.NameGenerator;
import charger.obj.Concept;
import charger.obj.GEdge;
import charger.obj.GNode;
import charger.obj.Graph;
import charger.obj.GraphObjectID;
import charger.obj.Relation;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubgraphQuestion implements Question {
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    private Graph graph;
    private String catalog;
    
    public SubgraphQuestion(Graph graph, String catalog) {
        this.graph = graph;
        this.catalog = catalog;
    }
    
    @Override
    public Answer getAnswer() {
        return new SubgraphAnswer();
    }
    
    @Override
    public String toCypher() {
        StringBuilder cypherBuilder = new StringBuilder();
        
        Map<GraphObjectID, NeoConcept> idToConcept = mapNeoConcepts(graph, 
                new NameGenerator(), catalog);
        
        //MATCH concept1, concept2, ...
        cypherBuilder.append("MATCH ");
        String allConcepts = idToConcept.values().stream()
                .map(NeoConcept::toCypher)
                .collect(Collectors.joining(", "));
        cypherBuilder.append(allConcepts);
        cypherBuilder.append(NEW_LINE);
        
        //WHERE rel1 AND rel2 AND, ...
        cypherBuilder.append("WHERE ");
        String allRelations = relateNeoConcepts(graph, idToConcept)
                .map(NeoRelation::toCypher)
                .collect(Collectors.joining(" AND "));
        cypherBuilder.append(allRelations);
        cypherBuilder.append(NEW_LINE);
        
        //WITH [var1, var2, ...] AS varSet
        cypherBuilder.append("WITH [");
        String allVariables = idToConcept.values().stream()
                .map(val -> val.getVariable())
                .collect(Collectors.joining(", "));
        cypherBuilder.append(allVariables);
        cypherBuilder.append("] AS varSet");
        cypherBuilder.append(NEW_LINE);
        
        //UNWIND varSet as n
        //WITH DISTINCT n
        //RETURN count(*) = numNodes AS foundMatch
        cypherBuilder.append("UNWIND varSet as n");
        cypherBuilder.append(NEW_LINE);
        cypherBuilder.append("WITH DISTINCT n");
        cypherBuilder.append(NEW_LINE);
        cypherBuilder.append(String.format("RETURN count(*) = %d AS foundMatch",
                idToConcept.size()));
        
        return cypherBuilder.toString();
    }
    
    private static boolean isBinaryRelation(Relation rel) {
        ArrayList<GNode> tos = rel.getLinkedNodes(GEdge.Direction.TO);
        ArrayList<GNode> froms = rel.getLinkedNodes(GEdge.Direction.FROM);
        
        if (tos.size() == 1 && froms.size() == 1) {
            return true;
        }
        return false;
    }
    
    private static Map<GraphObjectID, NeoConcept> mapNeoConcepts(Graph graph, 
            NameGenerator namer, String catalog) {
        return graph.getGraphObjects().stream()
                .filter(obj -> obj instanceof Concept)
                .map(obj -> (Concept) obj)
                .collect(Collectors.toMap(c -> c.objectID, 
                        c -> new NeoConcept(namer.generateName(),
                                c.getTypeLabel(),
                                c.getReferent(),
                                catalog)));
    }
    
    private static Stream<NeoRelation> relateNeoConcepts(Graph graph, 
            Map<GraphObjectID, NeoConcept> concepts) {
        
        return graph.getGraphObjects().stream()
                .filter(obj -> obj instanceof Relation)
                .map(obj -> (Relation) obj)
                .filter(SubgraphQuestion::isBinaryRelation)
                .map(rel -> {
                    ArrayList<GNode> tos = rel.getLinkedNodes(GEdge.Direction.TO);
                    ArrayList<GNode> froms = rel.getLinkedNodes(GEdge.Direction.FROM);
                    
                    return new NeoRelation(
                            concepts.get(tos.get(0).objectID), 
                            concepts.get(froms.get(0).objectID), 
                            rel.getTextLabel());
                });
    }
}

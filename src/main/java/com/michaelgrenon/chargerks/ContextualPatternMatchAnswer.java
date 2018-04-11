package com.michaelgrenon.chargerks;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

public class ContextualPatternMatchAnswer implements Answer {

    NeoGraph template;

    public ContextualPatternMatchAnswer(NeoGraph template) {
        this.template = template;
    }

    @Override
    public Collection<NeoGraph> fromResult(StatementResult result) {
        List<NeoGraph> graphs = new LinkedList<NeoGraph>();
        result.forEachRemaining(r -> graphs.add(recordToGraph(r)));
        return graphs;
    }

    private NeoGraph recordToGraph(Record record) {
        Map<String, NeoConceptBinding> conceptMap = 
            template.getConcepts().stream().map(c -> getConcept(record, c)).collect(Collectors.toMap(b -> b.getVariable(), b -> b));
        
        List<NeoRelationBinding> relations = template.getRelations().stream().map(rel -> getRelation(record, rel, conceptMap)).collect(Collectors.toList());

        return new NeoGraph(conceptMap.values(), relations, new NeoActorDag());
    }

    private NeoConceptBinding getConcept(Record record, NeoConceptBinding template) {
        Node node = record.get(template.getVariable()).asNode();

        Value referentValue = node.get("referent");
        Value contextTypeValue = node.get("contextType");
        Value contextNameValue = node.get("contextName");
        
        String type = "T";
        Iterator<String> itr = node.labels().iterator();
        if (itr.hasNext()) {
            type = itr.next();
        }

        String referent = referentValue.isNull() ? "" : referentValue.asString();
        ContextType contextType = contextTypeValue.isNull() ? ContextType.UNIVERSE : ContextType.valueOf(contextTypeValue.asString());
        String contextName = contextNameValue.isNull() ? "" : contextNameValue.asString();

        return new NeoConceptBinding(template.getVariable(), new NeoConcept(type, referent, new ContextInfo(contextType, contextName)));
    }

    private NeoRelationBinding getRelation(Record record, NeoRelationBinding template, Map<String, NeoConceptBinding> conceptMap) {
        Relationship relationship = record.get(template.getVariable()).asRelationship();

        Value contextTypeValue = relationship.get("contextType");
        Value contextNameValue = relationship.get("contextName");

        ContextType contextType = contextTypeValue.isNull() ? ContextType.UNIVERSE : ContextType.valueOf(contextTypeValue.asString());
        String contextName = contextNameValue.isNull() ? "" : contextNameValue.asString();

        NeoConceptBinding startNode = conceptMap.get(template.getRelation().getConcept1().getVariable());
        NeoConceptBinding endNode = conceptMap.get(template.getRelation().getConcept2().getVariable());

        return new NeoRelationBinding(template.getVariable(), new NeoRelation(startNode, endNode, new ContextInfo(contextType, contextName), relationship.type()));
    }
}
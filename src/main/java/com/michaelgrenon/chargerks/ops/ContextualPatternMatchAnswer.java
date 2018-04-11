package com.michaelgrenon.chargerks.ops;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoActorDag;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;
import com.michaelgrenon.chargerks.NeoRelationBinding;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

public class ContextualPatternMatchAnswer implements Answer {

    NeoGraph template;
    private StatementResult result;
    private int counter = 0;

    public ContextualPatternMatchAnswer(NeoGraph template) {
        this.template = template;
    }
    
    @Override
    public boolean hasNext() {
        return result != null && result.hasNext();
    }

    @Override
    public NeoGraph next() {
        counter++;
        return recordToGraph(result.next());
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

	@Override
	public String getSummary() {
        ResultSummary summary = result.summary();
		return String.format("%d rows available after %d ms, consumed after another %d ms", counter, summary.resultAvailableAfter(TimeUnit.MILLISECONDS), summary.resultConsumedAfter(TimeUnit.MILLISECONDS));
	}

	@Override
	public Answer setResult(StatementResult result) {
        this.result = result;
        return this;
	}
}
package com.michaelgrenon.chargerks.ops;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

public class PatternMatchAnswer implements Answer {

    private NeoGraph template;
    private StatementResult result;
    private int counter = 0;

    public PatternMatchAnswer(NeoGraph template) {
        this.template = template;
    }

    private NeoGraph recordToGraph(Record record) {
        Map<String, NeoConceptBinding> conceptMap = 
            template.getConcepts().stream().map(c -> getConcept(record, c)).collect(Collectors.toMap(b -> b.getVariable(), b -> b));
        
        List<NeoRelationBinding> relations = template.getRelations().stream().map(rel -> getRelation(record, rel, conceptMap)).collect(Collectors.toList());

        return new NeoGraph(conceptMap.values(), relations, new NeoActorDag());
    }

    private NeoConceptBinding getConcept(Record record, NeoConceptBinding template) {
        Value referentValue = record.get(template.referToReferentAsRecord());
    

        String referent = referentValue.isNull() ? "" : referentValue.asString();


        return new NeoConceptBinding(template.getVariable(), new NeoConcept(template.getConcept().getType(), referent, template.getConcept().getContext()));
    }

    private NeoRelationBinding getRelation(Record record, NeoRelationBinding template, Map<String, NeoConceptBinding> conceptMap) {
        NeoConceptBinding startNode = conceptMap.get(template.getRelation().getConcept1().getVariable());
        NeoConceptBinding endNode = conceptMap.get(template.getRelation().getConcept2().getVariable());

        return new NeoRelationBinding(template.getVariable(), new NeoRelation(startNode, endNode, template.getRelation().getContext(), template.getRelation().getLabel()));
    }

	@Override
	public boolean hasNext() {
		return result != null && result.hasNext();
	}

	@Override
	public NeoGraph next() {
		return recordToGraph(result.next());
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
package com.michaelgrenon.chargerks.dataspace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;

import cgif.generate.NameGenerator;

public class DistanceMatrixMetadataExtractor implements MetadataExtractor {

	@Override
	public NeoGraph generateCatalog(String catalogName) throws IOException {
        ContextInfo contextOfIntent = new ContextInfo(ContextType.INTENT, catalogName);
        
        NameGenerator namer = new NameGenerator();
        NeoConceptBinding recordConcept = new NeoConceptBinding(namer.generateName(), new NeoConcept("Record", null, new ContextInfo(ContextType.INTENT, catalogName)));
        NeoConceptBinding originConcept = new NeoConceptBinding(namer.generateName(), new NeoConcept("Value", "origin", new ContextInfo(ContextType.INTENT, catalogName)));
        NeoConceptBinding destinationConcept = new NeoConceptBinding(namer.generateName(), new NeoConcept("Value", "destination", new ContextInfo(ContextType.INTENT, catalogName)));
        NeoConceptBinding durationConcept = new NeoConceptBinding(namer.generateName(), new NeoConcept("Value", "duration", new ContextInfo(ContextType.INTENT, catalogName)));
        
        List<NeoConceptBinding> concepts = new ArrayList<NeoConceptBinding>();
        concepts.add(originConcept);
        concepts.add(destinationConcept);
        concepts.add(durationConcept);

        List<NeoRelation> relations = concepts.stream()
                .map(c -> new NeoRelation(recordConcept, c, contextOfIntent, c.getConcept().getReferent().orElse("Value")))
                .collect(Collectors.toList());
        
        concepts.add(recordConcept);

        return new NeoGraph(concepts, relations);
	}

	@Override
	public List<String> generateCsvHeader() throws IOException {
        List<String> ret = new ArrayList<String>();
        ret.add("origin");
        ret.add("destination");
        ret.add("duration");
        return ret;
	}
}
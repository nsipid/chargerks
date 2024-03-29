package com.michaelgrenon.chargerks.dataspace;

import java.util.List;
import java.util.stream.Collectors;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoActorDag;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;
import com.michaelgrenon.chargerks.NeoRelationBinding;

import cgif.generate.NameGenerator;


public class BorderedTableMetadataExtractor implements MetadataExtractor {
    private BorderedTable table;
    
	public BorderedTableMetadataExtractor(BorderedTable table) {
        this.table = table;
    }

    @Override
    public NeoGraph generateCatalog(String catalogName) {
        ContextInfo contextOfIntent = new ContextInfo(ContextType.INTENT, catalogName);
                
        NameGenerator namer = new NameGenerator();
        List<NeoConceptBinding> concepts = table.getHeader().getColumnNames().stream()
                .map(s -> new NeoConceptBinding(namer.generateName(), new NeoConcept("Value", s, contextOfIntent)))
                .collect(Collectors.toList());
        
        NeoConceptBinding recordConcept = new NeoConceptBinding(namer.generateName(), new NeoConcept("Record", null, new ContextInfo(ContextType.INTENT, catalogName)));
        
        List<NeoRelationBinding> relations = concepts.stream()
                .map(c -> new NeoRelationBinding(namer.generateName(), new NeoRelation(recordConcept, c, contextOfIntent, c.getConcept().getReferent().orElse("Value"))))
                .collect(Collectors.toList());
        
        concepts.add(recordConcept);
        return new NeoGraph(concepts, relations, new NeoActorDag());
    }

    @Override
    public List<String> generateCsvHeader() {
        return table.getHeader().getColumnNames();
    }
}
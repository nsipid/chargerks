package com.michaelgrenon.chargerks.dataspace;

import cgif.generate.NameGenerator;
import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;
import java.util.List;
import java.util.stream.Collectors;


public class BorderedTableMetadataExtractor implements MetadataExtractor {
    private BorderedTable table;
    
	public BorderedTableMetadataExtractor(BorderedTable table) {
        this.table = table;
    }
    public NeoGraph generateCatalog(String catalogName) {
        ContextInfo contextOfIntent = new ContextInfo(ContextType.INTENT, catalogName);
                
        NameGenerator namer = new NameGenerator();
        List<NeoConcept> concepts = table.getHeader().getColumnNames().stream()
                .map(s -> new NeoConcept(namer.generateName(), s, null, contextOfIntent))
                .collect(Collectors.toList());
        
        NeoConcept tableConcept = new NeoConcept(namer.generateName(), "Table", "Course", new ContextInfo(ContextType.INTENT, catalogName));
        
        List<NeoRelation> relations = concepts.stream()
                .map(c -> new NeoRelation(c, tableConcept, contextOfIntent, "SCHEMA_DECLARES"))
                .collect(Collectors.toList());
        
        concepts.add(tableConcept);
        return new NeoGraph(concepts, relations);
    }
}
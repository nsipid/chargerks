/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

import cgif.generate.NameGenerator;

import com.michaelgrenon.chargerks.NeoActorDag;
import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;
import com.michaelgrenon.chargerks.NeoRelationBinding;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class CsvMetadataExtractor implements MetadataExtractor {

    CSVParser parser;
    boolean hasHeader;
    
    public CsvMetadataExtractor(Reader reader, boolean hasHeader) throws IOException {
        this.hasHeader = hasHeader;

        if (hasHeader) {
            parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        } else {
            parser = new CSVParser(reader, CSVFormat.DEFAULT);
        }
    }
    
    @Override
    public NeoGraph generateCatalog(String catalogName) throws IOException {
        ContextInfo contextOfIntent = new ContextInfo(ContextType.INTENT, catalogName);
        CSVRecord record = parser.iterator().next();
        int size = record.size();
        NameGenerator namer = new NameGenerator();
        
        List<NeoConceptBinding> concepts = null;
        if (hasHeader) {
            concepts = parser.getHeaderMap().keySet().stream()
                .map(s -> new NeoConceptBinding(namer.generateName(), new NeoConcept("Value", s, contextOfIntent)))
                .collect(Collectors.toList());
        } else {
            concepts = IntStream.range(1,size)
                .mapToObj(s -> new NeoConceptBinding(namer.generateName(), new NeoConcept("Value", "Value"+s, contextOfIntent)))
                .collect(Collectors.toList());
        }
        
        NeoConceptBinding recordConcept = new NeoConceptBinding(namer.generateName(), new NeoConcept("Record", null, new ContextInfo(ContextType.INTENT, catalogName)));
        
        List<NeoRelationBinding> relations = concepts.stream()
                .map(c -> new NeoRelationBinding(namer.generateName(), new NeoRelation(recordConcept, c, contextOfIntent, c.getConcept().getReferent().orElse("Value"))))
                .collect(Collectors.toList());
        
        concepts.add(recordConcept);
        return new NeoGraph(concepts, relations, new NeoActorDag());
    }

    @Override
    public List<String> generateCsvHeader() {
        if (hasHeader) {
            return parser.getHeaderMap().keySet().stream().collect(Collectors.toList());
        } else {
            CSVRecord record = parser.iterator().next();
            int size = record.size();
            return IntStream.range(1,size).mapToObj(s -> "Value"+s).collect(Collectors.toList());
        }
    }
    
}

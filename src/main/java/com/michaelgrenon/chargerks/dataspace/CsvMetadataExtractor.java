/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

import cgif.generate.NameGenerator;
import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;
import java.io.IOException;
import java.io.Reader;
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
    
    public CsvMetadataExtractor(Reader reader) throws IOException {
        parser = new CSVParser(reader, CSVFormat.DEFAULT);
    }
    
    @Override
    public NeoGraph generateCatalog(String catalogName) throws IOException {
        ContextInfo contextOfIntent = new ContextInfo(ContextType.INTENT, catalogName);
        CSVRecord record = parser.iterator().next();
        int size = record.size();
        NameGenerator namer = new NameGenerator();
        
        List<NeoConcept> concepts = IntStream.range(1,size)
                .mapToObj(s -> new NeoConcept(namer.generateName(), "Value", "Value"+s, contextOfIntent))
                .collect(Collectors.toList());
        
        NeoConcept recordConcept = new NeoConcept(namer.generateName(), "Record", catalogName, new ContextInfo(ContextType.INTENT, catalogName));
        
        List<NeoRelation> relations = concepts.stream()
                .map(c -> new NeoRelation(c, recordConcept, contextOfIntent, c.getReferent().orElse("Value")))
                .collect(Collectors.toList());
        
        concepts.add(recordConcept);
        return new NeoGraph(concepts, relations);
    }
    
}

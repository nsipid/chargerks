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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author GrenonMP
 */
public class BorderedTableMetadataExtractor implements MetadataExtractor {
    String table;
    char borderSymbol;
    
    public BorderedTableMetadataExtractor(String table, char borderSymbol) {
        this.table = table;
        this.borderSymbol = borderSymbol;
    }
    
    public NeoGraph generateCatalog(String catalogName) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(this.table));
        ArrayList<String> lines = new ArrayList<String>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            lines.add(line);
            if (line.matches("^[\\s" + borderSymbol + "]+$"))
                break;
        }
        reader.close();
        
        FixedWidthHeader header = new FixedWidthHeader(lines.toArray(new String[0]));
        NameGenerator namer = new NameGenerator();
        List<NeoConcept> concepts = header.getColumnNames().stream()
                .map(s -> new NeoConcept(namer.generateName(), s, null, new ContextInfo(ContextType.INTENT, catalogName)))
                .collect(Collectors.toList());
        
        NeoConcept tableConcept = new NeoConcept(namer.generateName(), "Table", "Course", new ContextInfo(ContextType.INTENT, catalogName));
        
        List<NeoRelation> relations = concepts.stream()
                .map(c -> new NeoRelation(c, tableConcept, "SCHEMA_DECLARES"))
                .collect(Collectors.toList());
        
        concepts.add(tableConcept);
        return new NeoGraph(concepts, relations);
    }
}

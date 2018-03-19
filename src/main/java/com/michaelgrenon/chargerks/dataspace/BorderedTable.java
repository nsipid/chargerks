/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

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
public class BorderedTable {
    String table;
    char borderSymbol;

    FixedWidthHeader header;
    List<String> lines;
    
    public BorderedTable(String table, char borderSymbol) {
        this.table = table;
        this.borderSymbol = borderSymbol;

        try {
            BufferedReader reader = new BufferedReader(new StringReader(this.table));
            this.lines = new ArrayList<String>();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                lines.add(line);
                if (line.matches("^[\\s" + borderSymbol + "]+$"))
                    break;
            }
            header = new FixedWidthHeader(lines.toArray(new String[0]));

            lines.clear();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.trim().isEmpty()) {
                    break;
                }
                lines.add(line);
            }
            
            reader.close();  
        } catch (IOException e) {
            
        }
    }

    public FixedWidthHeader getHeader() {
        return header;
    }

    public List<String[]> parseLines() {
        return lines.stream().map(l -> header.parseLine(l)).collect(Collectors.toList());
    }

    
}

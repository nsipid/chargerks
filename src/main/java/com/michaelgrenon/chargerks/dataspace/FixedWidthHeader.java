package com.michaelgrenon.chargerks.dataspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class FixedWidthHeader {
    private ArrayList<FixedColumn> columns;
    private String[] columnNames;
    
    public FixedWidthHeader(String[] headerLines) {
        if (headerLines.length < 2) {
            throw new IllegalArgumentException("At least two header lines are required.");
        }
        initColumnInfoFromBorder(headerLines[headerLines.length - 1]);
        initHeadingsFromLinesAboveBorder(Arrays.copyOfRange(headerLines, 0, headerLines.length - 1));
    }
    
    public List<String> getColumnNames() {
        return Arrays.asList(this.columnNames);
    }

    public String[] parseLine(String line) {
        return columns.stream().map(c -> c.getColumnData(line)).toArray(String[]::new);
    }
    
    public void initHeadingsFromLinesAboveBorder(String[] lines) {
        this.columnNames = this.columns.stream()
                .map(col -> Arrays.stream(lines)
                        .map(line -> col.getColumnData(line))
                        .collect(Collectors.joining(" ")))
                .map(name -> name.trim())
                .toArray(String[]::new);
    }
    
    public void initColumnInfoFromBorder(String line) {
        this.columns = new ArrayList<>();
        int length = line.length();
        int i = 0;
        int j = 0;
        while (i < length) {
            if (Character.isWhitespace(line.charAt(i))) {
                i++;
            } else {
                for (j = i; j < length && !Character.isWhitespace(line.charAt(j)); ++j);
                columns.add(new FixedColumn(i,j));
                i = j;
            }
        }
    }
}
    
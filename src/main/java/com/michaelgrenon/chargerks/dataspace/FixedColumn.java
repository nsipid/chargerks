/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

/**
 *
 * @author GrenonMP
 */
public class FixedColumn {

    public FixedColumn(int startPosition, int endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }
    
    public String getColumnData(String line) {
        return line.substring(startPosition, endPosition).trim();
    }
    
    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    private int startPosition;
    private int endPosition;
}

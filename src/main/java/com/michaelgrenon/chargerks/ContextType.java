/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

/**
 *
 * @author GrenonMP
 */
public enum ContextType {
    UNIVERSE(0),
    STORE(1),
    INTENT(2),
    USE(3);
    
    private int layer;

    public int getLayer() {
        return layer;
    }
    
    ContextType(int layer) {
        this.layer = layer;
    }
}

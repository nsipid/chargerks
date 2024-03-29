/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import java.util.Objects;

/**
 *
 * @author GrenonMP
 */
public class ContextInfo {

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.type);
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }
    public static ContextInfo UNIVERSE = new ContextInfo(ContextType.UNIVERSE, 
            "Sheet of Assertion");
    
    private ContextType type;

    public ContextType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    private String name;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContextInfo other = (ContextInfo) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }
    
    public ContextInfo(ContextType type, String name) {
        this.type = type;
        this.name = name;
    }
}

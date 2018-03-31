package com.michaelgrenon.chargerks;

import java.util.Objects;

public class NeoRelationBinding {
    private String variable;
    private NeoRelation relation;

    public String getVariable() {
        return variable;
    }

    public NeoRelation getRelation() {
        return relation;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.variable);
        hash = 89 * hash + Objects.hashCode(this.relation);
        return hash;
    }

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
        final NeoRelationBinding other = (NeoRelationBinding) obj;
        if (!Objects.equals(this.variable, other.variable)) {
            return false;
        }
        if (!Objects.equals(this.relation, other.relation)) {
            return false;
        }
        return true;
    }
    
    public NeoRelationBinding(String variable, NeoRelation relation) {
        this.variable = variable;
        this.relation = relation;
    }
}
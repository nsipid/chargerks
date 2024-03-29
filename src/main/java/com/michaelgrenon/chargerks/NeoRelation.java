package com.michaelgrenon.chargerks;

import java.util.Objects;

public class NeoRelation {

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.concept1);
        hash = 11 * hash + Objects.hashCode(this.concept2);
        hash = 11 * hash + Objects.hashCode(this.context);
        hash = 11 * hash + Objects.hashCode(this.label);
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
        final NeoRelation other = (NeoRelation) obj;
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.concept1, other.concept1)) {
            return false;
        }
        if (!Objects.equals(this.concept2, other.concept2)) {
            return false;
        }
        if (!Objects.equals(this.context, other.context)) {
            return false;
        }
        return true;
    }
    private NeoConceptBinding concept1;
    private NeoConceptBinding concept2;
    private ContextInfo context;

    public ContextInfo getContext() {
        return context;
    }
    private String label;
    
    public NeoRelation(NeoConceptBinding concept1, NeoConceptBinding concept2, ContextInfo context, String label) {
        this.concept1 = concept1;
        this.concept2 = concept2;
        this.context = context;
        this.label = label;
    }
    
    public String toCypherExplicit() {
        return String.format("(%s)-[:`%s` {contextType: '%s', contextName: '%s'}]->(%s)", 
            concept1.getVariable(), label, context.getType().name(), context.getName(), concept2.getVariable());
    }

    public NeoConceptBinding getConcept1() {
        return concept1;
    }

    public NeoConceptBinding getConcept2() {
        return concept2;
    }

    public String getLabel() {
        return label;
    }
}

package com.michaelgrenon.chargerks;

public class NeoRelation {
    private NeoConcept concept1;
    private NeoConcept concept2;
    private ContextInfo context;

    public ContextInfo getContext() {
        return context;
    }
    private String label;
    
    public NeoRelation(NeoConcept concept1, NeoConcept concept2, ContextInfo context, String label) {
        this.concept1 = concept1;
        this.concept2 = concept2;
        this.context = context;
        this.label = label;
    }
    
    public String toCypher() {
        return String.format("(%s)-[:MATCHES*0..1]-()-[:%s]-(%s)", concept1.getVariable(), label, concept2.getVariable());
    }

    public NeoConcept getConcept1() {
        return concept1;
    }

    public NeoConcept getConcept2() {
        return concept2;
    }

    public String getLabel() {
        return label;
    }
}

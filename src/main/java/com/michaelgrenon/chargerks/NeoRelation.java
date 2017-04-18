package com.michaelgrenon.chargerks;

public class NeoRelation {
    private NeoConcept concept1;
    private NeoConcept concept2;
    private String label;
    
    public NeoRelation(NeoConcept concept1, NeoConcept concept2, String label) {
        this.concept1 = concept1;
        this.concept2 = concept2;
        this.label = label;
    }
    
    public String toCypher() {
        return String.format("(%s)-[:MATCHES*0..1]-()-[:%s]-(%s)", concept1.variable, label, concept2.variable);
    }
}

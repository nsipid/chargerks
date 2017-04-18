package com.michaelgrenon.chargerks;

public class NeoConcept {
    public String variable;
    public String type;
    public String referent;
    public String catalog;
    
    public NeoConcept(String variable, String type, String referent, String catalog) {
        this.variable = variable;
        this.type = type;
        this.referent = referent;
        this.catalog = catalog;
    }
    
    public String toCypher() {
        boolean nullOrEmpty = referent != null && !referent.isEmpty();
        String referentPart =  nullOrEmpty ? String.format("referent: '%s', ", referent) : "";
        return String.format("(%s:%s {%scatalog: '%s'})", variable, type, referentPart, catalog);
    }
}

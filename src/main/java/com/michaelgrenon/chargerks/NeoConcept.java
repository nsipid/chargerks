package com.michaelgrenon.chargerks;

public class NeoConcept {

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReferent() {
        return referent;
    }

    public void setReferent(String referent) {
        this.referent = referent;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
    private String variable;
    private String type;
    private String referent;
    private String catalog;
    
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

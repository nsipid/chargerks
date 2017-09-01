package com.michaelgrenon.chargerks;

public class NeoConcept {

    public String getVariable() {
        return variable;
    }

    public String getType() {
        return type;
    }

    public String getReferent() {
        return referent;
    }
    
    public ContextInfo getContext() {
        return context;
    }

    private String variable;
    private String type;
    private String referent;
    private ContextInfo context;
    
    public NeoConcept(String variable, String type, String referent, ContextInfo context) {
        this.variable = variable;
        this.type = type;
        this.referent = referent;
        this.context = context;
    }
    
    public String toCypher() {
        boolean nullOrEmpty = referent != null && !referent.isEmpty();
        String referentPart =  nullOrEmpty ? String.format("referent: '%s', ", referent) : "";
        String contextPart = (context.getType().equals(ContextType.INTENT) ? "catalog: " : "context: ") + String.format("'%s'", context.getName());
        return String.format("(%s:%s {%scatalog: '%s'})", variable, type, referentPart, contextPart);
    }
}

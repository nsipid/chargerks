package com.michaelgrenon.chargerks;

import java.util.Objects;
import java.util.Optional;

public class NeoConcept {

    public String getVariable() {
        return variable;
    }

    public String getType() {
        return type;
    }

    public Optional<String> getReferent() {
        return Optional.ofNullable(referent).filter(ref -> !ref.isEmpty());
    }
    
    public ContextInfo getContext() {
        return context;
    }

    private String variable;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.referent);
        hash = 89 * hash + Objects.hashCode(this.context);
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
        final NeoConcept other = (NeoConcept) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.referent, other.referent)) {
            return false;
        }
        if (!Objects.equals(this.context, other.context)) {
            return false;
        }
        return true;
    }
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
        String contextTypePart = String.format("contextType: %d, ", context.getType());
        String contextNamePart = String.format("contextName: '%s'", context.getName());
        return String.format("(%s:%s {%s%s%s})", variable, type, referentPart, contextTypePart, contextNamePart);
    }
    
}

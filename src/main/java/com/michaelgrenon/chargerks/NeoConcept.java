package com.michaelgrenon.chargerks;

import java.util.Objects;
import java.util.Optional;

public class NeoConcept {
    private String type;
    private String referent;
    private ContextInfo context;

    public String getType() {
        return type;
    }

    public Optional<String> getReferent() {
        return Optional.ofNullable(referent).filter(ref -> !ref.isEmpty());
    }
    
    public ContextInfo getContext() {
        return context;
    }

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

    
    public NeoConcept(String type, String referent, ContextInfo context) {
        this.type = type;
        this.referent = referent;
        this.context = context;
    }
    
    public String toCypher() {
        boolean nullOrEmpty = referent != null && !referent.isEmpty();
        String referentPart =  nullOrEmpty ? String.format("referent: '%s', ", referent) : "";
        String contextTypePart = String.format("contextType: '%s', ", context.getType().name());
        String contextNamePart = String.format("contextName: '%s'", context.getName());
        return String.format("(:`%s` {%s%s%s})", type, referentPart, contextTypePart, contextNamePart);
    }

    public String toCypherWithSpecialReferent() {
        boolean nullOrEmpty = referent != null && !referent.isEmpty();
        String referentPart =  nullOrEmpty ? String.format("referent: %s, ", referent) : "";
        String contextTypePart = String.format("contextType: '%s', ", context.getType().name());
        String contextNamePart = String.format("contextName: '%s'", context.getName());
        return String.format("(:`%s` {%s%s%s})", type, referentPart, contextTypePart, contextNamePart);
    }
    
}

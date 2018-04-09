package com.michaelgrenon.chargerks;

import java.util.Objects;

public class NeoConceptBinding {
    private String variable;
    private NeoConcept concept;
    private static final String referentVariableSuffix = "_referent";

    public String getVariable() {
        return variable;
    }

    public String getReferentVariable() {
        return variable + referentVariableSuffix;
    }

    public NeoConcept getConcept() {
        return concept;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.variable);
        hash = 89 * hash + Objects.hashCode(this.concept);
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
        final NeoConceptBinding other = (NeoConceptBinding) obj;
        if (!Objects.equals(this.variable, other.variable)) {
            return false;
        }
        if (!Objects.equals(this.concept, other.concept)) {
            return false;
        }
        return true;
    }
    
    public NeoConceptBinding(String variable, NeoConcept concept) {
        this.variable = variable;
        this.concept = concept;
    }
    
    public String toCypher() {
        String referentPart = concept.getReferent().map(referent -> String.format("referent: '%s', ", referent)).orElse("");
        String contextTypePart = String.format("contextType: '%s', ", concept.getContext().getType().name());
        String contextNamePart = String.format("contextName: '%s'", concept.getContext().getName());
        return String.format("(%s:`%s` {%s%s%s})", variable, concept.getType(), referentPart, contextTypePart, contextNamePart);
    }

    public String toCypherWithoutContext() {
        String referentPart = concept.getReferent().map(referent -> String.format("{referent: '%s'}", referent)).orElse("");
        return String.format("(%s:`%s` %s)", variable, concept.getType(), referentPart);
    }

    public String toCypherWithSpecialReferent() {
        String referentPart = concept.getReferent().map(referent -> String.format("referent: %s, ", referent)).orElse("");
        String contextTypePart = String.format("contextType: '%s', ", concept.getContext().getType().name());
        String contextNamePart = String.format("contextName: '%s'", concept.getContext().getName());
        return String.format("(%s:`%s` {%s%s%s})", variable, concept.getType(), referentPart, contextTypePart, contextNamePart);
    }

    public String toCypherWithoutReferent() {
        String contextTypePart = String.format("contextType: '%s', ", concept.getContext().getType().name());
        String contextNamePart = String.format("contextName: '%s'", concept.getContext().getName());
        return String.format("(%s:`%s` {%s%s})", variable, concept.getType(), contextTypePart, contextNamePart);
    }
    
}

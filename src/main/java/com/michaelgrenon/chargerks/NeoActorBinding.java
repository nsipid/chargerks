package com.michaelgrenon.chargerks;

import java.util.Objects;

public class NeoActorBinding implements Comparable<NeoActorBinding> {
    private String variable;
    private NeoActor actor;

    public String getVariable() {
        return variable;
    }

    public NeoActor getActor() {
        return actor;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.variable);
        hash = 89 * hash + Objects.hashCode(this.actor);
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
        final NeoActorBinding other = (NeoActorBinding) obj;
        if (!Objects.equals(this.variable, other.variable)) {
            return false;
        }
        if (!Objects.equals(this.actor, other.actor)) {
            return false;
        }
        return true;
    }
    
    public NeoActorBinding(String variable, NeoActor actor) {
        this.variable = variable;
        this.actor = actor;
    }

	@Override
	public int compareTo(NeoActorBinding o) {
		return actor.compareTo(o.actor);
	}
}
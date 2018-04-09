package com.michaelgrenon.chargerks;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NeoActor implements Comparable<NeoActor> {
    private List<NeoConceptBinding> outputs;
    private List<NeoConceptBinding> inputs;
    private String label;

    public List<NeoConceptBinding> getOutputs() {
        return outputs;
    }

    public List<NeoConceptBinding> getInputs() {
        return inputs;
    }

    public String getLabel() {
        return label;
    }

    public NeoActor(String label, List<NeoConceptBinding> inputs, List<NeoConceptBinding> outputs) {
        this.label = label;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = outputs.stream().map(Objects::hashCode).reduce(hash, (acc, next) -> 41 * acc + next);
        hash = inputs.stream().map(Objects::hashCode).reduce(hash, (acc, next) -> 41 * acc + next);
        hash = 41 * hash + Objects.hashCode(this.label);
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
        final NeoActor other = (NeoActor) obj;

        if (!Objects.equals(this.outputs, other.outputs)) {
            if (this.outputs.size() != this.outputs.size()) {
                return false;
            }

            for (int i = 0; i < this.outputs.size(); i++) {
                if (!Objects.equals(this.outputs.get(i), other.outputs.get(i))) {
                    return false;
                }
            }
        }
        if (!Objects.equals(this.inputs, other.inputs)) {
            if (this.inputs.size() != this.inputs.size()) {
                return false;
            }

            for (int i = 0; i < this.inputs.size(); i++) {
                if (!Objects.equals(this.inputs.get(i), other.inputs.get(i))) {
                    return false;
                }
            }
        }

        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(NeoActor that) {
        int sgn = 0;
        if (that.inputs.stream().anyMatch(this.outputs::contains)) {
            sgn = -1;
        } else if (this.inputs.stream().anyMatch(that.outputs::contains)) {
            sgn = 1;
        }
        return sgn;
    }
}
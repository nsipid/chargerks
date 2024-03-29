package com.michaelgrenon.chargerks;

import java.util.Iterator;
import java.util.Set;

public class ActorLambda {
    final static int constraintCounter = 0;
    
    private NeoActorBinding actorBinding;
    private Set<NeoConceptBinding> conceptsLinkedByRelation;
	private boolean referentIsRecord;

    public boolean isConstraint() {
        NeoActor actor = actorBinding.getActor();
        return actor.getOutputs().stream().anyMatch(c -> conceptHasReferent(c) && conceptTypeIsBool(c));
    }

    public boolean isPositiveContraint() {
        NeoActor actor = actorBinding.getActor();
        return actor.getOutputs().stream().anyMatch(c -> referentIsTrue(c) && conceptTypeIsBool(c));
    }

    public boolean isNegativeContraint() {
        NeoActor actor = actorBinding.getActor();
        return actor.getOutputs().stream().anyMatch(c -> referentIsFalse(c) && conceptTypeIsBool(c));
    }

    public String getVariable() {
        return actorBinding.getActor().getOutputs().stream().map(c->c.getVariable()).findAny().get();
    }

    public ActorLambda(NeoActorBinding actorBinding, Set<NeoConceptBinding> conceptsLinkedByRelation, boolean referentIsRecord) {
        this.actorBinding = actorBinding;
        this.conceptsLinkedByRelation = conceptsLinkedByRelation;
        this.referentIsRecord = referentIsRecord;
    }

    public String toCypherExpressionOrProcedure() {
        NeoActor actor = actorBinding.getActor();
        String upper = actor.getLabel().toUpperCase();
        String cypher = null;
        switch (upper) {
            case "MINUS":
            case "SUBTRACT":
            case "-":
                cypher = String.format("toInteger(%s) - toInteger(%s)", ref(0), ref(1));
                break;
            case "ADD":
            case "PLUS":
            case "+":
                cypher = String.format("toInteger(%s) + toInteger(%s)", ref(0), ref(1));
                break;
            case "*":
            case "TIMES":
            case "MULTIPLY":
                cypher = String.format("toInteger(%s) * toInteger(%s)", ref(0), ref(1));
                break;
            case "/":
            case "DIVIDED_BY":
            case "DIVIDE":
                cypher = String.format("toInteger(%s) / toInteger(%s)", ref(0), ref(1));
                break;
            case "gt":
            case ">":
            case "GREATER_THAN":
                cypher = String.format("toInteger(%s) > toInteger(%s)", ref(0), ref(1));
                break;
            case "lt":
            case "<":
            case "LESS_THAN":
                cypher = String.format("toInteger(%s) < toInteger(%s)", ref(0), ref(1));
                break;
            case "REGEXP":
            case "REGEX":
            case "REGULAR_EXPRESSION":
                cypher = String.format("%s =~ %s", ref(0), ref(1));
                break;
            default:
                //assume stored-function then, CALL/YIELD not supported yet
                StringBuilder functionBuilder = new StringBuilder();
                functionBuilder.append(actor.getLabel());
                functionBuilder.append("(");
                Iterator<NeoConceptBinding> inputItr = actor.getInputs().iterator();
                while (inputItr.hasNext()) {
                    NeoConceptBinding bnd = inputItr.next();

                    functionBuilder.append(getArgument(bnd));
                    if (inputItr.hasNext()) {
                        functionBuilder.append(", ");
                    } 
                }
                functionBuilder.append(")");
                cypher = functionBuilder.toString();
        }
        return cypher;
    }

    private String ref(int i) {
        NeoActor actor = actorBinding.getActor();
        NeoConceptBinding input = actor.getInputs().get(i);
        return getArgument(input);
    }

    private String getArgument(NeoConceptBinding input) {
        boolean inputLinkedToRelation = conceptsLinkedByRelation.contains(input);
        if (inputLinkedToRelation) {
            return referentIsRecord ? input.referToReferentAsRecord() : input.referToReferentAsNodeProperty();
        } else if(input.getConcept().getType().toUpperCase().trim().equals("LITERAL")) {
            return input.getConcept().getReferent().get();
        } else {
            return input.getVariable();
        }
    }

    private static boolean conceptHasReferent(NeoConceptBinding concept) {
        return !concept.getConcept().getReferent().orElse("").equals("");
    }

    private static boolean referentIsTrue(NeoConceptBinding concept) {
        return concept.getConcept().getReferent().orElse("").toUpperCase().trim().equals("TRUE");
    }

    private static boolean referentIsFalse(NeoConceptBinding concept) {
        return concept.getConcept().getReferent().orElse("").toUpperCase().trim().equals("FALSE");
    }

    private static boolean conceptTypeIsBool(NeoConceptBinding concept) {
        String type = concept.getConcept().getType().toUpperCase().trim();
        return type.equals("BOOLEAN") || type.equals("BOOL");
    }
}
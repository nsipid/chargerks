package com.michaelgrenon.chargerks.ops;


public class FullKsQuestion implements Question {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static String cypher;

    static {
        StringBuilder builder = new StringBuilder();
        builder.append("MATCH (a)-[r]-()");
        builder.append(NEW_LINE);
        builder.append("WHERE r.contextType = 'INTENT' OR r.contextType = 'USE'");
        builder.append(NEW_LINE);
        builder.append("RETURN collect(a) as nodes, collect(r) as relationships");
        cypher = builder.toString();
    }
    
    public FullKsQuestion() {
    }
    
    @Override
    public String toCypher() {
        return cypher;
    }

    @Override
    public Answer getAnswer() {
        return new NodesRelationshipsAnswer();
    }
    
}
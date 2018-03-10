package com.michaelgrenon.chargerks;


public class ContextQuestion implements Question {
    ContextInfo info;
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    public ContextQuestion(ContextInfo info) {
        this.info = info;
    }
    
    @Override
    public String toCypher() {
        StringBuilder builder = new StringBuilder();
        builder.append("MATCH (node {contextType: ");
        builder.append(info.getType().ordinal());
        builder.append(", contextName: `");
        builder.append(info.getName());
        builder.append("`}), (a)-[r {contextType: ");
        builder.append(info.getType().ordinal());
        builder.append(", contextName: `");
        builder.append(info.getName());
        builder.append("`}]-()");
        builder.append(NEW_LINE);
        builder.append("RETURN collect(a) as nodes, collect(r) as relationships");
        return builder.toString();
    }

    @Override
    public Answer getAnswer() {
        return new NodesRelationshipsAnswer();
    }
    
}

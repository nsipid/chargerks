package com.michaelgrenon.chargerks.ops;

import com.michaelgrenon.chargerks.ContextInfo;

public class ContextQuestion implements Question {
    ContextInfo info;
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    public ContextQuestion(ContextInfo info) {
        this.info = info;
    }
    
    @Override
    public String toCypher() {
        StringBuilder builder = new StringBuilder();
        builder.append("MATCH (a)-[r {contextType: '");
        builder.append(info.getType().name());
        builder.append("', contextName: '");
        builder.append(info.getName());
        builder.append("'}]-()");
        builder.append(NEW_LINE);
        builder.append("RETURN collect(a) as nodes, collect(r) as relationships");
        return builder.toString();
    }

    @Override
    public Answer getAnswer() {
        return new NodesRelationshipsAnswer();
    }
    
}

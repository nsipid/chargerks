package com.michaelgrenon.chargerks.ops;

import org.neo4j.driver.v1.StatementResult;

public interface Command {
    public String toCypher();
    public String getSummary(StatementResult result);
}
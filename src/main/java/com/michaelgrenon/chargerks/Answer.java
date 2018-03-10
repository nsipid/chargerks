package com.michaelgrenon.chargerks;

import org.neo4j.driver.v1.StatementResult;

public interface Answer {
    public NeoGraph fromResult(StatementResult result);
}

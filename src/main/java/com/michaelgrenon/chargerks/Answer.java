package com.michaelgrenon.chargerks;

import charger.obj.Graph;
import org.neo4j.driver.v1.StatementResult;

public interface Answer {
    public Graph fromResult(StatementResult result);
}

package com.michaelgrenon.chargerks;

import java.util.Collection;

import org.neo4j.driver.v1.StatementResult;

public interface Answer {
    public Collection<NeoGraph> fromResult(StatementResult result);
}

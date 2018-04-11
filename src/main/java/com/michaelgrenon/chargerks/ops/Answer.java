package com.michaelgrenon.chargerks.ops;

import java.util.Collection;

import com.michaelgrenon.chargerks.NeoGraph;

import org.neo4j.driver.v1.StatementResult;

public interface Answer {
    public Collection<NeoGraph> fromResult(StatementResult result);
}

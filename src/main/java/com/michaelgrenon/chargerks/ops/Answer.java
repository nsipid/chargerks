package com.michaelgrenon.chargerks.ops;

import java.util.Iterator;

import com.michaelgrenon.chargerks.NeoGraph;

import org.neo4j.driver.v1.StatementResult;

public interface Answer extends Iterator<NeoGraph> {
    public Answer setResult(StatementResult result);
    public String getSummary();
}

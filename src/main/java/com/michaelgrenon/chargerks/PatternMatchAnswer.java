package com.michaelgrenon.chargerks;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

public class PatternMatchAnswer implements Answer {

    @Override
    public NeoGraph fromResult(StatementResult result) {
        return null;
    }
}
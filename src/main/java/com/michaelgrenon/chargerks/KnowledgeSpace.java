package com.michaelgrenon.chargerks;

import java.util.Optional;
import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

public class KnowledgeSpace {
    Driver driver;
    AuthToken authToken;
    String uri;
    boolean isOpen;
    
    public KnowledgeSpace(String uri, String username, String password) {
        this.uri = uri;
        this.authToken = AuthTokens.basic(username, password);
    }
    
    public synchronized void open() {
        if(!isOpen) {
            this.driver = Optional.ofNullable(this.driver)
                    .orElse(GraphDatabase.driver(uri, authToken));
            isOpen = true;
        }
    }
    
    public synchronized void close() {
        if (isOpen) {
            isOpen = false;
            driver.close();
        }
    }
    
    public NeoGraph Ask(Question question) {
        try (Session session = driver.session()) {
            String query = question.toCypher();
            StatementResult result = session.run(query);
            return question.getAnswer().fromResult(result);
        }
    }
    
    public void Execute(Command command) {
        try (Session session = driver.session()) {
            String cypher = command.toCypher();
            session.run(cypher);
        }
    }
}

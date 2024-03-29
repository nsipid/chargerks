package com.michaelgrenon.chargerks;

import java.util.Iterator;
import java.util.Optional;

import com.michaelgrenon.chargerks.ops.Answer;
import com.michaelgrenon.chargerks.ops.Command;
import com.michaelgrenon.chargerks.ops.MultiCommand;
import com.michaelgrenon.chargerks.ops.Question;

import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
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
    
    public Iterator<NeoGraph> Ask(Question question) {
        try (Session session = driver.session()) {
            String query = question.toCypher();
            System.out.println(query);
            StatementResult result = session.run(query);
            Answer ans = question.getAnswer().setResult(result);
            return ans;
        }
    }
    
    public void Execute(Command command) {
        try (Session session = driver.session()) {
            String cypher = command.toCypher();
            System.out.println(cypher);           
            StatementResult result = session.run(cypher);
            System.out.println(command.getSummary(result));
        }
    }

    public void Execute(MultiCommand commands) {
        try (Session session = driver.session()) {
            for (Command command : commands.toList()) {
                String cypher = command.toCypher();
                System.out.println(cypher);           
                StatementResult result = session.run(cypher);
                System.out.println(command.getSummary(result));
            }
        }
    }
}

package com.michaelgrenon.chargerks.cli;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.michaelgrenon.chargerks.AskDataQuestion;
import com.michaelgrenon.chargerks.CgConverter;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.Question;

import charger.obj.Graph;
import charger.xml.CGXParser;

public class AskDataCli implements Runnable {

    private String inputFile;
    private String contextName;
    private KnowledgeSpace ks;

	public AskDataCli(KnowledgeSpace ks, String inputFile, String contextName) {
        this.ks = ks;
        this.inputFile = inputFile;
        this.contextName = contextName;
    }

    private Question buildQuestion() throws FileNotFoundException {

        charger.Global.setup( null, new ArrayList<String>(), false );
        InputStream inputStream = new FileInputStream(inputFile);
        Graph readGraph = new Graph();
        CGXParser.parseForNewGraph(inputStream, readGraph);
        
        NeoGraph neoGraph = CgConverter.chargerToNeo(readGraph);
        Question question = new AskDataQuestion(contextName, neoGraph);

        return question;
    }

	@Override
	public void run() {
        try {
            Question question = buildQuestion();
            ks.Ask(question);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read to input data.", e);
        }
	}

}

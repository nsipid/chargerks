package com.michaelgrenon.chargerks.cli;

import java.io.InputStream;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JFrame;

import com.michaelgrenon.chargerks.AskDataQuestion;
import com.michaelgrenon.chargerks.cg.CgConverter;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.Question;

import charger.IOManager;
import charger.exception.CGFileException;
import charger.obj.Graph;
import charger.xml.CGXParser;
import chargerlib.FileFormat;

public class AskDataCli implements Runnable {

    private String inputFile;
    private String contextName;
    private KnowledgeSpace ks;
	private int limit;
	private String outputFile;
	private int maxVariableExpansion;
	private boolean maintainContextualInfo;

	public AskDataCli(KnowledgeSpace ks, String inputFile, String outputFile, String contextName, int limit, int maxVariableExpansion, boolean maintainContextualInfo) {
        this.ks = ks;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.contextName = contextName;
        this.limit = limit;
        this.maxVariableExpansion = maxVariableExpansion;
        this.maintainContextualInfo = maintainContextualInfo;
    }

    private Question buildQuestion() throws FileNotFoundException {

        charger.Global.setup( null, new ArrayList<String>(), false );
        InputStream inputStream = new FileInputStream(inputFile);
        Graph readGraph = new Graph();
        CGXParser.parseForNewGraph(inputStream, readGraph);
        
        NeoGraph neoGraph = CgConverter.chargerToNeo(readGraph);
        Question question = new AskDataQuestion(contextName, neoGraph, limit, maxVariableExpansion, maintainContextualInfo);

        return question;
    }

	@Override
	public void run() {
        try {
            Question question = buildQuestion();
            Collection<NeoGraph> results = ks.Ask(question);
            int i = 0;
            for (NeoGraph result : results) {
                Graph outGraph = CgConverter.neoToCharger(result);
                
                int extension = outputFile.lastIndexOf(".");
                String infix = i > 0 ? Integer.toString(i) : "";
                String currentFile = outputFile.substring(0, extension) + infix + outputFile.substring(extension);
                IOManager.saveGraphAsTextFormat(outGraph, FileFormat.CHARGER4, new File(currentFile));
                i++;
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read to input data.", e);
        } catch (CGFileException e) {
            throw new IllegalArgumentException("Could not write to output file.", e);
        } finally {
            Arrays.stream(JFrame.getFrames()).forEach(Frame::dispose);
        }
	}

}

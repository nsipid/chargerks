package com.michaelgrenon.chargerks.cli;

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.cg.CgConverter;
import com.michaelgrenon.chargerks.ops.Command;
import com.michaelgrenon.chargerks.ops.MergeCommand;

import charger.obj.Graph;
import charger.xml.CGXParser;

public class MergeMetadataCli implements Runnable {
	private String inputFile;
	private KnowledgeSpace ks;


	public MergeMetadataCli(KnowledgeSpace ks, String inputFile) throws FileNotFoundException, IOException {
        this.inputFile = inputFile;
        this.ks = ks;
    }

    private Command buildCommand() throws FileNotFoundException {
        charger.Global.setup( null, new ArrayList<String>(), false );
        InputStream inputStream = new FileInputStream(inputFile);
        Graph readGraph = new Graph();
        CGXParser.parseForNewGraph(inputStream, readGraph);
        
        NeoGraph neoGraph = CgConverter.chargerToNeo(readGraph);

        return new MergeCommand(neoGraph);
    }


	@Override
	public void run() {
        try {
			ks.Execute(this.buildCommand());
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Could not parse input file", e);
		} finally {
            Arrays.stream(JFrame.getFrames()).forEach(Frame::dispose);
        }
	}
}
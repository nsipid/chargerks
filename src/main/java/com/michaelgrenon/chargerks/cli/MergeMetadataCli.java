package com.michaelgrenon.chargerks.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import charger.IOManager;
import charger.exception.CGFileException;
import charger.obj.Graph;
import charger.xml.CGXParser;
import chargerlib.FileFormat;
import com.michaelgrenon.chargerks.CgConverter;
import com.michaelgrenon.chargerks.Command;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.MergeCommand;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.dataspace.CsvMetadataExtractor;
import com.michaelgrenon.chargerks.dataspace.UahClassListMetadataExtractor;

public class MergeMetadataCli implements Runnable {
	private String inputFile;
	private Command command;
	private KnowledgeSpace ks;


	public MergeMetadataCli(KnowledgeSpace ks, String inputFile) throws FileNotFoundException, IOException {
        this.inputFile = inputFile;
        this.ks = ks;
        this.command = buildCommand();
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
        ks.Execute(this.command);
	}
}
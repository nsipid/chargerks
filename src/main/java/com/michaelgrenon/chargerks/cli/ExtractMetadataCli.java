package com.michaelgrenon.chargerks.cli;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import com.michaelgrenon.chargerks.Command;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.MergeCommand;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.dataspace.CsvMetadataExtractor;
import com.michaelgrenon.chargerks.dataspace.UahClassListMetadataExtractor;

public class ExtractMetadataCli implements Runnable {
    KnowledgeSpace ks;
    Command command;

    public ExtractMetadataCli(KnowledgeSpace ks, String contextName, String format, String uriOrPath) throws IOException {
        this.ks = ks;
        this.command = buildCommand(contextName, format, uriOrPath);
    }

    private Command buildCommand(String contextName, String format, String uriOrPath) throws IOException {
        switch (format.toLowerCase()) {
            case "csv":
                Reader reader = new FileReader(uriOrPath);
                CsvMetadataExtractor csvExtractor = new CsvMetadataExtractor(reader);
                NeoGraph csvGraph = csvExtractor.generateCatalog(contextName);
                return new MergeCommand(csvGraph);
            case "uah-classes":
                UahClassListMetadataExtractor uahClassExtractor = new UahClassListMetadataExtractor(uriOrPath);
                NeoGraph uahClassGraph = uahClassExtractor.generateCatalog(contextName);
                return new MergeCommand(uahClassGraph);
            default:
                throw new IllegalArgumentException("Unknown format.");
        }
    }

	@Override
	public void run() {
        this.ks.Execute(this.command);
	}
}
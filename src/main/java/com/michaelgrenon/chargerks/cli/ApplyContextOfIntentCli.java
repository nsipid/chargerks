package com.michaelgrenon.chargerks.cli;

import com.michaelgrenon.chargerks.ApplyContextOfIntentCommand;
import com.michaelgrenon.chargerks.Command;
import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextQuestion;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.Question;
import com.michaelgrenon.chargerks.dataspace.UahClassListTransformer;
import java.io.IOException;

public class ApplyContextOfIntentCli implements Runnable {

    private String importDataUri;
	private String contextOfIntent;
	private KnowledgeSpace ks;
	private String format;
	private Command apply;

	public ApplyContextOfIntentCli(KnowledgeSpace ks, String contextOfIntent, String format, String importDataUri) throws IOException {
        this.ks = ks;
        this.format = format;
        this.contextOfIntent = contextOfIntent;
        this.importDataUri = importDataUri;
        this.apply = buildCommand();
    }

    private Command buildCommand() throws IOException {
        Question getContext = new ContextQuestion(new ContextInfo(ContextType.INTENT, contextOfIntent));
        NeoGraph contextGraph = ks.Ask(getContext);
        if (this.format == "csv" || this.format == "csv-header") {
            
        } else if (this.format == "uah-classes") {
            UahClassListTransformer.toCsv(this.importDataUri, "D:\\Source\\neo4j\\import\\uahclasses.csv");
            importDataUri = "D:\\Source\\neo4j\\import\\uahclasses.csv";
        } else {
            throw new IllegalArgumentException("Bad format");
        }

        return new ApplyContextOfIntentCommand(contextGraph, importDataUri);
    }
    
	@Override
	public void run() {
        ks.Execute(apply);
	}
}
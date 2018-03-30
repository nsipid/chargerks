package com.michaelgrenon.chargerks.cli;

import com.michaelgrenon.chargerks.ApplyContextOfIntentCommand;
import com.michaelgrenon.chargerks.Command;
import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextQuestion;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.IndexIntentCommand;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.Question;
import com.michaelgrenon.chargerks.dataspace.DistanceMatrixTransformer;
import com.michaelgrenon.chargerks.dataspace.UahClassListTransformer;
import java.io.IOException;

public class ApplyContextOfIntentCli implements Runnable {

    private String importDataUri;
	private String contextOfIntent;
	private KnowledgeSpace ks;
	private String format;
	private Command apply;
	private String apiKey;

	public ApplyContextOfIntentCli(KnowledgeSpace ks, String contextOfIntent, String format, String importDataUri, String apikeyArg) throws IOException {
        this.ks = ks;
        this.format = format;
        this.contextOfIntent = contextOfIntent;
        this.importDataUri = importDataUri;
        this.apiKey = apikeyArg;
        this.apply = buildCommand();
    }

    private Command buildCommand() throws IOException {
        Question getContext = new ContextQuestion(new ContextInfo(ContextType.INTENT, contextOfIntent));
        NeoGraph contextGraph = ks.Ask(getContext);
        boolean withHeaders = false;
        if (this.format.equals("csv")) {
            withHeaders = false;
        } else if (this.format.equals("csv-header")) {
            withHeaders = true;
        } else if (this.format.equals("uah-classes")) {
            UahClassListTransformer.toCsv(this.importDataUri, "D:\\Source\\neo4j\\import\\scheduleSpring2016.csv");
            importDataUri = "D:\\Source\\neo4j\\import\\scheduleSpring2016.csv";
        } else if (this.format.equals("distance-matrix")) {
            DistanceMatrixTransformer.toCsv(apiKey, importDataUri, "Code", "Address", "D:\\Source\\neo4j\\import\\distanceMatrix.csv");
            importDataUri = "D:\\Source\\neo4j\\import\\distanceMatrix.csv";
        } else {
            throw new IllegalArgumentException("Bad format");
        }

        IndexIntentCommand indexing = new IndexIntentCommand(contextGraph);
        ks.Execute(indexing);
        
        return new ApplyContextOfIntentCommand(contextGraph, importDataUri, withHeaders);
    }
    
	@Override
	public void run() {
        ks.Execute(apply);
	}
}
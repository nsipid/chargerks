package com.michaelgrenon.chargerks.cli;

import charger.IOManager;
import charger.exception.CGFileException;
import charger.obj.Graph;
import chargerlib.FileFormat;
import com.michaelgrenon.chargerks.CgConverter;
import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextQuestion;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.FullKsQuestion;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.Question;
import java.io.File;
import java.util.ArrayList;

public class AskMetadataCli implements Runnable {
    private ContextInfo contextInfo;
	private Question question;
	private KnowledgeSpace ks;
	private String outputFile;

	public AskMetadataCli(KnowledgeSpace ks, String contextName, String contextType, String outputFile) {
		
		if (contextType == null) {
			this.question = new FullKsQuestion();
		} else {
			this.contextInfo = new ContextInfo(ContextType.valueOf(contextType), contextName);
			this.question = new ContextQuestion(this.contextInfo);
		}
        this.outputFile = outputFile;
        this.ks = ks;
    }

	@Override
	public void run() {
        charger.Global.setup( null, new ArrayList<String>(), false );
        
        NeoGraph neoGraph = ks.Ask(this.question);
        
        Graph outGraph = CgConverter.neoToCharger(neoGraph);
        try {
			IOManager.saveGraphAsTextFormat(outGraph, FileFormat.CHARGER4, new File(this.outputFile));
		} catch (CGFileException e) {
			throw new IllegalArgumentException("Could not write to output file.", e);
		}
	}


}
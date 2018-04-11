package com.michaelgrenon.chargerks.cli;

import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JFrame;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.cg.CgConverter;
import com.michaelgrenon.chargerks.ops.ContextQuestion;
import com.michaelgrenon.chargerks.ops.FullKsQuestion;
import com.michaelgrenon.chargerks.ops.Question;

import charger.IOManager;
import charger.exception.CGFileException;
import charger.obj.Graph;
import chargerlib.FileFormat;

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
		
		Iterator<NeoGraph> iterator = ks.Ask(this.question);
		while (iterator.hasNext()) {
			NeoGraph neoGraph = iterator.next();
			
			Graph outGraph = CgConverter.neoToCharger(neoGraph);
			try {
				IOManager.saveGraphAsTextFormat(outGraph, FileFormat.CHARGER4, new File(this.outputFile));
			} catch (CGFileException e) {
				throw new IllegalArgumentException("Could not write to output file.", e);
			} finally {
				Arrays.stream(JFrame.getFrames()).forEach(Frame::dispose);
			}
		}      
	}


}
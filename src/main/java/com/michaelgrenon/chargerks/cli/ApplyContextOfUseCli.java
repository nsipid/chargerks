package com.michaelgrenon.chargerks.cli;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.ops.ApplyContextOfUseCommand;
import com.michaelgrenon.chargerks.ops.ContextQuestion;
import com.michaelgrenon.chargerks.ops.MultiCommand;
import com.michaelgrenon.chargerks.ops.Question;

public class ApplyContextOfUseCli implements Runnable {

	private String contextOfUse;
	private KnowledgeSpace ks;

	public ApplyContextOfUseCli(KnowledgeSpace ks, String contextOfUse) {
        this.ks = ks;
        this.contextOfUse = contextOfUse;
    }
    
	@Override
	public void run() {
        Question getContext = new ContextQuestion(new ContextInfo(ContextType.USE, contextOfUse));
        Iterator<NeoGraph> iterator = ks.Ask(getContext);
        if (iterator.hasNext()) {
            NeoGraph contextGraph = iterator.next();

            MultiCommand apply = new ApplyContextOfUseCommand(contextGraph, contextOfUse);
            ks.Execute(apply);
        } else {
            throw new NoSuchElementException("Could not find context of use.");
        }       
	}
}
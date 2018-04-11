package com.michaelgrenon.chargerks.cli;

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
                NeoGraph contextGraph = ks.Ask(getContext).iterator().next();

                MultiCommand apply = new ApplyContextOfUseCommand(contextGraph, contextOfUse);
                ks.Execute(apply);
	}
}
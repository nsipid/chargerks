package com.michaelgrenon.chargerks.cli;

import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.ops.Command;
import com.michaelgrenon.chargerks.ops.DeleteCommand;

public class DeleteCli implements Runnable {
    private String contextName;
	private KnowledgeSpace ks;

	public DeleteCli(KnowledgeSpace ks, String contextName) {
        this.ks = ks;
        this.contextName = contextName;
    }

	@Override
	public void run() {
        Command c = new DeleteCommand(contextName);
		ks.Execute(c);
    }
}
package com.michaelgrenon.chargerks.cli;

import charger.IOManager;
import charger.exception.CGFileException;
import charger.obj.Graph;
import chargerlib.FileFormat;
import com.michaelgrenon.chargerks.CgConverter;
import com.michaelgrenon.chargerks.Command;
import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextQuestion;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.DeleteCommand;
import com.michaelgrenon.chargerks.FullKsQuestion;
import com.michaelgrenon.chargerks.KnowledgeSpace;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.Question;
import java.io.File;
import java.util.ArrayList;

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
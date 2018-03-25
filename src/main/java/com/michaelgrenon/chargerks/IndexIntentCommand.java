package com.michaelgrenon.chargerks;

import java.util.LinkedHashSet;

public class IndexIntentCommand implements MultiCommand {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private LinkedHashSet<String> commands;

	public IndexIntentCommand(NeoGraph intent) {
        commands = new LinkedHashSet<>();

        for (NeoConceptBinding binding : intent.getConcepts()) {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE INDEX ON :");
            builder.append(binding.getConcept().getType());
            builder.append("(referent);");
            builder.append(NEW_LINE);
            commands.add(builder.toString());  
        }
    }
	@Override
	public String[] toCypher() {
        String[] ret = new String[commands.size()];
        return commands.toArray(ret);
	}

}
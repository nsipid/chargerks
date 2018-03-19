package com.michaelgrenon.chargerks;

public class IndexIntentCommand implements Command {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private NeoGraph intent;

	public IndexIntentCommand(NeoGraph intent) {
        
        this.intent = intent;
    }
	@Override
	public String toCypher() {
        StringBuilder builder = new StringBuilder();

        for (NeoConceptBinding binding : intent.getConcepts()) {
            builder.append("CREATE INDEX ON :");
            builder.append(binding.getConcept().getType());
            builder.append("(referent);");
            builder.append(NEW_LINE);    
        }

        return builder.toString();
	}

}
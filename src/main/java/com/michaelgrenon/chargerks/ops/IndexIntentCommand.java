package com.michaelgrenon.chargerks.ops;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.summary.SummaryCounters;

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
	public List<Command> toList() {
        return commands.stream().map(comStr -> new Command() {

			@Override
			public String toCypher() {
				return comStr;
			}

			@Override
            public String getSummary(StatementResult result) {
                ResultSummary summary = result.consume();
                SummaryCounters counts = summary.counters();
                return String.format("Added %d indexes in %d ms.", counts.indexesAdded(), summary.resultAvailableAfter(TimeUnit.MILLISECONDS));
            }

        }).collect(Collectors.toList());
	}

}
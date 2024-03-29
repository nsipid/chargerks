package com.michaelgrenon.chargerks.ops;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;
import com.michaelgrenon.chargerks.NeoRelationBinding;

import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.summary.SummaryCounters;

public class ApplyContextOfUseCommand implements MultiCommand {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private NeoGraph contextOfUse;

	private String contextName;

	public ApplyContextOfUseCommand(NeoGraph contextOfUse, String contextName) {
        this.contextOfUse = contextOfUse;
        this.contextName = contextName;
    }

    @Override
    public List<Command> toList() {
        List<Criteria> criteriaList = new ArrayList<Criteria>();

        List<Command> allCommands = new ArrayList<Command>();
        for (NeoRelationBinding neoRelationBinding : contextOfUse.getRelations()) {
            NeoRelation neoRelation = neoRelationBinding.getRelation();
            String label = neoRelation.getLabel().toUpperCase().trim();
                if (label.equals("MATCHES")) {
                    allCommands.add(buildMatchCommand(neoRelation));
                } else if (label.equals("CRITERIA")) {
                    criteriaList.add(new Criteria(neoRelation));
                }
        }

        Map<NeoConceptBinding, List<Criteria>> criteriaMap = criteriaList.stream().collect(Collectors.groupingBy(c -> c.otherConcept));

        for (NeoConceptBinding otherConcept : criteriaMap.keySet()) {
            List<String> criteria = criteriaMap.get(otherConcept).stream().map(Criteria::toCypher).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
            StringBuilder critBuilder = new StringBuilder();
            critBuilder.append("MATCH ");
            critBuilder.append(otherConcept.toCypher());
            critBuilder.append(NEW_LINE);
            critBuilder.append("SET ");
            critBuilder.append(otherConcept.getVariable());
            critBuilder.append(".invalid_for_");
            critBuilder.append(contextName);
            critBuilder.append(" = ");

            Iterator<String> critItr = criteria.iterator(); 
            if (critItr.hasNext()) {

                do {
                    critBuilder.append(critItr.next());
                    if (critItr.hasNext()) {
                        critBuilder.append(" AND ");
                    }
                } while (critItr.hasNext());
            } else {
                critBuilder.append("false");
            }

            Command critCommand = new Command() {
                public String toCypher() {
                    return critBuilder.toString();
                }
    
                @Override
                public String getSummary(StatementResult result) {
                    ResultSummary summary = result.consume();
                    SummaryCounters counts = summary.counters();
                    return String.format("Marked %d nodes with criteria results in %d ms.", counts.propertiesSet(), summary.resultAvailableAfter(TimeUnit.MILLISECONDS));
                }
            };

            allCommands.add(critCommand);
        }

        return allCommands;
    }

    private Command buildMatchCommand(NeoRelation neoRelation) {
        StringBuilder builder = new StringBuilder();
        //MATCH (x1:c1Type {contextType: "STORE", contextName: "c1ContextName"}), (x2:c2Type {contextType: "STORE", contextName: "c2ContextName"})
        //WHERE x1.referent = x2.referent
        //MERGE x1-[:relationLabel {contextType:"STORE", contextName:contextOfUseName}]->x2
        NeoConceptBinding intentC1 = neoRelation.getConcept1();
        NeoConceptBinding c1 = new NeoConceptBinding(intentC1.getVariable(), new NeoConcept(intentC1.getConcept().getType(), null, new ContextInfo(ContextType.STORE, intentC1.getConcept().getContext().getName())));

        NeoConceptBinding intentC2 = neoRelation.getConcept2();
        NeoConceptBinding c2 = new NeoConceptBinding(intentC2.getVariable(), new NeoConcept(intentC2.getConcept().getType(), null, new ContextInfo(ContextType.STORE, intentC2.getConcept().getContext().getName())));

        NeoRelation instanceRelation = new NeoRelation(c1, c2, new ContextInfo(ContextType.STORE, neoRelation.getContext().getName()), neoRelation.getLabel());

        builder.append("MATCH ");
        builder.append(c1.toCypher());
        builder.append(", ");
        builder.append(c2.toCypher());
        builder.append(NEW_LINE);

        builder.append("WHERE ");
        builder.append(c1.getVariable());
        builder.append(".referent = ");
        builder.append(c2.getVariable());
        builder.append(".referent");
        builder.append(NEW_LINE);

        builder.append("MERGE ");
        builder.append(instanceRelation.toCypherExplicit());
        builder.append(NEW_LINE);

        return new Command() {
            public String toCypher() {
                return builder.toString();
            }

			@Override
			public String getSummary(StatementResult result) {
				ResultSummary summary = result.consume();
                SummaryCounters counts = summary.counters();
                return String.format("Created %d nodes and %d relationships in %d ms.", counts.nodesCreated(), counts.relationshipsCreated(), summary.resultAvailableAfter(TimeUnit.MILLISECONDS));
			}
        };
        
    }

    private class Criteria {
        public NeoConceptBinding regexConcept;
        public NeoConceptBinding otherConcept;

        public Criteria(NeoRelation neoRelation) {
            NeoConceptBinding intentC1 = neoRelation.getConcept1();
            NeoConceptBinding intentC2 = neoRelation.getConcept2();
            String c1TypeUpper = intentC1.getConcept().getType().toUpperCase().trim();
            String c2TypeUpper = intentC2.getConcept().getType().toUpperCase().trim();

            NeoConceptBinding c1 = new NeoConceptBinding(intentC1.getVariable(), new NeoConcept(intentC1.getConcept().getType(), null, new ContextInfo(ContextType.STORE, intentC1.getConcept().getContext().getName())));
            NeoConceptBinding c2 = new NeoConceptBinding(intentC2.getVariable(), new NeoConcept(intentC2.getConcept().getType(), null, new ContextInfo(ContextType.STORE, intentC2.getConcept().getContext().getName())));
            
            
            if (c1TypeUpper.equals("REGEX")) {
                regexConcept = intentC1;
                otherConcept = c2;
            } else if (c2TypeUpper.equals("REGEX")) {
                regexConcept = intentC2;
                otherConcept = c1;
            }
        }

        public Optional<String> toCypher() {
            if (regexConcept == null || otherConcept == null)
                return Optional.empty();
            return Optional.of(String.format("(NOT (%s =~ %s))", otherConcept.referToReferentAsNodeProperty(), "'"+regexConcept.getConcept().getReferent().orElse("*")+"'"));
        }
    }
}
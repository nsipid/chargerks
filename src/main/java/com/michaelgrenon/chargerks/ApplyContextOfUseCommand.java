package com.michaelgrenon.chargerks;

import cgif.generate.NameGenerator;

public class ApplyContextOfUseCommand implements Command {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private NeoGraph contextOfUse;

	public ApplyContextOfUseCommand(NeoGraph contextOfUse) {
        this.contextOfUse = contextOfUse;
    }

    @Override
    public String toCypher() {
        StringBuilder builder = new StringBuilder();

        NameGenerator generator = new NameGenerator();

        for (NeoRelation neoRelation : contextOfUse.getRelations()) {           
            //MATCH (x1:c1Type {contextType: "STORE", contextName: "c1ContextName"}), (x2:c2Type {contextType: "STORE", contextName: "c2ContextName"})
            //WHERE x1.referent = x2.referent
            //MERGE x1-[:relationLabel {contextType:"STORE", contextName:contextOfUseName}]->x2
            NeoConcept intentC1 = neoRelation.getConcept1();
            NeoConcept c1 = new NeoConcept(generator.generateName(), intentC1.getType(), null, new ContextInfo(ContextType.STORE, neoRelation.getContext().getName()));

            NeoConcept intentC2 = neoRelation.getConcept2();
            NeoConcept c2 = new NeoConcept(generator.generateName(), intentC2.getType(), null, new ContextInfo(ContextType.STORE, neoRelation.getContext().getName()));

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
        }

        return builder.toString();
    }
}
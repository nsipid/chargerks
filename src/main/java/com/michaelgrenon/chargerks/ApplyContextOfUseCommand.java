package com.michaelgrenon.chargerks;

public class ApplyContextOfUseCommand implements MultiCommand {
    private static final String NEW_LINE = System.getProperty("line.separator");

    private NeoGraph contextOfUse;

	public ApplyContextOfUseCommand(NeoGraph contextOfUse) {
        this.contextOfUse = contextOfUse;
    }

    @Override
    public String[] toCypher() {
        return contextOfUse.getRelations().stream().map(r -> buildCommand(r.getRelation())).toArray(String[]::new);
    }

    private String buildCommand(NeoRelation neoRelation) {
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

        return builder.toString();
    }
}
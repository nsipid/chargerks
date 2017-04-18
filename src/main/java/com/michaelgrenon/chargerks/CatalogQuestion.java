package com.michaelgrenon.chargerks;


public class CatalogQuestion implements Question {
    String catalog;
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    public CatalogQuestion(String catalog) {
        this.catalog = catalog;
    }
    
    @Override
    public String toCypher() {
        StringBuilder builder = new StringBuilder();
        builder.append("MATCH (a {catalog: '");
        builder.append(catalog);
        builder.append("'})-[r]-(b {catalog: '");
        builder.append(catalog);
        builder.append("'})");
        builder.append(NEW_LINE);
        builder.append("RETURN id(a), labels(a), a.referent, type(r), id(b), labels(b), b.referent");
        return builder.toString();
    }

    @Override
    public Answer getAnswer() {
        return new CatalogAnswer();
    }
    
}

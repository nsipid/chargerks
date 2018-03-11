/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

import com.michaelgrenon.chargerks.NeoGraph;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author GrenonMP
 */
public class UahClassListMetadataExtractor implements MetadataExtractor {
    private String baseUrl = "http://www.uah.edu";
    private String indexUrl;
    
    public UahClassListMetadataExtractor(String indexUrl) {
        this.indexUrl = indexUrl;
    }
    
    @Override
    public NeoGraph generateCatalog(String catalogName) throws IOException {
        String url = getUrlFromIndexUrl();
        Document doc = Jsoup.connect(url).get();
        Element pre = doc.select("pre").first();
        BorderedTableMetadataExtractor tableExtractor = new BorderedTableMetadataExtractor(new BorderedTable(pre.text(), '-'));
        return tableExtractor.generateCatalog(catalogName);
    }

    @Override
    public List<String> generateCsvHeader() throws IOException {
        String url = getUrlFromIndexUrl();
        Document doc = Jsoup.connect(url).get();
        Element pre = doc.select("pre").first();
        BorderedTableMetadataExtractor tableExtractor = new BorderedTableMetadataExtractor(new BorderedTable(pre.text(), '-'));
        return tableExtractor.generateCsvHeader();
    }

    private String getUrlFromIndexUrl() throws IOException {
        Connection connection = Jsoup.connect(this.indexUrl);
        connection.timeout(5000);
        Document index = connection.get();

        Iterator<String> links = index.select("a[href]").eachAttr("href").iterator();
        return MessageFormat.format("{0}/{1}", new Object[]{baseUrl, links.next()});
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

import com.michaelgrenon.chargerks.NeoGraph;
import java.io.IOException;
import java.text.MessageFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author GrenonMP
 */
public class UahClassListMetadataExtractor implements MetadataExtractor {
    private String url;
    
    public UahClassListMetadataExtractor(String semester, String segment, String dir) {
        this.url = MessageFormat.format("http://www.uah.edu/cgi-bin/schedule.pl?file={0}.html&dir={2}&segment={1}",
                new Object[]{semester, segment, dir});
    }
    
    public NeoGraph generateCatalog(String catalogName) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element pre = doc.select("pre").first();
        BorderedTableMetadataExtractor tableExtractor = new BorderedTableMetadataExtractor(new BorderedTable(pre.text(), '-'));
        return tableExtractor.generateCatalog(catalogName);
    }
}

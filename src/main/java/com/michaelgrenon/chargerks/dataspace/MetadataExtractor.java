/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

import com.michaelgrenon.chargerks.NeoGraph;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author GrenonMP
 */
public interface MetadataExtractor {
    NeoGraph generateCatalog(String catalogName) throws IOException;
    List<String> generateCsvHeader() throws IOException;
}

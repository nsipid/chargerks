package com.michaelgrenon.chargerks.dataspace;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.csv.*;

public class UahClassListTransformer {
    
    public static void toCsv(String fileName) throws IOException {
        UahClassIterable iterable = new UahClassIterable("sprg2016", "archived");
        UahClassListMetadataExtractor metadataExtractor = new UahClassListMetadataExtractor("sprg2016", "CS", "archived");
        String[] header = metadataExtractor.generateCsvHeader().stream().toArray(String[]::new);
        CSVFormat format = CSVFormat.DEFAULT.withHeader(header);
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        CSVPrinter printer = new CSVPrinter(writer, format);
        printer.printRecords(iterable);
        printer.flush();
        printer.close();
    }
}
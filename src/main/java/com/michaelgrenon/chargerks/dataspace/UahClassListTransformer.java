package com.michaelgrenon.chargerks.dataspace;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.csv.*;

public class UahClassListTransformer {
    public static void toCsv(String indexUrl, String fileName) throws IOException {
        //example https://www.uah.edu/cgi-bin/schedule.pl?file=sprg2016.html&segment=NDX&dir=archived

        UahClassIterable iterable = new UahClassIterable(indexUrl);
        UahClassListMetadataExtractor metadataExtractor = new UahClassListMetadataExtractor(indexUrl);
        String[] header = metadataExtractor.generateCsvHeader().stream().toArray(String[]::new);
        CSVFormat format = CSVFormat.DEFAULT.withHeader(header);
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        CSVPrinter printer = new CSVPrinter(writer, format);
        printer.printRecords(iterable);
        printer.flush();
        printer.close();
    }
}
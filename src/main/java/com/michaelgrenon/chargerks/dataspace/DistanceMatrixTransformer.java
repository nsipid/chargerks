package com.michaelgrenon.chargerks.dataspace;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.TravelMode;

import org.apache.commons.csv.*;

public class DistanceMatrixTransformer {
    public static void toCsv(String apiKey, String inputFileName, String keyColumn, String addressColumn, String outputFileName) throws IOException {
        Reader csvReader = new FileReader(inputFileName);
        CSVParser parser = new CSVParser(csvReader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        ArrayList<String> locationKeys = new ArrayList<String>();
        ArrayList<String> addresses = new ArrayList<String>();

        Iterator<CSVRecord> locItr = parser.iterator();
        while (locItr.hasNext()) {
            CSVRecord record = locItr.next();
            locationKeys.add(record.get(keyColumn));
            addresses.add(record.get(addressColumn));
        }

        String[] aArray = new String[addresses.size()];
        aArray = addresses.toArray(aArray);

        String[] lArray = new String[locationKeys.size()];
        lArray = locationKeys.toArray(lArray);

        DistanceMatrixIterable iterable = new DistanceMatrixIterable(apiKey, lArray, aArray);

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        String[] header = {"origin", "destination", "duration"};

        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(header));
        printer.printRecords(iterable);
        printer.flush();
        printer.close();
        parser.close();
    }
}
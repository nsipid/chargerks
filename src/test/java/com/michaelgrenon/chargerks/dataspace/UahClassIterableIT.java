/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

import java.io.BufferedWriter;
import java.io.FileWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class UahClassIterableIT {
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of generateCatalog method, of class UahClassListMetadataExtractor.
     */
    @org.junit.Test
    public void testCsv() throws Exception {
        String fileName = "C:\\users\\nsipi\\classes.csv";
        UahClassIterable iterable = new UahClassIterable("sprg2016", "archived");
        CSVFormat format = CSVFormat.DEFAULT;
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        CSVPrinter printer = new CSVPrinter(writer, format);
        printer.printRecords(iterable);
        printer.flush();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

import static org.junit.Assert.assertTrue;

import java.util.List;

import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author GrenonMP
 */
public class UahClassListMetadataExtractorIT {
    
    public UahClassListMetadataExtractorIT() {
    }
    
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
    public void testGenerateCatalog() throws Exception {
        String catalogName = "CourseListing";
        UahClassListMetadataExtractor instance = new UahClassListMetadataExtractor("https://www.uah.edu/cgi-bin/schedule.pl?file=sprg2016.html&segment=NDX&dir=archived");
        NeoGraph result = instance.generateCatalog(catalogName);
        List<NeoConceptBinding> concepts = result.getConcepts();
        assertContainsConceptOfType(concepts, "Sec Type");
        assertContainsConceptOfType(concepts, "CRN");
        assertContainsConceptOfType(concepts, "Course");
        assertContainsConceptOfType(concepts, "Title");
        assertContainsConceptOfType(concepts, "Credit");
        assertContainsConceptOfType(concepts, "Max Enrl");
        assertContainsConceptOfType(concepts, "Enrl");
        assertContainsConceptOfType(concepts, "Avail");
        assertContainsConceptOfType(concepts, "Wait List");
        assertContainsConceptOfType(concepts, "Days");
        assertContainsConceptOfType(concepts, "Start");
        assertContainsConceptOfType(concepts, "End");
        assertContainsConceptOfType(concepts, "Bldg");
        assertContainsConceptOfType(concepts, "Room");
        assertContainsConceptOfType(concepts, "Instructor");
    }
    
    private void assertContainsConceptOfType(List<NeoConceptBinding> concepts, String type) {
        assertTrue(concepts.stream().anyMatch(concept -> concept.getConcept().getType().equals(type)));
    }
    
}

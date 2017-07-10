/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.dataspace;

import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoGraph;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
        UahClassListMetadataExtractor instance = new UahClassListMetadataExtractor("fall2015", "CS", "archived");
        NeoGraph result = instance.generateCatalog(catalogName);
        List<NeoConcept> concepts = result.getConcepts();
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
    
    private void assertContainsConceptOfType(List<NeoConcept> concepts, String type) {
        assertTrue(concepts.stream().anyMatch(concept -> concept.getType().equals(type)));
    }
    
}

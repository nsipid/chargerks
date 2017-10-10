/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class CgConverterUnitTests {
    NeoGraph exampleNeoGraph;
    
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
    public void testNeoToCharger() throws Exception {
    
    }
    
    public void getExampleNeoGraph() {
        ArrayList<?> concepts = new ArrayList<NeoConcept>();
        ContextInfo catA = new ContextInfo(ContextType.INTENT, "catA");
        NeoConcept studentCatA = new NeoConcept("student", "Student", "", catA);
        NeoConcept tableStudentCatA = new NeoConcept("tableStudent", "Table", "Student", catA);
        NeoConcept studentIdCatA = new NeoConcept("studentId", "StudentId", "", catA);
        NeoConcept nameCatA = new NeoConcept("name", "Name", "", catA);
        NeoConcept majorCatA = new NeoConcept("major", "Major","",catA);
        NeoConcept freshmanCatA = new NeoConcept("freshman", "Freshman", "", catA);
        NeoConcept eng1CatA = new NeoConcept("eng1", "English", "Student who is...", catA);
        NeoConcept eng2CatA = new NeoConcept("eng2", "English", "1st year student...", catA);
        NeoConcept eng3CatA = new NeoConcept("eng3", "English", "Unique id...", catA);
        NeoConcept eng4CatA = new NeoConcept("eng4", "English", "Full name...", catA);
        NeoConcept eng5CatA = new NeoConcept("eng5", "English", "Undergraduate degree...", catA);
        NeoRelation rel1CatA = new NeoRelation(tableStudentCatA, studentIdCatA, catA, "SCHEMA_DECLARES");
        NeoRelation rel2CatA = new NeoRelation(tableStudentCatA, nameCatA, catA, "SCHEMA_DECLARES");
        NeoRelation rel3CatA = new NeoRelation(tableStudentCatA, majorCatA, catA, "SCHEMA_DECLARES");
        NeoRelation rel4CatA = new NeoRelation(studentCatA, eng1CatA, catA, "DESCRIBED_AS");
        NeoRelation rel5CatA = new NeoRelation(freshmanCatA, eng2CatA, catA, "DESCRIBED_AS");
        NeoRelation rel6CatA = new NeoRelation(studentIdCatA, eng3CatA, catA, "DESCRIBED_AS");
        NeoRelation rel7CatA = new NeoRelation(nameCatA, eng4CatA, catA, "DESCRIBED_AS");
        NeoRelation rel8CatA = new NeoRelation(majorCatA, eng5CatA, catA, "DESCRIBED_AS");
  
        
    }
}

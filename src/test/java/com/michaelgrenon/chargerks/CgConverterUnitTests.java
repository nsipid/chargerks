/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import charger.IOManager;
import charger.exception.CGFileException;
import charger.obj.Graph;
import chargerlib.FileFormat;
import java.io.File;
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
        charger.Global.setup( null, new ArrayList<String>(), false );
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
    public void testNeoToCharger() throws Exception, CGFileException {
        Graph chargerGraph = CgConverter.neoToCharger(getExampleNeoGraph());
        IOManager.saveGraphAsTextFormat(chargerGraph, FileFormat.CHARGER4, new File("C:\\Users\\GrenonMP\\test.cgx"));
    }
    
    public NeoGraph getExampleNeoGraph() {
        ArrayList<NeoConcept> concepts = new ArrayList<NeoConcept>();
        ArrayList<NeoRelation> relations = new ArrayList<NeoRelation>();
        
        ContextInfo catA = new ContextInfo(ContextType.INTENT, "catA");
        NeoConcept studentCatA = new NeoConcept("student", "Student", "", catA);
          
        ContextInfo catB = new ContextInfo(ContextType.INTENT, "catB");
        NeoConcept studentCatB = new NeoConcept("student", "Student", "", catB);
        
        ContextInfo relA = new ContextInfo(ContextType.USE, "relA");
        NeoRelation relAMatches = new NeoRelation(studentCatA, studentCatB, relA, "MATCHES");
        
        concepts.add(studentCatA);
        concepts.add(studentCatB);
        
        relations.add(relAMatches);
        
        return new NeoGraph(concepts, relations);
    }
}

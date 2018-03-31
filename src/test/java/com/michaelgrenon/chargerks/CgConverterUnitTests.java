/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import charger.IOManager;
import charger.exception.CGFileException;
import charger.obj.Coref;
import charger.obj.DeepIterator;
import charger.obj.Graph;
import charger.obj.GraphObject;
import charger.obj.GraphObjectIterator;
import charger.obj.ShallowIterator;
import charger.xml.CGXParser;
import chargerlib.FileFormat;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

    @Test
    public void testNeoToCharger() throws Exception, CGFileException {
        Graph chargerGraph = CgConverter.neoToCharger(getExampleNeoGraph());
        List<Graph> contexts = chargerGraph.getGraphObjects().stream().filter(Graph.class::isInstance).map(Graph.class::cast).collect(Collectors.toList());
        assertEquals(3, contexts.size());
        assertEquals(true, contexts.stream().anyMatch(g -> g.getTypeLabel().equals(ContextType.INTENT.toString()) && g.getReferent().equals("catA")));
        assertEquals(true, contexts.stream().anyMatch(g -> g.getTypeLabel().equals(ContextType.INTENT.toString()) && g.getReferent().equals("catB")));
        assertEquals(true, contexts.stream().anyMatch(g -> g.getTypeLabel().equals(ContextType.USE.toString()) && g.getReferent().equals("relA")));
        IOManager.saveGraphAsTextFormat(chargerGraph, FileFormat.CHARGER4, new File("C:\\Users\\nsipi\\test.cgx"));
    }
    
    @Test
    public void testChargerToNeo() throws Exception, CGFileException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream chargerStream = classLoader.getResourceAsStream("Coreference.cgx");

        Graph origGraph = new Graph();
        CGXParser.parseForNewGraph(chargerStream, origGraph);

        NeoGraph neoGraph = CgConverter.chargerToNeo(origGraph); 
        
        Graph verifyGraph = CgConverter.neoToCharger(neoGraph);
        IOManager.saveGraphAsTextFormat(verifyGraph, FileFormat.CHARGER4, new File("C:\\Users\\nsipi\\testverify.cgx"));
    }
    
    public NeoGraph getExampleNeoGraph() {
        ArrayList<NeoConceptBinding> concepts = new ArrayList<NeoConceptBinding>();
        ArrayList<NeoRelationBinding> relations = new ArrayList<NeoRelationBinding>();
        
        ContextInfo catA = new ContextInfo(ContextType.INTENT, "catA");
        NeoConceptBinding studentCatA = new NeoConceptBinding("student", new NeoConcept("Student", "", catA));
          
        ContextInfo catB = new ContextInfo(ContextType.INTENT, "catB");
        NeoConceptBinding studentCatB = new NeoConceptBinding("student", new NeoConcept("Student", "", catB));
        
        ContextInfo relA = new ContextInfo( ContextType.USE, "relA");
        NeoRelation relAMatches = new NeoRelation(studentCatA, studentCatB, relA, "MATCHES");
        
        concepts.add(studentCatA);
        concepts.add(studentCatB);
        
        relations.add(new NeoRelationBinding("var", relAMatches));
        
        return new NeoGraph(concepts, relations);
    }
}

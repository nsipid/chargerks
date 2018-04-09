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

import java.awt.Frame;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;

import com.michaelgrenon.chargerks.ContextInfo;
import com.michaelgrenon.chargerks.ContextType;
import com.michaelgrenon.chargerks.NeoConcept;
import com.michaelgrenon.chargerks.NeoConceptBinding;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.NeoRelation;
import com.michaelgrenon.chargerks.NeoRelationBinding;
import com.michaelgrenon.chargerks.cg.CgConverter;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class ApplyContextOfUseUnitTests {
    NeoGraph exampleNeoGraph;
    
    @BeforeClass
    public static void setUpClass() {
        charger.Global.setup( null, new ArrayList<String>(), false );
        Arrays.stream(JFrame.getFrames()).forEach(f -> f.setVisible(false));
    }
    
    @AfterClass
    public static void tearDownClass() {
        Arrays.stream(JFrame.getFrames()).forEach(Frame::dispose);
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSchedulingOffice() throws Exception, CGFileException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream chargerStream = classLoader.getResourceAsStream("SchedulingOfficeUse.cgx");

        Graph origGraph = new Graph();
        CGXParser.parseForNewGraph(chargerStream, origGraph);

        NeoGraph neoGraph = CgConverter.chargerToNeo(origGraph);
        MultiCommand command = new ApplyContextOfUseCommand(neoGraph, "schedulingOffice");
        String[] cypher = command.toCypher();
        assertNotNull(cypher);
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import static org.junit.Assert.assertNotNull;

import java.awt.Frame;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;

import com.michaelgrenon.chargerks.cg.CgConverter;
import com.michaelgrenon.chargerks.ops.ApplyContextOfUseCommand;
import com.michaelgrenon.chargerks.ops.MultiCommand;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import charger.exception.CGFileException;
import charger.obj.Graph;
import charger.xml.CGXParser;

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
        String[] cypher = command.toList().stream().map(c -> c.toCypher()).toArray(String[]::new);
        assertNotNull(cypher);
    }
}
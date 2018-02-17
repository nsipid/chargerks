/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import java.util.function.Supplier;

import com.michaelgrenon.chargerks.KnowledgeSpace;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class MainCli {
    public static final void main(final String[] args) {
        Option input = Option.builder("i").argName("input")
                .desc("input cgx graph file or database uri")
                .required()
                .longOpt("input")
                .build();
        
        Option output = Option.builder("o").argName("file")
                .desc("output cgx graph")
                .required()
                .longOpt("output")
                .build();
        
        Option uri = Option.builder().argName("uri")
                .desc("bolt uri to the neo4j instance")
                .longOpt("uri")
                .build();
        
        Option username = Option.builder().argName("user")
                .desc("neo4j user name")
                .longOpt("user")
                .build();
        
        Option password = Option.builder().argName("password")
                .desc("neo4j password")
                .build();
        
        Option contextName = Option.builder("c").argName("context name")
                .desc("name of the context of use/intent of the query/command")
                .longOpt("contextName")
                .build();

        Option contextType = Option.builder("t").argName("context type")
                .desc("type of context: use, intent")
                .longOpt("contextType")
                .build();

        Option format = Option.builder("f").argName("data format")
                .desc("input database format: csv, json, distance-matrix, uah-classes")
                .longOpt("format")
                .build();
        
        Options options = new Options();
        options.addOption(input);
        options.addOption(output);
        options.addOption(contextName);
        options.addOption(contextType);
        options.addOption(uri);
        options.addOption(username);
        options.addOption(password);
        options.addOption(format);
        
        CommandLineParser parser = new DefaultParser();
        
        Runnable helpRunner = () -> {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar chargerks [options] [command/query] ", options);
            System.exit(1);
        };
        
        try {
            CommandLine cmdLine = parser.parse(options, args);
            String[] remainingArgs = cmdLine.getArgs();
            
            String uriArg = "bolt://localhost:7687";
            if (cmdLine.hasOption("uri")) {
                uriArg = cmdLine.getParsedOptionValue("uri").toString();
            }
            
            String userArg = "neo4j";
            if (cmdLine.hasOption("user")) {
                userArg = cmdLine.getParsedOptionValue("user").toString();
            }
            
            String passArg = "random";
            if (cmdLine.hasOption("password")) {
                passArg = cmdLine.getParsedOptionValue("password").toString();
            }
            
            if (remainingArgs.length != 1) {
                helpRunner.run();
            }
            
            String commandString = remainingArgs[0].toLowerCase(); 
            
            KnowledgeSpace ks = new KnowledgeSpace(uriArg, userArg, passArg);
            ks.open();
            switch (commandString) {
                case "merge":
                    break;
                case "extract-metadata":
                    break;
                case "load-data":
                    break;
                case "delete":
                    break;
                case "ask":
                    break;
                default:
                    helpRunner.run();
            }
            
        } catch (ParseException e) {
            helpRunner.run();
        }
    }  
}

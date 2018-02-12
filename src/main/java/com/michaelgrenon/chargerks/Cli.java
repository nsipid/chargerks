/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.michaelgrenon.chargerks;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import java.util.function.Supplier;

/**
 *
 * @author Michael Grenon <grenonm@uah.edu>
 */
public class Cli {
    public static final void main(final String[] args) {
        Option input = Option.builder("i").argName("input")
                .desc("input cgx graph or database")
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
        
        Options options = new Options();
        options.addOption(input);
        options.addOption(output);
        options.addOption(contextName);
        options.addOption(uri);
        options.addOption(username);
        options.addOption(password);
        
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
            
            String filename = remainingArgs[0]; 
            
            KnowledgeSpace ks = new KnowledgeSpace(uriArg, userArg, passArg);
            ks.open();
            switch (remainingArgs[0].toLowerCase()) {
                case "merge":
                    break;
                case "extract-metadata":
                    break;
                case "load-data":
                    break;
                case "set":
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

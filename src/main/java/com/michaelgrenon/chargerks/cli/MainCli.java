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
                .hasArg(true)
                .longOpt("input")
                .build();
        
        Option output = Option.builder("o").argName("file")
                .desc("output cgx graph")
                .hasArg(true)
                .longOpt("output")
                .build();
        
        Option uri = Option.builder().argName("uri")
                .desc("bolt uri to the neo4j instance")
                .hasArg(true)
                .longOpt("uri")
                .build();
        
        Option username = Option.builder().argName("user")
                .desc("neo4j user name")
                .hasArg(true)
                .longOpt("user")
                .build();
        
        Option password = Option.builder().argName("password")
                .desc("neo4j password")
                .hasArg(true)
                .longOpt("password")
                .build();
        
        Option contextName = Option.builder("c").argName("context name")
                .desc("name of the context of use/intent of the query/command")
                .hasArg(true)
                .longOpt("contextName")
                .build();

        Option contextType = Option.builder("t").argName("context type")
                .desc("type of context: use, intent")
                .hasArg(true)
                .longOpt("contextType")
                .build();

        Option format = Option.builder("f").argName("data format")
                .desc("input database format: csv, csv-header, json, distance-matrix, uah-classes")
                .longOpt("format")
                .hasArg(true)
                .build();
        
        Option apikey = Option.builder("a").argName("api key")
                .desc("api key for input operations that need it, such as google distance-matrix")
                .longOpt("apikey")
                .hasArg(true)
                .build();
        
        Option limit = Option.builder("l").argName("limit")
                .desc("limits the number of results returned from the knowledge space")
                .longOpt("limit")
                .hasArg(true)
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
        options.addOption(apikey);
        
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
            
            String inputArg = null;
            if (cmdLine.hasOption("input")) {
                inputArg = cmdLine.getParsedOptionValue("input").toString();
            }

            String outputArg = null;
            if (cmdLine.hasOption("output")) {
                outputArg = cmdLine.getParsedOptionValue("output").toString();
            }

            String contextNameArg = null;
            if (cmdLine.hasOption("contextName")) {
                contextNameArg = cmdLine.getParsedOptionValue("contextName").toString();
            }

            String contextTypeArg = null;
            if (cmdLine.hasOption("contextType")) {
                contextTypeArg = cmdLine.getParsedOptionValue("contextType").toString();
            }

            String formatArg = null;
            if (cmdLine.hasOption("format")) {
                formatArg = cmdLine.getParsedOptionValue("format").toString();
            }

            String apikeyArg = null;
            if (cmdLine.hasOption("apikey")) {
                apikeyArg = cmdLine.getParsedOptionValue("apikey").toString();
            }

            int limitArg = 2000;
            if (cmdLine.hasOption("limit")) {
                limitArg = Integer.parseInt(cmdLine.getParsedOptionValue("limit").toString());
            }

            if (remainingArgs.length != 1) {
                helpRunner.run();
            }
            
            String commandString = remainingArgs[0].toLowerCase(); 
            
            KnowledgeSpace ks = new KnowledgeSpace(uriArg, userArg, passArg);
            ks.open();
            Runnable cmd;
            switch (commandString) {                
                case "merge-metadata":
                    cmd = new MergeMetadataCli(ks, inputArg);
                    cmd.run();
                    break;
                case "extract-metadata":
                    cmd = new ExtractMetadataCli(contextNameArg, formatArg, inputArg, outputArg);
                    cmd.run();
                    break;
                case "apply-intent":
                    cmd = new ApplyContextOfIntentCli(ks, contextNameArg, formatArg, inputArg, apikeyArg);
                    cmd.run();
                    break;
                case "apply-use":
                    cmd = new ApplyContextOfUseCli(ks, contextNameArg);
                    cmd.run();
                    break;
                case "delete-context":
                    cmd = new DeleteCli(ks, contextNameArg);
                    cmd.run();
                    break;
                case "ask-metadata":
                    cmd = new AskMetadataCli(ks, contextNameArg, contextTypeArg, outputArg);
                    cmd.run();
                    break;
                case "ask-data":
                    cmd = new AskDataCli(ks, inputArg, outputArg, contextNameArg, limitArg);
                    cmd.run();
                    break;
                default:
                    helpRunner.run();
            }
            
        } catch (ParseException e) {
            helpRunner.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }  
}

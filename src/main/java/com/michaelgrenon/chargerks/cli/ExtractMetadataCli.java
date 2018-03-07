package com.michaelgrenon.chargerks.cli;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import charger.IOManager;
import charger.exception.CGFileException;
import charger.obj.Graph;
import chargerlib.FileFormat;
import com.michaelgrenon.chargerks.CgConverter;
import com.michaelgrenon.chargerks.NeoGraph;
import com.michaelgrenon.chargerks.dataspace.CsvMetadataExtractor;
import com.michaelgrenon.chargerks.dataspace.UahClassListMetadataExtractor;

public class ExtractMetadataCli implements Runnable {

    private String contextName;
	private String format;
	private String inputUri;
	private String outFile;


	public ExtractMetadataCli(String contextName, String format, String inputUri, String outFile) throws IOException {
        this.contextName = contextName;
        this.format = format;
        this.inputUri = inputUri;
        this.outFile = outFile;
    }


	@Override
	public void run() {
            try {
            charger.Global.setup( null, new ArrayList<String>(), false );
            
            NeoGraph metaGraph = null;
            switch (format.toLowerCase()) {
                case "csv":
                    Reader csvReader = new FileReader(inputUri);
                    CsvMetadataExtractor csvExtractor = new CsvMetadataExtractor(csvReader, false);
                    metaGraph = csvExtractor.generateCatalog(contextName);
                    break;
                case "csv-header":
                    Reader csvHeaderReader = new FileReader(inputUri);
                    CsvMetadataExtractor csvHeaderExtractor = new CsvMetadataExtractor(csvHeaderReader, true);
                    metaGraph = csvHeaderExtractor.generateCatalog(contextName);
                    break;
                case "uah-classes":
                    UahClassListMetadataExtractor uahClassExtractor = new UahClassListMetadataExtractor(inputUri);
                    metaGraph = uahClassExtractor.generateCatalog(contextName);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown format.");
            }

            Graph outGraph = CgConverter.neoToCharger(metaGraph);
            IOManager.saveGraphAsTextFormat(outGraph, FileFormat.CHARGER4, new File(outFile));
            
		} catch (CGFileException e) {
			throw new IllegalArgumentException("Could not write to output file.", e);
		} catch (IOException e) {
            throw new IllegalArgumentException("Could not read to input data.", e);
        }
	}
}
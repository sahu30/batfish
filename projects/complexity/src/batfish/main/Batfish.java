package batfish.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.commons.io.FileUtils;


import batfish.grammar.cisco.CiscoGrammar;
import batfish.grammar.cisco.CiscoGrammarCommonLexer;



public class Batfish {
	//Map<String, Integer> complexity=new TreeMap<String, Integer>();
	Map<String, CiscoGrammar> configs = new TreeMap<String, CiscoGrammar>();
	private String readFile(File file) throws Exception {
		String text = null;
		try {
			text = FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new Exception("Failed to read file: " + file.toString(), e);
		}
		return text;
	}

	public Map<File, String> readConfigurationFiles(String testRigPath)
			throws Exception {
		System.out.print("\n*** READING CONFIGURATION FILES ***\n");

		Map<File, String> configurationData = new TreeMap<File, String>();
		File configsPath = new File(new File(testRigPath), "configs");
		File[] configFilePaths = configsPath.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
		});
		if (configFilePaths == null) {
			throw new Exception("Error reading test rig configs directory");
		}
		for (File file : configFilePaths) {
			System.out.print("Reading: \"" + file.toString() + "\"\n");
			String fileText = readFile(file.getAbsoluteFile());
			configurationData.put(file, fileText);
		}
		return configurationData;
	}

	
	public void parseVendorConfigurations(
			Map<File, String> configurationData) {
		System.out.println("\n*** PARSING VENDOR CONFIGURATION FILES ***\n");
	//	Map<String, Integer> vendorConfigurations = new TreeMap<String, Integer>();
		for (File currentFile : configurationData.keySet()) {
			String fileText = configurationData.get(currentFile);
			String currentPath = currentFile.getAbsolutePath();

			CiscoGrammarCommonLexer lexer;
			CiscoGrammar parser;
			ANTLRInputStream inputStream = new ANTLRInputStream(fileText);
			lexer = new CiscoGrammarCommonLexer(inputStream);
			CommonTokenStream tokens = new CommonTokenStream((TokenSource) lexer);
			parser = new CiscoGrammar((TokenStream) tokens);
			parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
	//		CiscoControlPlaneComplexity extractor = new CiscoControlPlaneComplexity();

			lexer.addErrorListener(new BaseErrorListener() {
		        @Override
		        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		            throw new IllegalStateException("failed to token at line " + line + " due to " + msg, e);
		        }
		    });
			
			
			parser.addErrorListener(new BaseErrorListener() {
		        @Override
		        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		            throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
		        }
		    });
			
			System.out.print("parsing "+currentPath+" ");
			try{
				if (fileText.charAt(0) == '!') {
					parser.cisco_configuration();
					configs.put(currentFile.getName(), parser);
		//			complexity.put(currentFile.getName(), parser.getComplexity());
					System.out.println("... OK");
				} else {
					System.out.println("... non-cisco Error\n");
				}
			}
			catch(Exception e){
				System.out.println("... parse Error");
			}
		}
	}
	public void outputIntraFileComplexity(String path){
		String file = path+"/"+"intra_complexity.txt";
		Writer writer  = null;
		double total = 0;
		int count = 0;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		    for(String net: configs.keySet()){
		    	Integer comp= configs.get(net).getComplexity();
		    	writer.write(net+":"+comp+"\n");
		    	total += comp;
		    	count++;
		    }
		    writer.write("average:"+(total/count));
		    writer.close();
		} catch (Exception ex) {
		    
		}
	}
	public void outputInterFileComplexity(String path){
		for(String net: configs.keySet()){
			CiscoGrammar g = configs.get(net);
			g.ProcessOspfReferences();
		}
		
		
		String file = path+"/"+"inter_complexity.txt";
		Writer writer  = null;
		int count = 0;
		
		for(String net1: configs.keySet()){
			for(String net2: configs.keySet()){
				if(net1.equals(net2)) continue;
            System.out.println("********from:"+net1+" to:"+net2+"*******");
				CiscoGrammar g1 = configs.get(net1);
				CiscoGrammar g2 = configs.get(net2);
				if(g1.OspfReferenced(g2)){
					count++;
					continue;
				}
				else if(g1.BgpReferenced(g2)){
System.out.println("find bgp reference");
					count++;
					continue;
				}
			}
		}
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		    writer.write(""+count);
		    writer.close();
		} catch (Exception ex) {
		    
		}
	}
}

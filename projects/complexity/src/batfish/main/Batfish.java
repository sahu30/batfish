package batfish.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import batfish.grammar.cisco.complexityUtil;



public class Batfish {
	//Map<String, Integer> complexity=new TreeMap<String, Integer>();
	Map<String, complexityUtil> configs = new TreeMap<String, complexityUtil>();
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
					configs.put(currentFile.getName(), parser.comp);
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
		    	Integer comp= configs.get(net).getIntraComplexity();
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
	   /*
		for(String net: configs.keySet()){
			CiscoGrammar g = configs.get(net);
			g.ProcessOspfReferences();
		}
		*/
		
		String file = path+"/"+"inter_complexity.txt";
		Writer writer  = null;
		int ibgpCount = 0;
		int ebgpCount = 0;
		int ospfCount = 0;
		HashSet<String> ases = new HashSet<String>();
		for(String net: configs.keySet()){
		   complexityUtil comp = configs.get(net);
		   // ibgp
		   ibgpCount+=comp.ibgpNeighbors.size();
		   // ebgp
		   ases.add(comp.bgpAs);
		}
		// ebgp
		for(String net: configs.keySet()){
		   complexityUtil comp = configs.get(net);
		   List<String> neighbors = comp.ebgpNeighbors;
		   for(String n: neighbors){
		      if(ases.contains(n))
		         ebgpCount++;
		   }
		}
		// ospf
      for(String net1: configs.keySet()){
         for(String net2: configs.keySet()){
            if(net1.equals(net2)) continue;
            complexityUtil comp1 = configs.get(net1);
            complexityUtil comp2 = configs.get(net2);
            if(comp1.OspfReferenced(comp2)){
               ospfCount++;
            }
         }
      }
         
		
		
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		    writer.write(""+ibgpCount+" "+ebgpCount+" "+ospfCount+" "+(ibgpCount+ebgpCount)
		          +" "+(ibgpCount+ebgpCount+ospfCount));
		    writer.close();
		} catch (Exception ex) {
		    
		}
	}
}

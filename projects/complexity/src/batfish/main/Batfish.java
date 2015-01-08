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
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.apache.commons.io.FileUtils;












import batfish.complexity.complexityUtil;
import batfish.grammar.arista.AristaGrammar;
import batfish.grammar.arista.AristaGrammarCommonLexer;
import batfish.grammar.cisco.CiscoGrammar;
import batfish.grammar.cisco.CiscoGrammarCommonLexer;
import batfish.grammar.flatjuniper.FlatJuniperLexer;
import batfish.grammar.flatjuniper.FlatJuniperParser;
import batfish.grammar.quanta.QuantaGrammar;
import batfish.grammar.quanta.QuantaGrammarCommonLexer;



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

			Lexer lexer;
			Parser parser;
			ANTLRInputStream inputStream = new ANTLRInputStream(fileText);
			lexer = createLexer(currentFile, inputStream);
			CommonTokenStream tokens = new CommonTokenStream((TokenSource) lexer);
			parser = createParser(currentFile, tokens);
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
			   Parse(currentFile, parser);
			   configs.put(currentFile.getName(), getComplexity(currentFile, parser));
				System.out.println("... OK");
			}
			catch(Exception e){
				System.out.println("... parse Error");
			}
		}
	}
	public Lexer createLexer(File file, ANTLRInputStream inputStream){
	   String filename = file.getName();
	   if(filename.startsWith("cisco")){
	      return new CiscoGrammarCommonLexer(inputStream);
	   }
	   else if(filename.startsWith("arista")){
	      return new AristaGrammarCommonLexer(inputStream);
	   }
	   else if(filename.startsWith("quanta")){
	      return new QuantaGrammarCommonLexer(inputStream);
	   }
	   else if(filename.startsWith("juniper")){
	      return new FlatJuniperLexer(inputStream);
	   }
	   System.out.println("no lexer for file "+file);
	   return null;
	}
	public Parser createParser(File file, TokenStream tokens){
	   String filename = file.getName();
      if(filename.startsWith("cisco")){
         return new CiscoGrammar(tokens);
      }
      else if(filename.startsWith("arista")){
         return new AristaGrammar(tokens);
      }
      else if(filename.startsWith("quanta")){
         return new QuantaGrammar(tokens);
      }
      else if(filename.startsWith("juniper")){
         return new FlatJuniperParser(tokens);
      }
      System.out.println("no parser for file "+file);
      return null;
	}
	public void Parse(File file, Parser parser){
	   String filename = file.getName();
	   if(filename.startsWith("cisco")){
	      CiscoGrammar cisco = (CiscoGrammar)parser;
	      cisco.cisco_configuration();
	   }
	   else if(filename.startsWith("arista")){
	      AristaGrammar arista = (AristaGrammar) parser;
	      arista.arista_configuration();
	   }
	   else if(filename.startsWith("quanta")){
	      QuantaGrammar quanta = (QuantaGrammar) parser;
	      quanta.quanta_configuration();
	   }
	   else if(filename.startsWith("juniper")){
	      FlatJuniperParser juniper = (FlatJuniperParser) parser;
	      juniper.flat_juniper_configuration();
	   }
	   else{
	      System.out.println("unknown device type Error");
	   }
	}
	public complexityUtil getComplexity(File file, Parser parser){
	   String filename =file.getName();
	   if(filename.startsWith("cisco")){
	      CiscoGrammar cisco = (CiscoGrammar)parser;
	      return cisco.comp;
	   }
	   else if(filename.startsWith("arista")){
	      AristaGrammar arista = (AristaGrammar)parser;
	      return arista.comp;
	   }
	   else if(filename.startsWith("quanta")){
	      QuantaGrammar quanta = (QuantaGrammar)parser;
	      return quanta.comp;
	   }
	   else if(filename.startsWith("juniper")){
	      FlatJuniperParser juniper = (FlatJuniperParser) parser;
	      return juniper.comp;
	   }
	   return null;
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
		    writer.write("average:"+(total/count)+"\n");
		    writer.write("total:"+total);
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
		      if(ases.contains(n)){
//System.out.println("find ebgp: "+n);		         
		         ebgpCount++;
		      }
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

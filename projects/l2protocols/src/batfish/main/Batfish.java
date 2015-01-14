package batfish.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.io.Writer;

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

import batfish.grammar.arista.AristaGrammar;
import batfish.grammar.arista.AristaGrammarCommonLexer;
import batfish.grammar.cisco.CiscoGrammar;
import batfish.grammar.cisco.CiscoGrammarCommonLexer;
import batfish.grammar.flatjuniper.FlatJuniperLexer;
import batfish.grammar.flatjuniper.FlatJuniperParser;
import batfish.grammar.quanta.QuantaGrammar;
import batfish.grammar.quanta.QuantaGrammarCommonLexer;


public class Batfish {
   String configPath;
   File configFile;
   String configContent;

	public void readConfigurationFiles(String testRigPath)
			throws Exception {
		configPath = testRigPath;

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
      assert configFilePaths.length == 1;
      for (File file : configFilePaths) {
         configFile = file;
         configContent = FileUtils.readFileToString(file);
      }
	}

   public void parseVendorConfigurations() {
      System.out.println("\n*** PARSING VENDOR CONFIGURATION FILES ***\n");
      String currentPath = configFile.getAbsolutePath();

      Lexer lexer;
      Parser parser;
      ANTLRInputStream inputStream = new ANTLRInputStream(configContent);
      lexer = createLexer(inputStream);
      CommonTokenStream tokens = new CommonTokenStream((TokenSource) lexer);
      parser = createParser(tokens);
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

      AddErrorListener(lexer, parser);
      System.out.print("parsing " + currentPath + " ");
      try {
         Parse(parser);
         Output(parser);
      } catch (Exception e) {
      }
   }
   
   private void AddErrorListener(Lexer lexer, Parser parser){
      lexer.addErrorListener(new BaseErrorListener() {
         @Override
         public void syntaxError(Recognizer<?, ?> recognizer,
               Object offendingSymbol, int line, int charPositionInLine,
               String msg, RecognitionException e) {
            throw new IllegalStateException("failed to token at line " + line
                  + " due to " + msg, e);
         }
      });

      parser.addErrorListener(new BaseErrorListener() {
         @Override
         public void syntaxError(Recognizer<?, ?> recognizer,
               Object offendingSymbol, int line, int charPositionInLine,
               String msg, RecognitionException e) {
            throw new IllegalStateException("failed to parse at line " + line
                  + " due to " + msg, e);
         }
      });
   }
   
	public Lexer createLexer(ANTLRInputStream inputStream){
	   String filename = configFile.getName();
	   if(filename.startsWith("Cisco")){
	      return new CiscoGrammarCommonLexer(inputStream);
	   }
	   else if(filename.startsWith("Arista")){
	      return new AristaGrammarCommonLexer(inputStream);
	   }
	   else if(filename.startsWith("Quanta")){
	      return new QuantaGrammarCommonLexer(inputStream);
	   }
	   else if(filename.startsWith("Juniper")){
	      return new FlatJuniperLexer(inputStream);
	   }
	   System.out.println("no lexer for file "+configFile);
	   return null;
	}
	public Parser createParser(TokenStream tokens){
	   String filename = configFile.getName();
      if(filename.startsWith("Cisco")){
         return new CiscoGrammar(tokens);
      }
      else if(filename.startsWith("Arista")){
         return new AristaGrammar(tokens);
      }
      else if(filename.startsWith("Quanta")){
         return new QuantaGrammar(tokens);
      }
      else if(filename.startsWith("Juniper")){
         return new FlatJuniperParser(tokens);
      }
      System.out.println("no parser for file "+configFile);
      return null;
	}
	public void Parse(Parser parser){
	   String filename = configFile.getName();
	   if(filename.startsWith("Cisco")){
	      CiscoGrammar cisco = (CiscoGrammar)parser;
	      cisco.cisco_configuration();
	   }
	   else if(filename.startsWith("Arista")){
	      AristaGrammar arista = (AristaGrammar) parser;
	      arista.arista_configuration();
	   }
	   else if(filename.startsWith("Quanta")){
	      QuantaGrammar quanta = (QuantaGrammar) parser;
	      quanta.quanta_configuration();
	   }
	   else if(filename.startsWith("Juniper")){
	      FlatJuniperParser juniper = (FlatJuniperParser) parser;
	      juniper.flat_juniper_configuration();
	   }
	   else{
	      System.out.println("unknown device type Error");
	   }
	}
	
	public void Output(Parser parser){
      String filename = configFile.getName();
      String l2 = "NA";
      String links = "";
      if(filename.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         l2 = cisco.OutputL2();
         links = cisco.OutputLinks();
      }
      else if(filename.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(filename.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(filename.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      WriteToFile(l2, configPath+"/l2protocols.txt");
      WriteToFile(links, configPath+"/links.txt");
	}
	
	private void WriteToFile(String content, String file){
      Writer writer  = null;
      try {
          writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
          writer.write(content);
          writer.close();
      } catch (Exception ex) {
          
      }   
	}
}

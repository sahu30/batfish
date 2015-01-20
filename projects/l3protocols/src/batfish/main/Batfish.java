package batfish.main;



import java.util.Map;

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

import batfish.grammar.arista.AristaGrammar;
import batfish.grammar.arista.AristaGrammarCommonLexer;
import batfish.grammar.cisco.CiscoGrammar;
import batfish.grammar.cisco.CiscoGrammarCommonLexer;
import batfish.grammar.flatjuniper.FlatJuniperLexer;
import batfish.grammar.flatjuniper.FlatJuniperParser;
import batfish.grammar.quanta.QuantaGrammar;
import batfish.grammar.quanta.QuantaGrammarCommonLexer;


public class Batfish {
   String vendor="";
   String configContent="";

   Lexer lexer;
   Parser parser;
   CommonTokenStream tokens;
   
	public Batfish(String vend, String content) {
	   vendor = vend;
	   configContent = content;
   }

   public boolean parseVendorConfigurations() {
      ANTLRInputStream inputStream = new ANTLRInputStream(configContent);
      lexer = createLexer(inputStream);
      tokens = new CommonTokenStream((TokenSource) lexer);
      parser = createParser(tokens);
      parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

      AddErrorListener(lexer, parser);
//      System.out.print("parsing " + currentPath + " ");
      try {
         Parse(parser);
      } catch (Exception e) {
         return false;
      }
      return true;
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
	   if(vendor.startsWith("Cisco")){
	      return new CiscoGrammarCommonLexer(inputStream);
	   }
	   else if(vendor.startsWith("Arista")){
	      return new AristaGrammarCommonLexer(inputStream);
	   }
	   else if(vendor.startsWith("Quanta")){
	      return new QuantaGrammarCommonLexer(inputStream);
	   }
	   else if(vendor.startsWith("Juniper")){
	      return new FlatJuniperLexer(inputStream);
	   }
	   System.out.println("no lexer for "+vendor);
	   return null;
	}
	public Parser createParser(TokenStream tokens){
      if(vendor.startsWith("Cisco")){
         return new CiscoGrammar(tokens);
      }
      else if(vendor.startsWith("Arista")){
         return new AristaGrammar(tokens);
      }
      else if(vendor.startsWith("Quanta")){
         return new QuantaGrammar(tokens);
      }
      else if(vendor.startsWith("Juniper")){
         return new FlatJuniperParser(tokens);
      }
      System.out.println("no parser for "+vendor);
      return null;
	}
	public void Parse(Parser parser){
	   if(vendor.startsWith("Cisco")){
	      CiscoGrammar cisco = (CiscoGrammar)parser;
	      cisco.cisco_configuration();
	   }
	   else if(vendor.startsWith("Arista")){
	      AristaGrammar arista = (AristaGrammar) parser;
	      arista.arista_configuration();
	   }
	   else if(vendor.startsWith("Quanta")){
	      QuantaGrammar quanta = (QuantaGrammar) parser;
	      quanta.quanta_configuration();
	   }
	   else if(vendor.startsWith("Juniper")){
	      FlatJuniperParser juniper = (FlatJuniperParser) parser;
	      juniper.flat_juniper_configuration();
	   }
	   else{
	      System.out.println("unknown device type Error");
	   }
	}
	

   public String OutputStat(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputStat(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputIfaceIp(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputIfaceIp(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputOspfNetworkArea(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputOspfNetworkArea(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputOspfPassiveIface(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputOspfPassiveIface(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputOspfPassiveIfaceDefault(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputOspfPassiveIfaceDefault(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }
   
   public String OutputWarning(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputWarning(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputRuleList(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputRuleList(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputNeighborAs(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputNeighborAs(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputGroupAs(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputGroupAs(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputGroupNeighbor(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputGroupNeighbor(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputNetworks(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputNetworks(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputNeighborTemplate(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputNeighborTemplate(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputAddressFamily(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputAddressFamily(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputTemplateRemoteAs(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputTemplateRemoteAs(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }

   public String OutputVrf(String prefix) {
      String out = "";
      if(vendor.startsWith("Cisco")){
         CiscoGrammar cisco = (CiscoGrammar)parser;
         out += cisco.OutputVrf(prefix);
      }
      else if(vendor.startsWith("Arista")){
         AristaGrammar arista = (AristaGrammar) parser;
      }
      else if(vendor.startsWith("Quanta")){
         QuantaGrammar quanta = (QuantaGrammar) parser;
      }
      else if(vendor.startsWith("Juniper")){
         FlatJuniperParser juniper = (FlatJuniperParser) parser;
      }
      else{
         System.out.println("unknown device type Error");
         assert false;
      }
      return out;
   }
}

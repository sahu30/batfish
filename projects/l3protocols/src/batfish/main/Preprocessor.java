package batfish.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Preprocessor {
   public String Process(String vendor, String file) throws IOException{
      if (vendor.equals("Cisco")){
         return CiscoProcess(file);
      }
      else{
         System.out.println("unknown vendor");
         assert false;
      }
      return null;
   }
   public String CiscoProcess(String file) throws IOException{
      boolean inStanza=false;
      boolean inIfaceStanza=false;
      boolean inOspfStanza=false;
      String INTERFACE = "interface";
      String OSPF = "router ospf";
      String BGP = "router bgp";
      String out = "";
      BufferedReader br = new BufferedReader(new FileReader(file));  
      String line = null;  
      while ((line = br.readLine()) != null)  
      {
         if(!line.startsWith(" ")){
            if(line.startsWith(INTERFACE)){
               inStanza = true;
               inIfaceStanza = true;
               inOspfStanza = false;
            }
            else if(line.startsWith(OSPF) ){
               inStanza = true;
               inOspfStanza = true;
               inIfaceStanza = false;
            }
            else if(line.startsWith(BGP)){
               inStanza = true;
               inOspfStanza = false;
               inIfaceStanza = false;
            }
            else{
               inStanza = false;
               inIfaceStanza = false;
               inOspfStanza = false;
            }
         }
         if (inStanza){
            if(!line.startsWith(" ")){
               out+=line+"\n";
            }
            else if(inIfaceStanza){
               if(line.contains("ospf") || line.contains("ip address"))
                  out+=line+"\n";
            }
            else{
               out+=line+"\n";
            }
         }
      }
      return out;
   }
   
}

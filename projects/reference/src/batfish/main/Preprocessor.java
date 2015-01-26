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
      String INTERFACE = "interface";
      String OSPF = "router ospf";
      String BGP = "router bgp";
      String ROUTE_MAP = "route-map";
      String ACCESS_LIST = "access-list";
      String PREFIX_LIST = "prefix-list";
      String COMMUNITY_LIST = "community-list";
      String out = "";
      BufferedReader br = new BufferedReader(new FileReader(file));  
      String line = null;  
      while ((line = br.readLine()) != null)  
      {
         if(!line.startsWith(" ")){
            if(line.startsWith(INTERFACE) || line.startsWith(OSPF) ||
                  line.startsWith(BGP) || line.startsWith(ROUTE_MAP)){
               inStanza = true;
            }
            else{
               inStanza = false;
            }
         }
         if (inStanza){
            out+=line+"\n";            
         }
         else{
            if(!line.startsWith(" ")){
               if(line.contains(ACCESS_LIST) || line.contains(PREFIX_LIST)
                     || line.contains(COMMUNITY_LIST)){
                  out+=line+"\n";
               }
            }
         }
      }
      return out;
   }   
}

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
      String UDLD = "udld aggressive";
      String MST = "spanning-tree mst configuration";
      String MST_MODE = "spanning-tree mode mst";
      
      String out = "";
      BufferedReader br = new BufferedReader(new FileReader(file));  
      String line = null;  
      while ((line = br.readLine()) != null)  
      {
         if(!line.startsWith(" ")){
            if(line.startsWith(INTERFACE) || line.startsWith(UDLD)
                  || line.startsWith(MST) || line.startsWith(MST_MODE) ){
               inStanza = true;
            }
            else{
               inStanza = false;
            }
         }
         if (inStanza){
            out+=line+"\n";
         }
      }
      return out;
   }
   
}

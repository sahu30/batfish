package batfish.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

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
      boolean inSubstanza = false;
      String INTERFACE = "interface";
   //   Pattern VLAN = Pattern.compile("^\\bvlan \\d+");
      String VLAN = "vlan \\d+";
      
      String out = "";
      BufferedReader br = new BufferedReader(new FileReader(file));  
      String line = null;  
      while ((line = br.readLine()) != null)  
      {
         // vlan
         
         if(line.matches(VLAN)){
   //         System.out.println(line);
            out+=line+"\n";
         }
         else{
            if(!line.startsWith(" ")){
               inSubstanza = false;
               if(line.startsWith(INTERFACE)){
                  inStanza = true;
               }
               else{
                  inStanza = false;
               }
            }
            else{
               inSubstanza = true;
            }
            if (inStanza){
               if(!inSubstanza){
         //         System.out.println(line);
                  out+=line+"\n";
               }
               else if(line.contains("vlan")){
        //          System.out.println(line);
                  out+=line+"\n"; 
               }
            }
         }
      }
      return out;
   }
}

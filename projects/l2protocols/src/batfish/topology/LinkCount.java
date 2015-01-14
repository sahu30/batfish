package batfish.topology;

import java.util.HashMap;
import java.util.Map;

public class LinkCount {
   Map<String, String> links = new HashMap<String, String>();
   
   public void AddLink(String src, String dst){
      links.put(src, dst);
   }
   
   @Override
   public String toString(){
      String out="";
      for(String key: links.keySet()){
         String value = links.get(key);
         out+=key+"\t"+value+"\n";
      }
      return out.trim();
   }
}

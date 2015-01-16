package batfish.topology;

import java.util.HashMap;
import java.util.Map;

public class LinkCount {
   Map<String, String> links = new HashMap<String, String>();
   
   public void AddLink(String src, String dst){
      links.put(src, dst);
   }
   
   public Map<String, String> getLinks(){
      return links;
   }
   
}

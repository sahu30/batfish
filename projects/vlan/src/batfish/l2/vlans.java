package batfish.l2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class vlans {
   HashSet<String> vlans = new HashSet<String>();
   
   String currentIface=null;
   List<String> currentIfaceRanges;
   
   Map<String, List<String>> iface_ranges = new HashMap<String, List<String>>();
   
   public void FindVLAN(String vlanNum){
      vlans.add(vlanNum);
   }
   
   public void EnterIface(String ifname){
      currentIface = ifname;
      currentIfaceRanges = new ArrayList<String>();
   }
   
   public void FindVLANRange(String range){
      currentIfaceRanges.add(range);
   }
   
   public void ExitIface(){
      if(currentIfaceRanges.size()!=0){
         iface_ranges.put(currentIface, currentIfaceRanges);
      }
      currentIface = null;
      currentIfaceRanges = null;
   }


   private int CountInstance(List<String> ranges){
      // vlan is from 1 - 4096, this array should be enough
      int []vlansInRange = new int[5000];
      for(String range_list: ranges){
         range_list = range_list.trim();
         String[] fields = range_list.split(",");
         for(String r : fields){
            String []end = r.split("-");
            if(end.length==2){
               int low = Integer.parseInt(end[0]);
               int high = Integer.parseInt(end[1]);
               for(int i = low; i<=high; i++){
                  vlansInRange[i] = 1;
               }
            }
            else{
               int v = Integer.parseInt(end[0]);
               vlansInRange[v]=1;
            }
         }
      }
      int count = 0;
      for(String v : vlans){
         int vlanNum = Integer.parseInt(v);
         count+=vlansInRange[vlanNum];
      }
      return count;
   }
   
   public int OutputIntances(){
      int count = 0;
      for(String iface: iface_ranges.keySet()){
         List<String> ranges = iface_ranges.get(iface);
         count += CountInstance(ranges);
      }
      return count;
   }
   
   public String OutputIfaceRanges(String prefix){
      String out="";
      for(String iface: iface_ranges.keySet()){
         List<String> ranges = iface_ranges.get(iface);
         String ranges_all = null;
         for(String range: ranges){
            if(ranges_all==null){
               ranges_all = range;
            }
            else{
               ranges_all+=","+range;
            }
         }
         out+=prefix+iface+"\t"+ranges_all+"\n";
      }
      return out;
   }
   
   public String OutputVlans(){
      String out="NA";
      for(String v: vlans){
         if(out.equals("NA")){
            out=v;
         }
         else{
            out+=","+v;
         }
      }
      return out;
   }

}

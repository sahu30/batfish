package batfish.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import batfish.util.Util;

public class reference {
   // ACL <type, name>
   Map<String, String[]> acls = new HashMap<String, String[]>();
   public String OutputAcl(String prefix){
      if(acls.size()==0) return "";
      String out = "";
      for(String[] a: acls.values()){
         out+=prefix+a[0]+"\t"+a[1]+"\n";
      }
      return out;
   }
   // Route-map <name>
   Set<String> routemaps = new HashSet<String>();
   public String OutputRoutemap(String prefix){
      if(routemaps.size()==0) return "";
      String out= "";
      for(String r: routemaps){
         out+=prefix+r+"\n";
      }
//      System.out.print(out);
      return out;
   }
   // IfaceIp <name, ip, mask>
   List<String[]> ifaceIps = new ArrayList<String[]>();
   public String OutputIfaceIp(String prefix){
      if(ifaceIps.size()==0) return "";
      String out="";
      for(String[] ip: ifaceIps){
         out+=prefix+ip[0]+"\t"+ip[1]+"\t"+ip[2]+"\n";
      }
      return out;
   }
   // BGP as <as>
   Set<String> bgpAses = new HashSet<String>();
   public String OutputBgpAs(String prefix){
      if(bgpAses.size()==0) return prefix+"NA\n";
      String out= "";
      for(String as: bgpAses){
         out+=prefix+as+"\n";
      }
      return out;
   }
   // Bgp Neighbors <as, ip, mask, remote-as>
   List<String[]> bgpNeighbors = new ArrayList<String[]>();
   public String OutputBgpNeighborAs(String prefix){
      if(bgpNeighbors.size()==0) return "";
      String out="";
      for(String[] neighbor: bgpNeighbors){
         out+=prefix+neighbor[0]+"\t"+neighbor[1]+"\t"+neighbor[2]+"\t"+neighbor[3]+"\n";
      }
      return out;
   }
   // OSPF proc <proc>
   Set<String> ospfProcs = new HashSet<String>();
   public String OutputOspfProc(String prefix){
      if(ospfProcs.size()==0) return prefix+ "NA\n";
      String out = "";
      for(String proc: ospfProcs){
         out +=prefix + proc+"\n";
      }
      return out;
   }
   // OSPF  networks <proc, ip, mask>
   List<String[]> ospfNetworks = new ArrayList<String[]>();
   public String OutputOspfNetworks(String prefix){
      if(ospfNetworks.size()==0) return "";
      String out="";
      for(String[] network: ospfNetworks){
         out+=prefix+network[0]+"\t"+network[1]+"\t"+network[2]+"\n";
      }
      return out;
   }
   // MSTP <instance, range>
   List<String[]> mstpInstances = new ArrayList<String[]>();
   public String OutputMstpInstance(String prefix){
      if(mstpInstances.size()==0) return prefix+"NA\tNA\n";
      String out = "";
      for(String[] i: mstpInstances){
         out+=prefix+i[0]+"\t"+i[1]+"\n";
      }
      return out;
   }
   
   // Intra-ref <from_type, from_name, to_type, to_name>
   List<String[]> intraRef = new ArrayList<String[]>();
   public String OutputIntraReference(String prefix){
      if(intraRef.size()==0) return "";
      String out="";
      for(String[] ref: intraRef){
         out+=prefix+ref[0]+"\t"+ref[1]+"\t"+ref[2]+"\t"+ref[3]+"\n";
      }
      return out;
   }
   
   // Warning
   String warning = "";
   public void Likely(boolean exp, String msg){
      if(!exp){
         warning+=msg+",";
      }
   }
   public String OutputWarning(String prefix){
      if (warning.equals("")) return "";
      return prefix+warning+"\n";
   }
   

   // Stanza type
   final String ACL_T = "acl";
   final String ROUTEMAP_T = "routemap";
   final String IFACE_T = "iface";
   final String OSPF_T = "ospf";
   final String BGP_T = "bgp";
   
   // address format
   final String IP_F = "ip";
   final String PREFIX_F = "subnet";
   final String IPMASK_F = "ip/mask";
   
   // ACL
   public void FindAcl(String type, String name){
      String key = type+"/"+name;
      String []value = new String[]{type, name};
      acls.put(key, value);
   }

   // Routemap
   String currentRoutemap = null;
   public void EnterRoutemap(String name){
      Likely(currentRoutemap == null, "EnterRouteMap: currentRoutemap not null (name is "+name+", currentRoutemap is "+currentRoutemap+")");
      currentRoutemap = name;
      routemaps.add(name);
   }
   public void ExitRoutemap(){
      Likely(currentRoutemap!=null, "ExitRoutemap: currentRotuemap null");
      currentRoutemap = null;
   }
   public void RoutemapAcl(String acl){
      Likely(currentRoutemap!=null, "RoutemapAcl: currentRoutemap null");
      intraRef.add(new String[]{ROUTEMAP_T, currentRoutemap, ACL_T, acl});
   }
   
   public void RoutemapIface(String iname){
      Likely(currentRoutemap!=null, "RoutemapIface: currentRoutemap null");
      intraRef.add(new String[]{ ROUTEMAP_T, currentRoutemap, IFACE_T, iname });
   }
   
   // interface
   String currentIface = null;
   // <ip, mask>
   List<String[]> currentIfaceAddress = new ArrayList<String[]>();
   public void EnterIface(String iface){
      Likely(currentIface ==null, "EnterIface: currentIface not null");
      currentIface = iface;
      currentIfaceAddress.clear();
   }
   public void ExitIface(){
      Likely(currentIface!=null, "ExitIface: currentIface null");
      currentIface = null;
      currentIfaceAddress.clear();
   }
   public void IfaceRoutemap(String routemap){
      Likely(currentIface!=null, "IfaceRoutemap: currentIface null");
      intraRef.add(new String[]{IFACE_T, currentIface, ROUTEMAP_T, routemap});
   }
   public void IfaceAcl(String acl){
      Likely(currentIface!=null, "IfaceRoutemap: currentIface null");
      intraRef.add(new String[]{IFACE_T, currentIface, ACL_T, acl});
   }
   public void IfaceIp(String format, String value){
      Likely(currentIface!=null, "IfaceIp: currentIface null");
      String[] ip_mask = FormatValue2IpMask(format, value);
      if(ip_mask == null){
         Likely(false, "IfaceIp: unknow format "+format+" "+value);
      }
      else{
         String ip = ip_mask[0];
         String mask = ip_mask[1];
         ifaceIps.add(new String[]{currentIface, ip, mask});
         currentIfaceAddress.add(new String[]{ip, mask});
      }
   }
   

   public void IfaceOspf(String ospf){
      Likely(currentIface!=null, "IfaceOspf: currentIface null");
      for(String[] address: currentIfaceAddress){
         ospfNetworks.add(new String[]{ospf, address[0], address[1]});
      }
   }
   
   // BGP
   String currentBgp=null;
   public void EnterBgp(String asNum){
      Likely(currentBgp==null, "EnterBgp: currentBgp not null");
      currentBgp = asNum;
      bgpAses.add(asNum);
      neighborAs.clear();
   }
   public void ExitBgp(){
      Likely(currentBgp!=null, "ExitBgp: currentBgp null");
      currentBgp = null;
      // Fill in neighbor list
      bgpNeighbors.addAll(neighborAs.values());
   }
   public void BgpRoutemap(String routemap){
      Likely(currentBgp!=null, "BgpRoutemap: currentBgp null");
      intraRef.add(new String[]{BGP_T, currentBgp, ROUTEMAP_T, routemap});
   }
   public void BgpAcl(String acl){
      Likely(currentBgp!=null, "BgpAcl: currentBgp null");
      intraRef.add(new String[]{BGP_T, currentBgp, ACL_T, acl});
   }
   
   String currentTemplate = null;
   Map<String, String> templateAs = new HashMap<String, String>();
   public void BgpEnterTemplate(String name){
      Likely(currentBgp!=null, "BgpEnterTemplate: currentBgp null");
      Likely(currentTemplate == null, "BgpEnterTemplate: currentTemplate not null");
      currentTemplate = name;
   }
   public void BgpExitTemplate(){
      Likely(currentBgp!=null, "BgpExitTemplate: currentBgp null");
      Likely(currentTemplate != null, "BgpEnterTemplate: currentTemplate null");
      currentTemplate = null;
   }
   public void BgpTemplateAs(String as){
      Likely(currentBgp!=null, "BgpTemplateAs: currentBgp null");
      Likely(currentTemplate != null, "BgpTemplateAs: currentTemplate null");
      templateAs.put(currentTemplate, as);
   }
   String currentGroup = null;
   Map<String, String> groupAs = new HashMap<String, String>();
   public void BgpEnterGroup(String name){
      Likely(currentBgp!=null, "BgpEnterGroup: currentBgp null");
      Likely(currentGroup== null, "BgpEnterGroup: currentGroup not null");
      currentGroup = name;
   }
   public void BgpExitGroup(){
      Likely(currentBgp!=null, "BgpExitGroup: currentBgp null");
      Likely(currentGroup!= null, "BgpExitGroup: currentGroup null");
      currentGroup = null;
   }
   public void BgpGroupAs(String as){
      Likely(currentBgp!=null, "BgpGroupAs: currentBgp null");
      Likely(currentGroup!= null, "BgpGroupAs: currentGroup null");
      groupAs.put(currentGroup, as);
   }
   String currentNeighborIp = null;
   String currentNeighborMask = null;
   Map<String, String[]> neighborAs = new HashMap<String, String[]>();
   public void BgpEnterNeighbor(String format, String neighbor){
      Likely(currentBgp!=null, "BgpEnterneighbor: currentBgp null");
      Likely(currentNeighborIp== null, "BgpEnterNeighbor: currentNeighborIp not null");
      String []ip_mask = FormatValue2IpMask(format, neighbor);
      currentNeighborIp = ip_mask[0];
      currentNeighborMask = ip_mask[1];
   }
   public void BgpExitNeighbor(){
      Likely(currentBgp!=null, "BgpExitNeighbor: currentBgp null");
      Likely(currentNeighborIp!= null, "BgpEnterNeighbor: currentNeighborIp null");
      currentNeighborIp = null;
      currentNeighborMask = null;
   }
   public void BgpNeighborAs(String as){
      Likely(currentBgp!=null, "BgpNeighborAs: currentBgp null");
      neighborAs.put(currentNeighborIp+"/"+currentNeighborMask, new String[]{currentBgp, currentNeighborIp, currentNeighborMask, as});
   }
   
   
   public void BgpNeighborOrGroupAs(String asNum) { 
      if(currentNeighborIp!=null){
         neighborAs.put(currentNeighborIp+"/"+currentNeighborMask, new String[]{currentBgp, currentNeighborIp, currentNeighborMask, asNum});
      }
      else if(currentGroup!=null){
         groupAs.put(currentGroup, asNum);
      }
      else{
         Likely(false, "BgpNeighborOrGroupAs: currentNeighborIp and currentGroup are null");
      }
   }
   public void BgpNeighborGroup(String group) {
      Likely(currentNeighborIp!=null, "BgpNeighborGroup: currentNeighborIp null, group is "+group);
      if(groupAs.containsKey(group)){
         String asNum = groupAs.get(group);
         neighborAs.put(currentNeighborIp+"/"+currentNeighborMask, new String[]{currentBgp, currentNeighborIp, currentNeighborMask, asNum});         
      }
      else{
         return ;
      }
   }
   public void BgpNeighborNoAs(String asNum) {
      Likely(currentNeighborIp!=null, "BgpNeighborNoAs: currentNeighborIp null");
      String key = currentNeighborIp+"/"+currentNeighborMask;
      if(neighborAs.containsKey(key)){
         String[] value = neighborAs.get(key);
         String orgAsNum = value[3];
         if(orgAsNum.equals(asNum)){
            neighborAs.remove(key);
         }
      }
   }
   public void BgpNeighborInherit(String template) {
      Likely(currentNeighborIp!=null, "BgpNeighborInherit: currentNeighborIp null");
      if(templateAs.containsKey(template)){
         String asNum = templateAs.get(template);
         String key = currentNeighborIp+"/"+currentNeighborMask;
         String []value = new String[]{currentBgp, currentNeighborIp, currentNeighborMask, asNum};
         neighborAs.put(key, value);
      }
   }
   public void BgpTemplateInherit(String template) { 
      Likely(currentTemplate!=null, "BgpTemplateInherit: currentTemplate null");
      if(templateAs.containsKey(template)){
         String asNum = templateAs.get(template);
         templateAs.put(currentTemplate, asNum);
      }
   }
   
   // OSPF
   String currentOspf=null;
   public void EnterOspf(String proc){
      Likely(currentOspf==null, "EnterOspf: currentOspf not null");
      currentOspf = proc;
      ospfProcs.add(proc);
   }
   public void ExitOspf(){
      Likely(currentOspf!=null, "ExitOspf: currentOspf null");
      currentOspf = null;
   }
   public void OspfRoutemap(String routemap){
      Likely(currentOspf!=null, "OspfRoutemap: currentOspf null");
      intraRef.add(new String[]{OSPF_T, currentOspf, ROUTEMAP_T, routemap});
   }
   public void OspfAcl(String acl){
      Likely(currentOspf!=null, "OspfAcl: currentOspf null");
      intraRef.add(new String[]{OSPF_T, currentOspf, ACL_T, acl});
   }
   public void OspfNetwork(String format, String value){
      Likely(currentOspf!=null, "OspfNetwork: currentOspf null");
      String[] ip_mask = FormatValue2IpReverseMask(format, value);
      String ip = ip_mask[0];
      String mask = ip_mask[1];
      ospfNetworks.add(new String[]{currentOspf, ip, mask});
   }
   
   // MSTP
   public void MstpInstance(String id, String range){
      mstpInstances.add(new String[]{id, range});
   }
   
   
   // Util
   private String[] FormatValue2IpMask(String format, String value){
      String ip, mask;
      if(format.equals(IP_F)){
         ip = value;
         mask = Util.longToIp(4294967295L);
      }
      else if(format.equals(PREFIX_F)){
         String []fields = value.split("/");
         ip = fields[0];
         long mask_long = Util.numSubnetBitsToSubnetLong(Integer.parseInt(fields[1]));
         mask = Util.longToIp(mask_long);
      }
      else if(format.equals(IPMASK_F)){
         String []fields = value.split("/");
         ip = fields[0];
         mask = fields[1];
      }
      else{
         return null;
      }
      return new String[]{ip, mask};
   }
   private String[] FormatValue2IpReverseMask(String format, String value){
      String ip, mask;
      if(format.equals(IP_F)){
         ip = value;
         mask = Util.longToIp(4294967295L);
      }
      else if(format.equals(PREFIX_F)){
         String []fields = value.split("/");
         ip = fields[0];
         long mask_long = Util.numSubnetBitsToSubnetLong(Integer.parseInt(fields[1]));
         mask = Util.longToIp(mask_long);
      }
      else if(format.equals(IPMASK_F)){
         String []fields = value.split("/");
         ip = fields[0];
         mask = fields[1];
         long mask_long = Util.ipToLong(mask);
         mask_long = 4294967295L^mask_long;
         mask = Util.longToIp(mask_long);
      }
      else{
         return null;
      }
      return new String[]{ip, mask};
   }
   
}

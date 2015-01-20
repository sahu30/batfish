package batfish.l3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import batfish.representation.Ip;
import batfish.util.Util;

public class protocols {
   String warning = "";
   public String OutputStat(String prefix) {
      //return prefix+OutputOSPFStat()+"\t"+OutputBGPStat()+"\n";
      if(currentBgp==null) return "";
      return prefix+OutputBGPStat()+"\n";
   }
   /*******************************************************
    * Interface
    *******************************************************/
   // fill in raw data
   List<iface> ifaces = new ArrayList<iface>();
   iface currentIface = null;

   public void EnterIface(String ifacename) {
      Assert(currentIface == null, "EnterIface with currentIface not null");
      currentIface = new iface(ifacename);
   }

   public void IfaceIp(String ip, boolean secondary)  {
      Assert(currentIface!=null, "IfaceIP with currentIface null");
      currentIface.IfaceIp(ip, secondary);
   }

   public void IfaceIp(String ip, String mask, boolean secondary)   {
      Assert(currentIface!=null, "IfaceIP with currentIface null");
      currentIface.IfaceIp(ip, mask, secondary);
      
   }
   public void IfaceOspfArea(String procNum, String area)  {
      Assert(currentIface!=null, "IfaceOspfArea with currentIface null");
      currentIface.IfaceOspfArea(procNum, area);
   }

   public void IfacePassive(boolean yes) {
      currentIface.IfacePassive(yes);      
   }
   public void ExitIface()  {
      Assert(currentIface != null, "ExitIface with currentIface null");
      ifaces.add(currentIface);
      currentIface = null;
   }

   /********************************************************
    * OSPF
    ********************************************************/
   List<ospf> ospfs = new ArrayList<ospf>();   
   ospf currentOspf = null;   
   public void EnterOspf(String ospfID)  {
      Assert(currentOspf==null, "EnterOspf with currentOspf not null");
      currentOspf = new ospf(ospfID);
   }
   
   public void OspfNetworkArea(String addr, String subnet, String area)  {
      Assert(currentOspf!=null, "OspfNetworkArea with currentOspf null");
      currentOspf.OspfNetworkArea(addr, subnet, area);
   }

   public void OspfNetworkArea(String prefix, String area)   {
      Assert(currentOspf!=null, "OspfNetworkArea with currentOspf null");
      currentOspf.OspfNetworkArea(prefix, area);
      
   }
   public void OspfRedistribute() {
      Assert(currentOspf!=null, "OspfRedistribute with currentOspf null");
      currentOspf.OspfRedistribute();
   }
   
   public void OspfPassiveDefault()  {
      Assert(currentOspf!=null, "OspfPassiveDefault with currentOspf null");
      currentOspf.OspfPassiveDefault();
   }
   
   public void OspfPassiveIface(boolean yes, String ifacename)  {
      Assert(currentOspf!=null, "OspfPassiveIface with currentOspf null");
      currentOspf.OspfPassiveIface(yes, ifacename);
   }
   
   public void ExitOspf()  {
      Assert(currentOspf!=null, "ExitOspf with currentOspf null");
      ospfs.add(currentOspf);
      currentOspf = null;
   }
   
   public void Warning(String w){
      Assert(false, w);
   }
   
   private void Assert(boolean exp, String info)  {
      if(!exp){
         warning+=info+",";
      }
   }   

   // process get <router, subnet, mask, area> and
   // <router, iface, passive>, and numOspfInstance
   private boolean networkAreaComputed = false;
   private List<ospfRouterNetworkArea> routerNetworkAreas= new ArrayList<ospfRouterNetworkArea>();
   private List<ospfRouterNetworkArea> getOspfRouterNetworkAreaList(){
      if(networkAreaComputed){
         return routerNetworkAreas;
      }
      for(iface ifac: ifaces){
         ospfRouterNetworkArea rna = ifac.getOspfRouterNetworkArea();
         if(rna != null)
            routerNetworkAreas.add(rna);
      }
      for(ospf osp: ospfs){
         List<ospfRouterNetworkArea> rnas = osp.getOspfRouterNetworkAreas();
         routerNetworkAreas.addAll(rnas);
      }
      networkAreaComputed = true;
      return routerNetworkAreas;
   }
   private boolean passiveIfaceComputed = false;
   private List<ospfRouterPassiveIface> routerPassiveIfaces = new ArrayList<ospfRouterPassiveIface>();
   private List<ospfRouterPassiveIface> getOspfRouterPassiveIfaceList(){
      if(passiveIfaceComputed){
         return routerPassiveIfaces;
      }
      for(iface ifac: ifaces){
         ospfRouterPassiveIface rpi = ifac.getOspfRouterPassiveIface();
         if(rpi !=null){
            routerPassiveIfaces.add(rpi);
         }
      }
      for(ospf osp: ospfs){
         List<ospfRouterPassiveIface> rpis = osp.getOspfRouterPassiveIfaces();
         routerPassiveIfaces.addAll(rpis);
      }
      passiveIfaceComputed = true;
      return routerPassiveIfaces;
   }
   
   private int ifaceInNetworkAreaCount(iface ifac){
      int count = 0;
      Set<String> routers = new HashSet<String>();
      List<ospfRouterNetworkArea> ornas = getOspfRouterNetworkAreaList();
      for(ospfRouterNetworkArea orna: ornas){
         if(orna.IncludeIface(ifac)){
            String router = orna.getRouter();
            if(!routers.contains(router)){
               count++;
               routers.add(router);
            }
         }
      }
      return count;
   }
   private int getOspfInst(){
      int count = 0;
      for(iface ifac: ifaces){
         count+=ifaceInNetworkAreaCount(ifac);
      }
      return count;
   }
   private int getOspfRedistribute(){
      int count = 0;
      for(ospf osp: ospfs){
         count+=osp.getRedistribute();
      }
      return count;
   }
   
   // Output: stat, <iface ip>, <router, subnet, mask, area> 
   // <router, iface, passive>, <router, default>
   // <warning>
   private String OutputOSPFStat(){
      // numOSPFRouter, OSPFInst, numOSPFNetwork, numOSPFPassiveIface, numOSPFRedistribute      
      List<ospfRouterNetworkArea> ornas = getOspfRouterNetworkAreaList();
      List<ospfRouterPassiveIface> opis = getOspfRouterPassiveIfaceList();
      String out = "";
      out+=ospfs.size();
      out+="\t"+getOspfInst();
      out+="\t"+ornas.size();
      out+="\t"+opis.size();
      out+="\t"+getOspfRedistribute();
      return out;
   }

   public String OutputIfaceIp(String prefix) {
      String out = "";
      for(iface ifac: ifaces){
         String ifaceIp_str = ifac.getIfaceIp_str();
         if(ifaceIp_str==null)
            continue;
         out+=prefix+ifaceIp_str+"\n";
      }
      return out;
   }

   public String OutputOspfNetworkArea(String prefix) {
      String out = "";
      List<ospfRouterNetworkArea> ornas = getOspfRouterNetworkAreaList();
      for(ospfRouterNetworkArea orna: ornas){
         out+=prefix;
         out+=orna.toString()+"\n";
      }
      return out;
   }

   public String OutputOspfPassiveIface(String prefix) {
      String out = "";
      List<ospfRouterPassiveIface> orpis = getOspfRouterPassiveIfaceList();
      for(ospfRouterPassiveIface orpi: orpis){
         out+=prefix;
         out+=orpi.toString()+"\n";
      }
      return out;
   }
   public String OutputOspfPassiveIfaceDefault(String prefix){
      String out = "";
      for(ospf osp: ospfs){
         out+=prefix;
         out+=osp.getOspfRouterPassiveDefault_str()+"\n";
      }
      return out;
   }
   
   public String OutputWarning(String prefix) {
      String out = "";
      if(warning.equals("")){
         return "";
      }
      out = prefix;
      out+=warning+"\n";
      return out;
   }
   
   // raw data interface, ospf
   private class iface{
      String name;
      boolean passiveIfaceConfigured = false;
      boolean passiveIface = false;      
      Ip address = null;
      Ip mask = null;
      Ip address_secondary = null;
      Ip mask_secondary = null;      
      String ospfProcNum = null;
      String ospfArea = null;
      
      public iface(String n){
         name = n;
      }      


      public void IfaceIp(String addr, String mas, boolean secondary) {
         Ip a = new Ip(addr);
         Ip m = new Ip(mas);
         if(secondary){
            Assert(address_secondary== null, "secondary address is configured twice");
            address_secondary = a;
            mask_secondary = m;
         }
         else{
            Assert(address==null, "primary address is configured twice");
            address = a;
            mask = m;
         }
      }

      public void IfaceIp(String prefix, boolean secondary){
         String []fields = prefix.split("/");
         Assert(fields.length==2, "wrong format of prefix:"+prefix);
         String ip_str = fields[0];
         String numMaskBits= fields[1];
         Ip addr = new Ip(ip_str);
         int numBits = Integer.parseInt(numMaskBits);
         Ip mas = new Ip(Util.numSubnetBitsToSubnetLong(numBits));
         if(secondary){
            Assert(address_secondary== null, "secondary address is configured twice");
            address_secondary = addr;
            mask_secondary = mas;
         }
         else{
            Assert(address==null, "primary address is configured twice");
            address = addr;
            mask = mas;
         }
      }
      
      public void IfaceOspfArea(String procNum, String area)
      {
         Assert(ospfProcNum == null, "IfaceOspfArea with procNum not null");
         ospfProcNum = procNum;
         ospfArea = area;
      }
      
      public void IfacePassive(boolean yes) {
         passiveIfaceConfigured = true;
         passiveIface = yes;
      }
      /*
      public Ip getIfaceIp(){
         return address;
      }
      
      public Ip getIfaceMask(){
         return mask;
      }
      
      public Ip getIfaceIpSecondary(){
         return address_secondary;
      }
      public Ip getIfaceMaskSecondary(){
         return mask_secondary;
      }
      */


      public ospfRouterPassiveIface getOspfRouterPassiveIface() {
         if(passiveIfaceConfigured){
            Assert(ospfProcNum!=null, "interface passive-interface configured without router ospf");
            return new ospfRouterPassiveIface( ospfProcNum, name, passiveIface?1:0);
         }
         return null;
      }

      public ospfRouterNetworkArea getOspfRouterNetworkArea() {
         if(ospfProcNum!=null){
            Assert(address!=null, "interface router ospf area configured without ip address");
            return new ospfRouterNetworkArea(ospfProcNum, address, mask, ospfArea);
         }
         return null;
      }

      public String getIfaceIp_str() {
         String out = null;
         if(address == null){
            return out;
         }
         else{
            String addr_str = address.toString();
            String mas_str = mask.toString();
            String addr_s_str = (address_secondary==null? "NA": address_secondary.toString());
            String mas_s_str = (mask_secondary==null? "NA" : mask_secondary.toString());
            out=name;
            out+="\t"+addr_str+"\t"+mas_str;
            out+="\t"+addr_s_str+"\t"+mas_s_str;
            return out;
         }
      }
      
      public Ip getIfaceIp(){
         return address;
      }
   }
   
   private class ospf{
      String name = null;
      int redistributeCount = 0;
      boolean passiveIfConfigured = false;
      boolean passiveIfDefault = false;      
      List<OspfNetworkArea> ospfNetowrkAreas = new ArrayList<OspfNetworkArea>();
      List<OspfPassiveIface> ospfPassiveIfaces = new ArrayList<OspfPassiveIface>();
      
      public ospf(String n){
         name = n;
      }

      public String getOspfRouterPassiveDefault_str() {
         if(!passiveIfConfigured)
            return "NA";
         else{
            return name+"\t"+(passiveIfDefault?"1":"0");
         }
      }

      public List<ospfRouterPassiveIface> getOspfRouterPassiveIfaces() {
         List<ospfRouterPassiveIface> ret = new ArrayList<ospfRouterPassiveIface>();
         for(OspfPassiveIface opi: ospfPassiveIfaces){
            ospfRouterPassiveIface orpi = opi.getOspfRouterPassiveIface(name);
            ret.add(orpi);
         }
         return ret;
      }

      public List<ospfRouterNetworkArea> getOspfRouterNetworkAreas() {
         List<ospfRouterNetworkArea> ret = new ArrayList<ospfRouterNetworkArea>();
         for(OspfNetworkArea ona: ospfNetowrkAreas){
            ret.add(ona.getOspfRouterNetworkArea(name));
         }
         return ret;
      }      
      
      public int getRedistribute() {
         return redistributeCount;
      }

      public void OspfNetworkArea(String prefix, String area) {
         String []fields = prefix.split("/");
         Assert(fields.length==2, "wrong format of prefix:"+prefix);
         String ip_str = fields[0];
         String numMaskBits= fields[1];
         Ip addr = new Ip(ip_str);
         int numBits = Integer.parseInt(numMaskBits);
         Ip mas = new Ip(Util.numSubnetBitsToSubnetLong(numBits));
         OspfNetworkArea ospfNetworkArea = new OspfNetworkArea(addr, mas, area);
         ospfNetowrkAreas.add(ospfNetworkArea);         
      }
      
      public void OspfNetworkArea(String addr, String subnet_str, String area){
         Ip subnet = new Ip(subnet_str);
         long mask_long = subnet.asLong();
         Ip mask = new Ip(mask_long ^ 4294967295L);
         Ip address = new Ip(addr);
         OspfNetworkArea ospfNetworkArea = new OspfNetworkArea(address, mask, area);
         ospfNetowrkAreas.add(ospfNetworkArea);
      }
      
      public void OspfRedistribute()  {
         redistributeCount++;
      }
      
      public void OspfPassiveDefault()  {
         // Assume there is no "no passive-interface default"
         passiveIfConfigured = true;
         passiveIfDefault = true;
      }
      
      public void OspfPassiveIface(boolean yes, String ifname){
         int passive = yes? 1: 0;
         OspfPassiveIface iface = new OspfPassiveIface(passive, ifname);
         ospfPassiveIfaces.add(iface);
      }
   }

   // list entry: <subnet, mask, area>, <iface, passive>
   // <router, subnet, mask, area>, <router, iface, passive>
   private class OspfNetworkArea{ 
      Ip subnet, mask;
      String area;
      public OspfNetworkArea(Ip sub, Ip mas, String are){
         subnet = sub;
         mask = mas;
         area = are;
      }      
      public ospfRouterNetworkArea getOspfRouterNetworkArea(String router){
         return new ospfRouterNetworkArea(router, subnet, mask, area);
      }
   }
   
   private class OspfPassiveIface{
      int passive;
      String iface;
      public OspfPassiveIface(int p, String ifacename){
         passive = p;
         iface = ifacename;
      }
      public ospfRouterPassiveIface getOspfRouterPassiveIface(String router){
         return new ospfRouterPassiveIface(router, iface, passive);
      }
   }
   private class ospfRouterNetworkArea{
      String router, area;
      Ip subnet;
      Ip mask;
      public ospfRouterNetworkArea(String r, Ip s, Ip m, String a){
         router = r;
         area = a;
         subnet = s;
         mask = m;
      }
      public String getRouter() {
         return router;
      }
      public boolean IncludeIface(iface ifac) {
         Ip ifaceIp = ifac.getIfaceIp();
         if(ifaceIp == null){
            return false;
         }
         long ifaceIpLong = ifaceIp.asLong();
         long subnetLong = subnet.asLong();
         long maskLong = mask.asLong();
         if( (ifaceIpLong&maskLong) == (subnetLong&maskLong) ){
            return true;
         }
         return false;
      }
      @Override
      public String toString(){
         return router+"\t"+subnet+"\t"+mask+"\t"+area;
      }
   }
   private class ospfRouterPassiveIface{
      String router, iface;
      int passive;
      public ospfRouterPassiveIface(String r, String i, int p){
         router = r;
         iface = i;
         passive = p;
      }
      @Override
      public String toString(){
         return router+"\t"+iface+"\t"+passive;
      }
   }
   /********************************************************
    * BGP
    ********************************************************/
   // Output      
   // <neighborType, neighbor, listType, listName, stanza_type, stanza_name, other>
   public String OutputRuleList(String prefix){
      if(currentBgp==null) return "";
      return currentBgp.OutputRuleList(prefix);
   }
   // <neighborType, neighbor, asNum, stanza_type, stanza_name>
   public String OutputNeighborAs(String prefix){
      if(currentBgp==null) return "";
      return currentBgp.OutputNeighborAs(prefix);
   }
   // <groupName, asNum, stanza_type, stanza_name>
   public String OutputGroupAs(String prefix){
      if(currentBgp==null) return "";
      return currentBgp.OutputGroupAs(prefix);
      
   }
   // <neighborType, neighbor, group, stanza_type, stanza_name>
   public String OutputGroupNeighbor(String prefix){
      if(currentBgp==null) return "";
      return currentBgp.OutputGroupNeighbor(prefix);
      
   }
   // <networkType, network, stanza_type, stanza_name, other>
   public String OutputNetworks(String prefix){
      if(currentBgp==null) return "";
      return currentBgp.OutputNetworks(prefix);
      
   }
   // <neighborType, neighbor, templateName>  
   public String OutputNeighborTemplate(String prefix){
      if(currentBgp==null) return "";
      return currentBgp.OutputNeighborTemplate(prefix);
      
   }
   // <afType, redistribute>
   public String OutputAddressFamily(String prefix){
      if(currentBgp==null) return "";
      return currentBgp.OutputAddressFamily(prefix);
      
   }
   //  <templateName, remoteAs>
   public String OutputTemplateRemoteAs(String prefix){
      if(currentBgp==null) return "";
      return currentBgp.OutputTemplateRemoteAs(prefix);
      
   }
   // <VRF>
   public String OutputVrf(String prefix){
      if(currentBgp==null) return "";
      return currentBgp.OutputVrf(prefix);
      
   }
   // <hasBGP, BGPInst, asNum, numListRef, numNeighborAs, numGroupAs, 
   // numNeighborGroup, numUniqueNetwork, numNeighborTemplate, numRedistribute,
   // numTemplate, numVRF, numNetwork, BGPInstAlone, BGPInstInGroup, BGPInstByTemplate>
   private String OutputBGPStat(){
      if(currentBgp==null){ 
         return "0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t"+
                  "0\t0\t0\t0\t0\t0\t0\t0\t0\t0";
      }
      return currentBgp.OutputStat();      
   }
   
   
   // neighborType
   final String IPV4_ADDR_T = "ipv4_addr";
   final String IPV6_ADDR_T = "ipv6_addr";
   final String IPV4_PREFIX_T = "ipv4_prefix";
   final String IPV6_PREFIX_T = "ipv6_prefix";
   final String GROUP_T = "group";
   // netType
   final String IPV4_MASK_T = "ipv4_mask";
   final String IPV4_T = "ipv4";
   final String IPv4_PREFIX_T = "ipv4_prefix";
   final String IPV6_T = "ipv6";
   
   
   // BGP 
   Bgp currentBgp=null;
   public void Bgp(String asNum){
      Assert(currentBgp == null, "Bgp: currentBgp not null");
      currentBgp = new Bgp(asNum);
   }
   public void BgpExit(){
      Assert(currentBgp!=null, "BgpExit: currentBgp null");
   }
   String afHeader; 
   public void BgpAfHeader(String afType){
      afHeader = afType;
   }
   // address-family stanza
   AddressFamily currentAddressFamily=null;
   public void BgpAf(){
      Assert(currentAddressFamily==null, "BgpAf: currentAddressFamily not null");
      Assert(afHeader!=null, "Bgp: afHeader null");
      currentAddressFamily = new AddressFamily(afHeader);
   }
   public void BgpAfExit(){
      Assert(currentAddressFamily!=null, "BgpAfExit: currentAddressFamily null");
      currentBgp.AddAddressFamily(currentAddressFamily);
      currentAddressFamily=null;
   }
   public void BgpAfNetwork(String type, String net){ // network
      Assert(currentAddressFamily!=null, "BgpAfNetwork: currentAddressFamily null");
      currentAddressFamily.Network(type, net);
   }
   public void BgpAfRedistribute(){ // redistribute
      Assert(currentAddressFamily!=null, "BgpAfRedistribute: currentAddressFamily null");
      currentAddressFamily.Redistribute();
   }
   public void BgpAfNeighbor(String type, String neighbor){// neighbor
      Assert(currentAddressFamily!=null, "BgpAfNeighbor: currentAddressFamily null");
      currentAddressFamily.Neighbor(type, neighbor);
   }
   public void BgpAfNeighborExit(){
      Assert(currentAddressFamily!=null, "BgpAfNeighborExit: currentAddressFamily null");
      currentAddressFamily.NeighborExit();
   }
   public void BgpAfList(String type, String name){ // tail
      Assert(currentAddressFamily!=null, "BgpAfList: currentAddressFamily null");
      currentAddressFamily.List(type,name);
   }
   public void BgpAfPeerGroup(String groupname){ // tail
      Assert(currentAddressFamily!=null, "BgpAfPeerGroup: currentAddressFamily null");
      currentAddressFamily.PeerGroup(groupname);
   }
   public void BgpAfNeighborRemoteAs(String asNum){ // tail
      Assert(currentAddressFamily!=null, "BgpAfNeighborRemoteAs: currentAddressFamily null");
      currentAddressFamily.RemoteAs(asNum);
   }
   // standalone configs
   public void BgpNetwork(String type, String net){
      Assert(currentBgp!=null, "BgpNetwork: currentBgp null");
      currentBgp.Network(type, net);
   }
   public void BgpNeighbor(String type, String neighbor){
      Assert(currentBgp!=null, "BgpNeighbor: currentBgp null");
      currentBgp.Neighbor(type, neighbor);
   }
   public void BgpNeighborExit(){
      Assert(currentBgp!=null, "BgpNeighborExit: currentBgp null");
      currentBgp.NeighborExit();
   }
   public void BgpList(String type, String name){ // tail
      Assert(currentBgp!=null, "BgpList: currentBgp null");
      currentBgp.List(type, name);
   }
   public void BgpPeerGroup(String groupname){  // tail
      Assert(currentBgp!=null, "BgpPeerGroup: currentBgp null");
      currentBgp.PeerGroup(groupname);
   }
   public void BgpNeighborRemoteAs(String asNum){ // tail
      Assert(currentBgp!=null, "BgpNeighborRemoteAs: currentBgp null");
      currentBgp.RemoteAs(asNum);
   }
   // nexus configs
   NeighborNexus currentNeighborNexus= null;
   public void BgpNeighborNexus(String type, String net, String asNum){ // asNum may be null, assume type is not GROUP_T
      Assert(currentNeighborNexus==null, "BgpNeighborNexus: currentNeighborNexus not null");
      currentNeighborNexus = new NeighborNexus(type, net, asNum);
   }
   public void BgpNeighborNexusExit(){
      Assert(currentNeighborNexus!=null, "BgpNeighborNexusExit: currentNeighborNexus null");
      currentBgp.AddNeighborNexus(currentNeighborNexus);
      currentNeighborNexus = null;
   }
   public void BgpNeighborNexusAf(){
      Assert(currentNeighborNexus!=null, "BgpNeighborNexusAf: currentNeighborNexus null");
      currentNeighborNexus.Af(afHeader);
   }
   public void BgpNeighborNexusAfExit(){
      Assert(currentNeighborNexus!=null, "BgpNeighborNexusAfExit: currentNeighborNexus null");
      currentNeighborNexus.AfExit();
   }
   public void BgpNeighborNexusAfList(String type, String name){
      Assert(currentNeighborNexus!=null, "BgpNeighborNexusAfList: currentNeighborNexus null");
      currentNeighborNexus.AfList(type, name);
   }
   public void BgpNeighborNexusInherit(String template){
      Assert(currentNeighborNexus!=null, "BgpNeighborNexusInherit: currentNeighborNexus null");
      currentNeighborNexus.Inherit(template);
   }
   // template configs
   Template currentTemplate = null;
   public void BgpTemplate(String templatename){
      Assert(currentTemplate==null, "BgpTemplate: currentTempalte not null");
      currentTemplate = new Template(templatename);
   }
   public void BgpTemplateExit(){
      Assert(currentTemplate!=null, "BgpTemplateExit: currentTemplate null");
      currentBgp.AddTemplate(currentTemplate);
      currentTemplate = null;
   }
   public void BgpTemplateAf(){
      Assert(currentTemplate!=null, "BgpTemplateAf: currentTemplate null");
      Assert(afHeader!=null, "BgpTemplate: afHeader null");
      currentTemplate.Af(afHeader);
   }
   public void BgpTemplateAfExit(){
      Assert(currentTemplate!=null, "BgpTemplateAfExit: currentTemplate null");
      currentTemplate.AfExit();
   }
   public void BgpTemplateAfList(String type, String name){
      Assert(currentTemplate!=null, "BgpTemplateAfList: currentTemplate null");
      currentTemplate.AfList(type, name);
   }
   public void BgpTemplateRemoteAs(String asNum){
      Assert(currentTemplate!=null, "BgpTemplateRemoteAs: currentTemplate null");
      currentTemplate.RemoteAs(asNum);
   }
   // VRF configs
   VrfNexus currentVrfNexus = null;
   public void BgpVrfNexus(String name){
      Assert(currentVrfNexus==null, "BgpVrfNexus: currentVrfNexus not null");
      currentVrfNexus = new VrfNexus(name);
   }
   public void BgpVrfNexusExit(){
      Assert(currentVrfNexus!=null, "BgpVrfNexusExit: currentVrfNexus null");
      currentBgp.AddVrfNexus(currentVrfNexus);
      currentVrfNexus = null;
   }
   public void BgpVrfAf(){
      Assert(currentVrfNexus!=null, "BgpVrfAf: currentVrfNexus null");
      Assert(afHeader!=null, "BgpVrfAf: afHeader null");
      currentVrfNexus.Af(afHeader);
   }
   public void BgpVrfAfExit(){
      Assert(currentVrfNexus!=null, "BgpVrfAfExit: currentVrfNexus null");
      currentVrfNexus.AfExit();
   }
   public void BgpVrfAfNetwork(String type, String net){
      Assert(currentVrfNexus!=null, "BgpVrfAfNetwork: currentVrfNexus null");
      currentVrfNexus.AfNetwork(type, net);
   }
   public void BgpVrfNeighborNexus(String type, String neighbor){
      Assert(currentVrfNexus!=null, "BgpVrfNeighborNexus: currentVrfNexus null");
      currentVrfNexus.NeighborNexus(type, neighbor);
   }
   public void BgpVrfNeighborNexusExit(){
      Assert(currentVrfNexus!=null, "BgpVrfNeighborNexusExit: currentVrfNexus null");
      currentVrfNexus.NeighborNexusExit();
   }
   public void BgpVrfNeighborNexusInherit(String template){
      Assert(currentVrfNexus!=null, "BgpVrfNeighborNexusInherit: currentVrfNexus null");
      currentVrfNexus.NeighborNexusInherit(template);
   }
   
   
   private class Bgp{
      //  acl-list: <neighborType, neighbor, listType, listName, stanza_type, stanza_name, other>
      List<String[]> ruleList = new ArrayList<String[]>();
      // neighbor-as: <neighborType, neighbor, asNum, stanza_type, stanza_name>
      List<String[]> neighborAsList = new ArrayList<String[]>();
      // group-as: <name, asNum, stanza_type, stanza_name>
      List<String[]> groupAsList = new ArrayList<String[]>();
      // neighbor-group: <neighborType, neighbor, group, stanza_type, stanza_name>
      List<String[]> neighborGroupList = new ArrayList<String[]>();
      // network: <networkType, network, stanza_type, stanza_name, other>
      List<String[]> networkList = new ArrayList<String[]>();
      // neighbor-template: <neighborType, neighbor, templateName, stanza_type, stanza_name>  
      List<String[]> neighborTemplateList = new ArrayList<String[]>();
      // address-family: <afType, redistribute>
      List<String[]> addressFamilyRedistributeList = new ArrayList<String[]>();
      // template: <templateName, remoteAs>
      List<String[]> templateRemoteAsList = new ArrayList<String[]>();
      // VRF: <name>
      List<String> vrfList = new ArrayList<String>();
      boolean aggregated = false;
      private void Aggregate(){
         if(!aggregated){
            for(String[] item: neighborList){
               String[] entry = (String[])ArrayUtils.addAll(item, new String[]{"router_bgp", asNum, "NA"});
               ruleList.add(entry);
            }
            for(String[] item: neighborRemoteAs){
               if(item[0].equals(GROUP_T)){
                  String[] entry = new String[]{item[1], item[2], "router_bgp", asNum};
                  groupAsList.add(entry);
               }
               else{
                  String[] entry = (String[])ArrayUtils.addAll(item, new String[]{"router_bgp", asNum});
                  neighborAsList.add(entry);
               }
            }
            for(String[] item: neighborGroup){
               String[] entry = (String[])ArrayUtils.addAll(item, new String[]{"router_bpg", asNum});
               neighborGroupList.add(entry);
            }
            for(String[] item: networks){
               String[] entry = (String[])ArrayUtils.addAll(item, new String[]{"router_bgp", asNum, "NA"});
               networkList.add(entry);
            }
            for(AddressFamily af: addressFamilyList){
               af.Aggregate();
               ruleList.addAll(af.getRuleList());
               neighborAsList.addAll(af.getNeighborAsList());
               groupAsList.addAll(af.getGroupAsList());
               neighborGroupList.addAll(af.getNeighborGroupList());
               networkList.addAll(af.getNetworkList());
               addressFamilyRedistributeList.add(af.getAddressFamilyRedistribute());
            }
            for(NeighborNexus nn: neighborNexusList){
               nn.Aggregate();
               ruleList.addAll(nn.getRuleList());
               neighborAsList.add(nn.getNeighborAs());
               String[] neighborTemplate = nn.getNeighborTemplate();
               if(neighborTemplate!=null) neighborTemplateList.add(neighborTemplate);
            }
            for(Template t: templateList){
               t.Aggregate();
               ruleList.addAll(t.getRuleList());
               templateRemoteAsList.add(t.getTemplateRemoteAs());
            }
            for(VrfNexus vn: vrfNexusList){
               vn.Aggregate();   
               networkList.addAll(vn.getNetworkList());
               neighborTemplateList.addAll(vn.getNeighborTemplateList());
               vrfList.add(vn.getVrf());
            }
            aggregated = true;
         }
      }
      // <neighborType, neighbor, listType, listName, stanza_type, stanza_name, other>
      public String OutputRuleList(String prefix){
         if(!aggregated) Aggregate();
         String out = "";
         for(String[] item: ruleList){
            Assert(item.length == 7, "rule length is not 7");
            out+=prefix;
            out+=item[0]+"\t"+item[1]+"\t"+item[2]+"\t"+item[3];
            out+="\t"+item[4]+"\t"+item[5]+"\t"+item[6]+"\n";
         }
         return out;
      }
      // <neighborType, neighbor, asNum, stanza_type, stanza_name>
      public String OutputNeighborAs(String prefix){
         if(!aggregated) Aggregate();
         String out = "";
         for(String[]item: neighborAsList){
            Assert(item.length == 5, "neighborAs length is not 5");
            out+=prefix;
            out+=item[0]+"\t"+item[1]+"\t"+item[2];
            out+="\t"+item[3]+"\t"+item[4]+"\n";
         }
         return out;
      }
      // <groupName, asNum, stanza_type, stanza_name>
      public String OutputGroupAs(String prefix){
         if(!aggregated) Aggregate();
         String out = "";
         for(String[]item: groupAsList){
            Assert(item.length == 4, "neighborAs length is not 4");
            out+=prefix;
            out+=item[0]+"\t"+item[1]+"\t"+item[2];
            out+="\t"+item[3]+"\n";
         }
         return out;
      }
      // <neighborType, neighbor, group, stanza_type, stanza_name>
      public String OutputGroupNeighbor(String prefix){
         if(!aggregated) Aggregate();
         String out = "";
         for(String[]item: neighborGroupList){
            Assert(item.length == 5, "neighborAs length is not 5");
            out+=prefix;
            out+=item[0]+"\t"+item[1]+"\t"+item[2];
            out+="\t"+item[3]+"\t"+item[4]+"\n";
         }
         return out;
      }
      // <networkType, network, stanza_type, stanza_name, other>
      public String OutputNetworks(String prefix){
         if(!aggregated) Aggregate();
         String out = "";
         for(String[]item: networkList){
            Assert(item.length == 5, "neighborAs length is not 5");
            out+=prefix;
            out+=item[0]+"\t"+item[1]+"\t"+item[2];
            out+="\t"+item[3]+"\t"+item[4]+"\n";
         }
         return out;
      }
      // <neighborType, neighbor, templateName>  
      public String OutputNeighborTemplate(String prefix){
         if(!aggregated) Aggregate();
         String out = "";
         for(String[]item: neighborTemplateList){
            Assert(item.length == 5, "neighborAs length is not 5");
            out+=prefix;
            out+=item[0]+"\t"+item[1]+"\t"+item[2];
            out+="\t"+item[3]+"\t"+item[4]+"\n";
         }
         return out;
      }
      // <afType, redistribute>
      public String OutputAddressFamily(String prefix){
         if(!aggregated) Aggregate();
         String out="";
         for(String[]item: addressFamilyRedistributeList){
            Assert(item.length == 2, "neighborAs length is not 2");
            out+=prefix;
            out+=item[0]+"\t"+item[1]+"\n";
         }
         return out;
      }
      //  <templateName, remoteAs>
      public String OutputTemplateRemoteAs(String prefix){
         if(!aggregated) Aggregate();
         String out="";
         for(String[]item: templateRemoteAsList){
            Assert(item.length == 2, "neighborAs length is not 2");
            out+=prefix;
            out+=item[0]+"\t"+item[1]+"\n";
         }
         return out;
      }
      // <VRF>
      public String OutputVrf(String prefix){
         if(!aggregated) Aggregate();
         String out = "";
         for(String item: vrfList){
            out+=prefix;
            out+=item+"\n";
         }
         return out;
      }
      // <hasBGP, BGPInst, asNum, numListRef, numNeighborAs, numGroupAs, 
      // numNeighborGroup, numUniqueNetwork, numNeighborTemplate, numRedistribute,
      // numTemplate, numVRF, numNetwork, BGPInstAlone, BGPInstInGroup, BGPInstByTemplate>
      public String OutputStat(){
         if(!aggregated) Aggregate();
         int hasBGP, BGPInst, as, numListRef, numNeighborAs, numGroupAs, numNeighborGroup, 
            numUniqueNetwork, numNeighborTemplate, numRedistribute, numTemplate, numVrf, 
            numNetwork, BGPInstAlone, BGPInstInGroup, BGPInstByTemplate;
         // numNeighborAs, numGroupAs, numNeighborGroup, numNeighborTemplate, numTemplate, 
         // BGPInst, BGPInstAlone, BGPInstInGroup, BGPInstByTemplate
         int numArray1[] = getBgpInst();  
         // numUniqueNetwork, numNetwork
         int numArray2[] = getNumNet(); 
         hasBGP = 1;
         BGPInst = numArray1[5];
         as = Integer.parseInt(asNum);
         numListRef = ruleList.size();
         numNeighborAs = numArray1[0];
         numGroupAs = numArray1[1];
         numNeighborGroup = numArray1[2];
         numUniqueNetwork = numArray2[0];
         numNeighborTemplate = numArray1[3];
         numRedistribute = getNumRedistribute();
         numTemplate = numArray1[4];
         numVrf = vrfList.size();
         numNetwork = numArray2[1];
         BGPInstAlone = numArray1[6];
         BGPInstInGroup = numArray1[7];
         BGPInstByTemplate = numArray1[8];
         String out = "";
         out+=hasBGP+"\t"+BGPInst+"\t"+as+"\t"+numListRef+"\t"+numNeighborAs;
         out+="\t"+numGroupAs+"\t"+numNeighborGroup+"\t"+numUniqueNetwork+"\t"+numNeighborTemplate;
         out+="\t"+numRedistribute+"\t"+numTemplate+"\t"+numVrf+"\t"+numNetwork+"\t"+BGPInstAlone;
         out+="\t"+BGPInstInGroup+"\t"+BGPInstByTemplate;
         return out;
         /*return new int[]{hasBGP, BGPInst, as, numListRef, numNeighborAs, numGroupAs, numNeighborGroup, 
               numUniqueNetwork, numNeighborTemplate, numRedistribute, numTemplate, numVrf, 
               numNetwork, BGPInstAlone, BGPInstInGroup, BGPInstByTemplate};
               */
      }
      
      
      private int getNumRedistribute() {
         int count = 0;
         for(String[] item: addressFamilyRedistributeList){
            count+= Integer.parseInt(item[1]);
         }
         return count;
      }
      // <numNeighborAs, numGroupAs, numNeighborGroup, numNeighborTemplate, 
      // numTemplate, BGPInst, BGPInstAlone, BGPInstInGroup, BGPInstByTemplate>
      private int[] getBgpInst() {
         int numNeighborAs = 0, numGroupAs = 0, numNeighborGroup = 0, 
               numNeighborTemplate = 0, numTemplate = 0, BGPInst = 0,
               BGPInstAlone = 0, BGPInstInGroup = 0, BGPInstByTemplate = 0;
         // direct
         for(String[] item: neighborAsList){
            String asNum = item[2];
            if(!asNum.equals("NA")){
               numNeighborAs++;
               BGPInstAlone++;
            }
         }         
         // inherit template
         Set<String> groups_with_as = new HashSet<String>();
         for(String[] item: groupAsList){
            numGroupAs++;
            groups_with_as.add(item[0]);
         }
         for(String[] item: neighborGroupList){
            numNeighborGroup++;
            String group = item[2];
            if(groups_with_as.contains(group)){
               BGPInstInGroup++;
            }
         }
         // inherit template
         Set<String> template_with_as = new HashSet<String>();
         for(String[] item: templateRemoteAsList){
            numTemplate++;
            String as = item[1];
            if(!as.equals("NA")){
               template_with_as.add(item[0]);
            }
         }
         for(String[] item: neighborTemplateList){
            numNeighborTemplate++;
            String template = item[2];
            if(template_with_as.contains(template)){
               BGPInstByTemplate++;
            }
         }
         BGPInst = BGPInstAlone+BGPInstInGroup+BGPInstByTemplate;
         return new int[]{numNeighborAs, numGroupAs, numNeighborGroup, numNeighborTemplate, 
               numTemplate, BGPInst, BGPInstAlone, BGPInstInGroup, BGPInstByTemplate};
      }
      // <numUniqueNetwork, numNetwork>
      private int[] getNumNet() {
         int count = 0;
         Set<String> networks = new HashSet<String>();
         for(String[] item: networkList){
            String net = item[0]+"_"+item[1];
            networks.add(net);
            count++;
         }
         return new int[]{networks.size(), count};
      }

      // asNum
      String asNum;
      public Bgp(String num){
         asNum = num;
      }
      String currentNeighborType = null;
      String currentNeighbor = null;
      public void Neighbor(String type, String neighbor) {
         Assert(currentNeighbor==null, "Bgp:Neighbor currentNeighbor not null");
         currentNeighbor = neighbor;
         currentNeighborType = type;
      }
      public void NeighborExit() {
         Assert(currentNeighbor!=null, "Bgp:NeighborExit currentNeighbor null");
         currentNeighbor = null;
         currentNeighborType = null;
      }
      // <neighborType, neighbor, asNum>
      List<String[]> neighborRemoteAs = new ArrayList<String[]>();
      public void RemoteAs(String asNum2) {
         Assert(currentNeighbor!=null, "Bgp:RemoteAs currentNeighbor null");
         neighborRemoteAs.add(new String[]{currentNeighborType, currentNeighbor, asNum2});         
      }
      // <neighborType, neighbor, groupname>
      List<String[]> neighborGroup = new ArrayList<String[]>();
      public void PeerGroup(String groupname) {
         if(groupname == null){
            // this is a group declaration
            Assert(currentNeighborType.equals(GROUP_T), "Bgp:PeerGroup groupname null but current is not");
            return ;
         }
         Assert(currentNeighbor!=null, "Bgp:PeerGroup currentNeighbor null");
         Assert(!currentNeighborType.equals(GROUP_T), "Bgp:PeerGroup currentNeighborType is group");
         neighborGroup.add(new String[]{currentNeighborType, currentNeighbor, groupname});
      }
      // <neighborType, neighbor, listType, listName>
      List<String[]> neighborList = new ArrayList<String[]>();
      public void List(String type, String name) {
         Assert(currentNeighbor!=null, "Bgp:List currentNeighbor null");
         neighborList.add(new String[]{currentNeighborType, currentNeighbor, type, name});
      }
      // <networkType, network>
      List<String[]> networks = new ArrayList<String[]>();
      public void Network(String type, String net) {
         networks.add(new String[]{type, net});
      }
      // addressFamilyList
      List<AddressFamily> addressFamilyList = new ArrayList<AddressFamily>();
      public void AddAddressFamily(AddressFamily af){
         addressFamilyList.add(af);
      }
      // neighborNexusList
      List<NeighborNexus> neighborNexusList = new ArrayList<NeighborNexus>();
      public void AddNeighborNexus(NeighborNexus currentNeighborNexus) {
         neighborNexusList.add(currentNeighborNexus);
      }
      // templateList
      List<Template> templateList = new ArrayList<Template>();
      public void AddTemplate(Template currentTemplate) {
         templateList.add(currentTemplate);         
      }
      // vrfList
      List<VrfNexus> vrfNexusList = new ArrayList<VrfNexus>();
      public void AddVrfNexus(VrfNexus currentVrfNexus) {
         vrfNexusList.add(currentVrfNexus);
      }
   }
   private class AddressFamily{
      // acl-list: <neighborType, neighbor, listType, listName, stanza_type, 
      //    stanza_name, other>
      List<String[]> ruleList = new ArrayList<String[]>();
      public List<String[]> getRuleList(){ return ruleList; }
      // neighbor-as: <neighborType, neighbor, asNum, stanza_type, stanza_name>
      List<String[]> neighborAsList = new ArrayList<String[]>();
      public List<String[]> getNeighborAsList(){ return neighborAsList; }
      // group-as: <name, asNum, stanza_type, stanza_name>
      List<String[]> groupAsList = new ArrayList<String[]>();
      public List<String[]> getGroupAsList(){ return groupAsList; }
      // neighbor-group: <neighborType, neighbor, group, stanza_type, stanza_name>
      List<String[]> neighborGroupList = new ArrayList<String[]>();
      public List<String[]> getNeighborGroupList(){ return neighborGroupList; }
      // network: <networkType, network, stanza_type, stanza_name, other>
      List<String[]> networkList = new ArrayList<String[]>();
      public List<String[]> getNetworkList(){ return networkList; }
      // redistribute: <afType, redistributeCount>
      public String[] getAddressFamilyRedistribute(){ 
         return new String[] { afType, Integer.toString(redistributeCount) };
      }
      public void Aggregate() {
         for(String[] item: neighborRemoteAs){
            String[] entry = (String[]) ArrayUtils.addAll(item, new String[]{"address-family", afType});
            if(item[0].equals(GROUP_T)) groupAsList.add(entry);
            else neighborAsList.add(entry);
         }
         for(String[] item: neighborsGroup){
            String[] entry = (String[]) ArrayUtils.addAll(item, new String[]{"address-family", afType});
            neighborGroupList.add(entry);
         }
         for(String[] item: neighborList){
            String[] entry = (String[]) ArrayUtils.addAll(item, new String[]{"address-family", afType, "NA"});
            ruleList.add(entry);
         }         
         for(String[] item: networks){
            String[] entry = (String[]) ArrayUtils.addAll(item, new String[]{"address-family", afType, "NA"});
            networkList.add(entry);
         }
      }
      // address-family type
      String afType;
      public AddressFamily(String type){
         afType = type;
      }
      String currentNeighborType=null;
      String currentNeighbor=null;
      public void NeighborExit() {
         Assert(currentNeighbor!=null, "AddressFamily:NeighborExit currentNeighbor null");
         currentNeighbor = null;
         currentNeighborType = null;
      }
      public void Neighbor(String type, String neighbor) {
         Assert(currentNeighbor==null, "AddressFamily:Neighbor currentNeighbor not null");
         currentNeighbor = neighbor;
         currentNeighborType = type;
      }
      // <neighborType, neighbor, asNum>
      List<String[]> neighborRemoteAs = new ArrayList<String[]>();
      public void RemoteAs(String asNum) {
         Assert(currentNeighbor!=null, "AddressFamily:NeighborExit currentNeighbor null");
         neighborRemoteAs.add(new String[]{currentNeighborType, currentNeighbor, asNum});
      }
      // <neighborType, neighbor, groupname> 
      List<String[]> neighborsGroup = new ArrayList<String[]>();
      public void PeerGroup(String groupname) {
         Assert(currentNeighbor!=null, "AddressFamily:NeighborExit currentNeighbor null");
         Assert(!currentNeighborType.equals(GROUP_T), "AddressFamily:PeerGroup currentNeighborType is group");
         neighborsGroup.add(new String[]{currentNeighborType, currentNeighbor, groupname});
      }
      // <neighborType, neighbor, listType, list>
      List<String[]> neighborList = new ArrayList<String[]>();
      public void List(String type, String name) {
         Assert(currentNeighbor!=null, "AddressFamily:NeighborExit currentNeighbor null");
         neighborList.add(new String[]{currentNeighborType, currentNeighbor, type, name});
         
      }
      // redistributeCount
      int redistributeCount = 0;
      public void Redistribute() {
         redistributeCount++;
      }
      // <type, network>
      List<String[]> networks = new ArrayList<String[]>();
      public void Network(String type, String net) {
         networks.add(new String[]{type, net});
      }
   }
   private class NeighborNexus{
      // acl-list: <neighborType, neighbor, listType, listName, 
      //    stanza_type, stanza_name, other>
      List<String[]> ruleList = new ArrayList<String[]>();
      public List<String[]> getRuleList(){ return ruleList; }
      // <neighborType, neighbor, as, stanza_type, stanza>
      public String[] getNeighborAs(){
         return new String[]{ type, neighbor, asNum, "neighborNexus", "NA" };
      }
      // <neighborType, neighbor, templateName, stanza_type, stanza_name>  
      public String[] getNeighborTemplate(){
         if(inherit==null) return null;
         return new String[]{ type, neighbor, inherit, "neighborNexus", "NA"};
      }
      public void Aggregate() {
         for(String[] item: afList){
            String[] entry = new String[]{type, neighbor, item[1], item[2], "neighborNexus", "NA", item[0]};
            ruleList.add(entry);
         }         
      }
      // neighborType, neighbor, asNum
      String type, neighbor, asNum;
      public NeighborNexus(String t, String n, String a) {
         type = t;
         neighbor = n;
         if(a!=null)
            asNum =a;
         else asNum = "NA";
      }
      String currentAf = null;
      public void Af(String afHeader) {
         Assert(currentAf==null, "NeighborNexus:Af currentAf not null");
         currentAf = afHeader;
      }
      public void AfExit() {
         Assert(currentAf!=null, "NeighborNexus:AfExit currentAf null");
         currentAf = null;         
      }
      // <address-familiy, listType, listName>
      List<String[]> afList = new ArrayList<String[]>();
      public void AfList(String type2, String name) {
         Assert(currentAf!=null, "NeighborNexus:AfList currentAf null");
         afList.add(new String[]{currentAf, type2, name});
      }
      // inherit
      String inherit =null;
      public void Inherit(String template) {
         Assert(inherit==null, "NeighborNexus:Inherit inherit not null");
         inherit = template;
      }
   }
   private class Template{
      // acl-list: <neighborType, neighbor, listType, listName, 
      //   stanza_type, stanza_name, other>
      List<String[]> ruleList = new ArrayList<String[]>();
      public List<String[]> getRuleList(){ return ruleList; }
      // <templateName, remoteAs>
      public String[] getTemplateRemoteAs(){
         return new String[]{name, remoteAs};
      }
      public void Aggregate() {
         for(String[] item: afList){
            String[] entry = new String[]{"NA", "NA", item[1], item[2], "Template", name, item[0]};
            ruleList.add(entry);
         }
      }
      // template name
      String name;
      public Template(String templatename) {
         name = templatename;
      }
      String currentAf=null;;
      public void Af(String afHeader) {
         Assert(currentAf==null, "Template:Af currentAf not null");
         currentAf = afHeader;
      }
      public void AfExit() {
         Assert(currentAf!=null, "Template:AfExit currentAf null");
         currentAf = null;
      }
      // remoteAs
      String remoteAs="NA";
      public void RemoteAs(String asNum) {
         Assert(remoteAs.equals("NA"), "Template:RemoteAs remoteAs not null");
         remoteAs = asNum;
      }
      // <address-family, listType, listName>
      List<String[]> afList = new ArrayList<String[]>();
      public void AfList(String type, String name) {
         Assert(currentAf!=null, "Template:AfList currentAf null");
         afList.add(new String[]{currentAf, type, name});
      }
   }
   private class VrfNexus{
      // network: <networkType, network, stanza_type, stanza_name, other>
      List<String[]> networkList = new ArrayList<String[]>();
      public List<String[]> getNetworkList(){ return networkList;}
      // neighbor-template: <neighborType, neighbor, templateName>  
      List<String[]> neighborTemplateList = new ArrayList<String[]>();
      public List<String[]> getNeighborTemplateList(){ return neighborTemplateList; }      
      // VRF: <name>
      public String getVrf(){ return name; }
      public void Aggregate() {
         for(String[] item: afNetworkList){
            String[] entry = new String[]{item[1], item[2], "vrf", name, item[0]};
            networkList.add(entry);
         }
         for(String[] item: neighborTemplate){
            String[] entry = (String[]) ArrayUtils.addAll(item, new String[]{"vrf", name});
            neighborTemplateList.add(entry);
         }
      }
      // Vrf name
      String name;
      public VrfNexus(String n) {
         name = n;
      }
      String currentNeighbor= null;
      String currentNeighborType = null;
      public void NeighborNexus(String type, String net) {
         Assert(currentNeighbor==null, "VrfNexus:NeighborNexus currentNeighbor not null");
         currentNeighbor = net;
         currentNeighborType = type;
      }
      public void NeighborNexusExit() {
         Assert(currentNeighbor!=null, "VrfNexus:NeighborNexusExit currentNeighbor null");
         currentNeighbor = null;
         currentNeighborType = null;
      }
      // <neighborType, neighbor, template>
      List<String[]> neighborTemplate = new ArrayList<String[]>();
      public void NeighborNexusInherit(String template) {
         Assert(currentNeighbor!=null, "VrfNexus:NeighorNexusInherit currentNeighbor null");
         neighborTemplate.add(new String[]{currentNeighborType, currentNeighbor, template});
      }
      String currentAf = null;
      public void Af(String afHeader) {
         Assert(currentAf==null, "VrfNexus:Af currentAf not null");
         currentAf = afHeader;
      }
      public void AfExit() {
         Assert(currentAf!=null, "VrfNexus:AfExit currentAf null");
         currentAf = null; 
      }
      // <address-family, networkType, network>
      List<String[]> afNetworkList = new ArrayList<String[]>();
      public void AfNetwork(String type, String net) {
         Assert(currentAf!=null, "VrfNexus:AfNetwork currentAf null");
         afNetworkList.add(new String[]{currentAf, type, net});
      }      
   }
}


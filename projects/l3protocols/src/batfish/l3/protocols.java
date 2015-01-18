package batfish.l3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import batfish.representation.Ip;
import batfish.util.Util;

public class protocols {
   String warning = "";
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
      // TODO
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
   public String OutputStat(String prefix) {
      // numOSPFRouter, OSPFInst, numOSPFNetwork, numOSPFPassiveIface, numOSPFRedistribute      
      List<ospfRouterNetworkArea> ornas = getOspfRouterNetworkAreaList();
      List<ospfRouterPassiveIface> opis = getOspfRouterPassiveIfaceList();
      String out = "";
      out = prefix;
      out+=ospfs.size();
      out+="\t"+getOspfInst();
      out+="\t"+ornas.size();
      out+="\t"+opis.size();
      out+="\t"+getOspfRedistribute();
      out+="\n";
      
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
            return null;
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
   
   
}


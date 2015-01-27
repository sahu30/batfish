package batfish.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import batfish.util.Util;

public class Postprocessor {
   String path;
   String devicelist;
   public Postprocessor(String p, String dl){
      path = p;
      devicelist=dl;
   }
   // stamp, numDevice, mstpInst, ospfInst, ospfPeering, avgOspfSize, 
   // bgpInst, bgpPeering, avgBgpSize 
   // ospfInternalPeering, ospfExternalPeering,
   // bgpInternalPeering, bgpExternalPeering,
   // ibgpInternal, ebgpInternal
   // ibgpExternal, ibgpExternal
   // intraRef
   public void Process(){
      // read file
      ReadFiles();
      // process
      for(Stamp stamp: stamps.values()){
         stamp.Process();
      }
      // output
      Output();
   }
   private void CreateDevices(List<String[]> devices){
      for(String[] d: devices){
         String st = d[0];
         String de = d[1];
         Stamp stamp = null;
         if(stamps.containsKey(st)){
            stamp = stamps.get(st);
         }
         else{
            stamp = new Stamp(st);
         }
         stamp.CreateDevice(de);
         stamps.put(st, stamp);
      }
   }
   private void ReadFiles(){
      // File list
      List<String[]> devices = ReadFromFile(devicelist);
      CreateDevices(devices);
      //
      String acl = path+"/acl.txt";
      List<String[]> acls = ReadFromFile(acl);
      CreateAcl(acls);
      
      String bgpAs = path+"/bgpAs.txt";
      List<String[]> bgpAses = ReadFromFile(bgpAs);
      CreateBgpAs(bgpAses);
      
      String bgpNeighborAs = path+"/bgpNeighborAs.txt";
      List<String[]> bgpNeighbors = ReadFromFile(bgpNeighborAs);
      CreateBgpNeighbors(bgpNeighbors);
      
      String ifaceIp = path+"/ifaceIp.txt";
      List<String[]> ifaceIps = ReadFromFile(ifaceIp);
      CreateIfaceIps(ifaceIps);
      
      String intraReference = path+"/intraReference.txt";
      List<String[]> intraRef = ReadFromFile(intraReference);
      CreateIntraRef(intraRef);
      
      String ospfNetwork = path+"/ospfNetwork.txt";
      List<String[]> ospfNet = ReadFromFile(ospfNetwork);
      CreateOspfNet(ospfNet);
      
      String ospfProc = path+"/ospfProc.txt";
      List<String[]> ospfProcs = ReadFromFile(ospfProc);
      CreateOspfProc(ospfProcs);
      
      String routemap = path+"/routemap.txt";
      List<String[]> rm = ReadFromFile(routemap);
      CreateRoutemap(rm);
      
      String mstpInstance = path+"/mstpInstance.txt";
      List<String[]> mstpInst = ReadFromFile(mstpInstance);
      CreateMstpInstance(mstpInst);
   }
   private void CreateMstpInstance(List<String[]> mstpInst) {
      for(String[] inst: mstpInst){
         if(inst[5].equals("NA"))
            continue;
         String stamp = inst[0];
         String device = inst[1];
         stamps.get(stamp).devices.get(device).AddMstpInst(inst[5]);
      }      
   }

   private void CreateRoutemap(List<String[]> rm) {
      for(String[] routemap: rm){
         String stamp = routemap[0];
         String device = routemap[1];
         stamps.get(stamp).devices.get(device).AddRoutemap(routemap[4]);
      }
   }

   private void CreateOspfProc(List<String[]> ospfProcs) {
      for(String[] proc: ospfProcs){
         String stamp = proc[0];
         String device = proc[1];
         stamps.get(stamp).devices.get(device).AddOspfProc(proc[4]);
      }      
   }

   private void CreateOspfNet(List<String[]> ospfNet) {
      for(String[] net: ospfNet){
         String stamp = net[0];
         String device = net[1];
         stamps.get(stamp).devices.get(device).AddOspfNetwork(net[4], net[5], net[6]);
      }      
   }

   private void CreateIntraRef(List<String[]> intraRef) {
      for(String[] ref: intraRef){
         String stamp = ref[0];
         String device = ref[1];
         stamps.get(stamp).devices.get(device).AddIntraRefDst(ref[6], ref[7]);
      }      
   }

   private void CreateIfaceIps(List<String[]> ifaceIps) {
      for(String[] ip: ifaceIps){
         String stamp = ip[0];
         String device = ip[1];
         stamps.get(stamp).devices.get(device).AddIfaceIp(ip[4], ip[5], ip[6]);
      }      
   }

   private void CreateBgpNeighbors(List<String[]> bgpNeighbors) {
      for(String[] neighbor: bgpNeighbors){
         if(neighbor[5].equals("null"))
            continue;
         String stamp = neighbor[0];
         String device = neighbor[1];
         stamps.get(stamp).devices.get(device).AddBgpNeighbor(neighbor[4],neighbor[5], neighbor[6], neighbor[7]);
      }      
   }

   private void CreateBgpAs(List<String[]> bgpAses) {
      for(String[] as: bgpAses){
         String stamp = as[0];
         String device = as[1];
         stamps.get(stamp).devices.get(device).AddBgpAs(as[4]);
      }      
   }

   private void CreateAcl(List<String[]> acls) {
      for(String[] acl: acls){
         String stamp = acl[0];
         String device = acl[1];
         stamps.get(stamp).devices.get(device).AddAcl(acl[5]);
      }      
   }
   // stamp, numDevice, mstpInst, ospfInst, ospfPeering, avgOspfSize, 
   // bgpInst, bgpPeering, avgBgpSize 
   // ospfInternalPeering, ospfExternalPeering,
   // bgpInternalPeering, bgpExternalPeering,
   // ibgpInternal, ebgpInternal
   // ibgpExternal, ibgpExternal
   private void Output(){
      // stamp, numDevice, mstpInst, ospfInst, ospfPeering, avgOspfSize, 
      // bgpInst, bgpPeering, avgBgpSize 
      // ospfInternalPeering, ospfExternalPeering,
      // bgpInternalPeering, bgpExternalPeering,
      // ibgpInternal, ebgpInternal
      // ibgpExternal, ibgpExternal
      // intraRef
      String stat = "stamp,numDevice,mstpInst,ospfInst,ospfPeering,avgOspfSize,bgpInst,bgpPeering,avgBgpSize,ospfInternalPeering,ospfExternalPeering,bgpInternalPeering, bgpExternalPeering,ibgpInternal,ebgpInternal,ibgpExternal,ibgpExternal,intraRef\n";
      for(Stamp stamp: stamps.values()){
         stat+= stamp.Stat();
      }
      WriteToFile(stat, path+"/stat.txt", false);
      
      String ospfInternalPeer = "";
      for(Stamp stamp: stamps.values()){
         ospfInternalPeer+=stamp.OutputOspfInternalPeer();
      }
      WriteToFile(ospfInternalPeer, path+"/ospfInternalPeer.txt", false);
      
      String ospfExternalPeer ="";
      for(Stamp stamp: stamps.values()){
         ospfExternalPeer+=stamp.OutputOspfExternalPeer();
      }
      WriteToFile(ospfExternalPeer, path+"/ospfExternalPeer.txt", false);
      
      String bgpInternalPeer ="";
      for(Stamp stamp: stamps.values()){
         bgpInternalPeer+=stamp.OutputBgpInternalPeer();
      }
      WriteToFile(bgpInternalPeer, path+"/bgpInternalPeer.txt", false);
      
      String bgpExternalPeer ="";
      for(Stamp stamp: stamps.values()){
         bgpExternalPeer+=stamp.OutputBgpExternalPeer();
      }
      WriteToFile(bgpExternalPeer, path+"/bgpExternalPeer.txt", false);
   }
   
   Map<String, Stamp> stamps = new HashMap<String, Stamp>();
   private class Stamp{
      String name;
      public Stamp(String n){
         name = n;
      }
      
      public String OutputOspfInternalPeer(){
         String out="";
         for(Device d: devices.values()){
            out+=d.OutputOspfInternalPeer(name);
         }
         return out;
      }
      public String OutputOspfExternalPeer(){
         String out="";
         for(Device d: devices.values()){
            out+=d.OutputOspfExternalPeer(name);
         }
         return out;
      }
      public String OutputBgpInternalPeer(){
         String out="";
         for(Device d: devices.values()){
            out+=d.OutputBgpInternalPeer(name);
         }
         return out;
      }
      public String OutputBgpExternalPeer(){
         String out="";
         for(Device d: devices.values()){
            out+=d.OutputBgpExternalPeer(name);
         }
         return out;
      }
      
      
      public String Stat() {
         double ospfPeer[]= new double[2];
         ospfPeer[0] = ospfPeering[0];         ospfPeer[1] = ospfPeering[1];
         ospfPeer[0]/=2;
         double bgpPeer[]=new double [4];
         bgpPeer[0]=bgpPeering[0]; bgpPeer[1]=bgpPeering[1]; bgpPeer[2]=bgpPeering[2]; bgpPeer[3]=bgpPeering[3];
         bgpPeer[0]/=2;        bgpPeer[1]/=2;
         String stamp = name; 
         int numParsed = devices.size();
         int mstpIns = mstpInst.contains("1-4094")? 1 :mstpInst.size()+1;
         int ospfIns = ospfInst.size();
         double numOspfPeering = ospfPeer[0]+ospfPeer[1];
         double avgOspfSize = ospfIns==0? 0: numOspfPeering/ospfIns;
         int bgpIns = bgpInst.size();
         double numBgpPeering = bgpPeer[0]+bgpPeer[1]+bgpPeer[2]+bgpPeer[3];
         double avgBgpSize = bgpIns ==0? 0: numBgpPeering/bgpIns;
         double ospfInternalPeer = ospfPeer[0];
         double ospfExternalPeer = ospfPeer[1];
         double bgpInternalPeer = bgpPeer[0]+bgpPeer[1];
         double bgpExternalPeer = bgpPeer[2]+bgpPeer[3];
         double ibgpInternalPeer = bgpPeer[0];
         double ebgpInternalPeer = bgpPeer[1];
         double ibgpExternalPeer = bgpPeer[2];
         double ebgpExternalPeer = bgpPeer[3];
         String out=stamp+"\t"+numParsed+"\t"+mstpIns+"\t"+ospfIns+"\t"+numOspfPeering+"\t"+avgOspfSize+"\t"+
               bgpIns+"\t"+numBgpPeering+"\t"+avgBgpSize+"\t"+ospfInternalPeer+"\t"+ospfExternalPeer+"\t"+
               bgpInternalPeer+"\t"+bgpExternalPeer+"\t"+
               ibgpInternalPeer+"\t"+ebgpInternalPeer+"\t"+ibgpExternalPeer+"\t"+ebgpExternalPeer+"\t"
               +intraRef+"\n";
         return out;
      }

      int intraRef = -1;
      Set<String> mstpInst = new HashSet<String>();
      Set<String> ospfInst = null;
      Set<String> bgpInst = null;
      // <internal, external>
      int [] ospfPeering = null;
      // <internal igbp, inter ebgp, external ibgp, external ebgp>
      int [] bgpPeering = null;
      public void Process() {
         // count intra ref
         AggregateIntraRef();
         // count mstp
         AggregateMstpInst();
         // cout stamp ospf inst
         AggregateOspfInst();
         // check peering
         CheckOspfPeering();
         // count stamp bgp inst
         AggregateBgpInst();
         // check ibgp/ebgp, check internal/external   static -> ifaceIp or dynamic (internal); otherwise external
         CheckBgpPeering();         
      }

      private void CheckBgpPeering() {
         // aggregate neighbor   iface ip & dynamic neighbor
         for(Device device: devices.values()){
            device.AggregateNeighbor();
         }
         // <internal igbp, inter ebgp, external ibgp, external ebgp>
         int []total_peering = new int[]{0, 0, 0, 0};
         for(Device d1: devices.values()){
            for(Device d2: devices.values()){
               if(d1.getName().equals(d2.getName())){
                  continue;
               }
               int peering[] = d1.CheckBgpPeering(d2);
               total_peering[0]+=peering[0];
               total_peering[1]+=peering[1];
               total_peering[2]+=peering[2];
               total_peering[3]+=peering[3];
            }
         }
      //   total_peering[0]/=2;
      //   total_peering[1]/=2;
         bgpPeering = total_peering;
      }

      private void CheckOspfPeering() {
         // put iface to proc
         for(Device device: devices.values()){
            device.AggregateIface();
         }
         // check peering in the same proc
         int []total_peering = new int[]{0, 0};
         for(Device d1: devices.values()){
            for(Device d2: devices.values()){
               if(d1.getName().equals(d2.getName())){
                  continue;
               }
               int peering[] = d1.CheckOspfPeering(d2);
               total_peering[0]+=peering[0];
               total_peering[1]+=peering[1];
            }
         }
      //   total_peering[0]/=2;
         ospfPeering = total_peering;
      }

      private void AggregateBgpInst() {
         Set<String> ases = new HashSet<String>();
         for(Device device: devices.values()){
            ases.addAll(device.getBgpAses());
         }
         ases.remove("NA");
         bgpInst = ases;
      }

      private void AggregateOspfInst() {
         Set<String> procs = new HashSet<String>();
         for(Device device: devices.values()){
            procs.addAll(device.getOspfProcs());
         }
         procs.remove("NA");
         ospfInst = procs;
      }

      private void AggregateMstpInst() {
         Set<String> mstpRanges = new HashSet<String>();
         for(Device device: devices.values()){
            mstpRanges.addAll(device.getMstpRange());
         }
         mstpInst = mstpRanges;
      }

      private void AggregateIntraRef() {
         int count = 0;
         for(Device device:devices.values()){
            count+=device.CountIntraRef();
         }
         intraRef = count;
      }

      public void CreateDevice(String name){
         Device device = new Device(name);
         devices.put(name, device);
      }
      
      public Map<String, Device> devices = new HashMap<String, Device>();
   }
   
   private class Device{
      String name;
      public Device(String n){
         name = n;
      }
      // <name, neighbor, remote_name, ip>
      List<String[]> bgpInternalPeer = new ArrayList<String[]>();
      // <name, neighbor ip>
      List<String[]> bgpExternalPeer = new ArrayList<String[]>();
       
      public String OutputBgpInternalPeer(String stamp){
         if(bgpInternalPeer.size()==0){
            return "";
         }
         String out = "";
         for(String[] p: bgpInternalPeer){
            out+=stamp+"\t"+p[0]+"\t"+p[1]+"\t"+p[2]+"\t"+p[3]+"\n";
         }
         return out;
      }
      public String OutputBgpExternalPeer(String stamp){
         if(bgpExternalPeer.size()==0){
            return "";
         }
         String out = "";
         for(String[] p: bgpExternalPeer){
            out+=stamp+"\t"+p[0]+"\t"+p[1]+"\n";
         }
         return out;   
      }
      // <internal ibgp, inter ebgp, extern ibgp, extern ebgp>
      public int[] CheckBgpPeering(Device d2) {
         int []peers = new int[]{0, 0, 0, 0};
         for(String[] neighbor: bgpNeighbors){
            String ip = neighbor[1];
            String mask =neighbor[2];
            if(!mask.equals("255.255.255.255")){
               continue;
            }
            long ip_l = Util.ipToLong(ip);
            boolean internal = false;
            for(String[] server: d2.neighborServers){
               String s_ip = server[0];
               String s_mask = server[1];
               long s_ip_l = Util.ipToLong(s_ip);
               long s_mask_l = Util.ipToLong(s_mask);
               if( (ip_l & s_mask_l)==(s_ip_l & s_mask_l) ){
                  internal = true;
                  bgpInternalPeer.add(new String[]{name, ip, d2.getName(), s_ip});
                  break;
               }
            }
            String local = neighbor[0];
            String remote = neighbor[3];
            boolean ibgp = local.equals(remote);
            if(internal){
               if(ibgp) peers[0]++;                  
               else peers[1]++;
            }
            else{
               bgpExternalPeer.add(new String[]{name, ip});
               if(ibgp) peers[2]++;
               else peers[3]++;
            }
         }
         return peers;
      }

      List<String[]> neighborServers =null;
      public void AggregateNeighbor() {
         neighborServers = new ArrayList<String[]>();
         // iface, mask should be 255.255.255.255
         for(String[] addr: address){
            String ip = addr[0];
            String mask = addr[1];
            neighborServers.add(new String[]{ip, "255.255.255.255"});
         }
         /*
         // dynamic neighbor
         for(String[] neighbor: bgpNeighbors){
            String ip = neighbor[1];
            String mask = neighbor[2];
            if(!mask.equals("255.255.255.255")){
               neighborServers.add(new String[]{ip,mask});
            }
         }
         */
      }

      Map<String, List<String[]>> ospfProc_Ip = null;
      public void AggregateIface() {
         ospfProc_Ip = new HashMap<String, List<String[]>>();
         for(String[] addr: address){
            for(String []net: ospfNetworks){
               String ip = addr[0];
               String network = net[1];
               String mask = net[2];
               long ip_l = Util.ipToLong(ip);
               long net_l = Util.ipToLong(network);
               long mask_l = Util.ipToLong(mask);
               if((ip_l & mask_l)==(net_l & mask_l)){
                  String proc = net[0];
                  List<String[]> ips;
                  if(ospfProc_Ip.containsKey(proc)){
                     ips = ospfProc_Ip.get(proc);
                  }
                  else{
                     ips = new ArrayList<String[]>();
                  }
                  ips.add(new String[]{addr[0], addr[1]});
                  ospfProc_Ip.put(proc, ips);
               }
            }
         }
      }
      
      public String OutputOspfInternalPeer(String stamp){
         if(ospfInternalPeers.size()==0) return "";
         String out = "";
         for(String[] p: ospfInternalPeers){
            out+=stamp+"\t"+p[0]+"\t"+p[1]+"\t"+p[2]+"\t"+p[3]+"\t"+p[4]+"\t"+p[5]+"\n";
         }
         return out;
      }
      public String OutputOspfExternalPeer(String stamp){
         if(ospfExternalPeers.size()==0) return "";
         String out = "";
         for(String[] p: ospfExternalPeers){
            out+=stamp+"\t"+p[0]+"\t"+p[1]+"\t"+p[2]+"\n";
         }
         return out;
      }

      // <from_device, ip, mask, to_device, ip, mask>
      List<String[]> ospfInternalPeers = new ArrayList<String[]>();
      // <device ip, mask>
      List<String[]> ospfExternalPeers = new ArrayList<String[]>();
      public int[] CheckOspfPeering(Device d2) {
         // <internal, external>
         int peer[] = new int[]{0, 0};
         for(String proc: ospfProc_Ip.keySet()){
            if(!d2.ospfProc_Ip.containsKey(proc)){
               peer[1]+= ospfProc_Ip.get(proc).size();
               continue;
            }
            List<String[]> ips1 = ospfProc_Ip.get(proc);
            List<String[]> ips2 = d2.ospfProc_Ip.get(proc);
            for(String[] ip1: ips1){
               boolean internal = false;
               for(String[] ip2: ips2){
                  long ip1_l = Util.ipToLong(ip1[0]);
                  long mask1_l = Util.ipToLong(ip1[1]);
                  long ip2_l = Util.ipToLong(ip2[0]);
                  long mask2_l = Util.ipToLong(ip2[1]);
                  if(mask1_l==mask2_l &&
                        (ip1_l & mask1_l)==(ip2_l & mask2_l) ){
                     internal = true;
                     ospfInternalPeers.add(new String[]{name, ip1[0], ip1[1], d2.getName(), ip2[0], ip2[1]});
                     break;
                  }
               }
               if(internal) peer[0]++;
               else{
                  ospfExternalPeers.add(new String[]{name, ip1[0], ip1[1]});
                  peer[1]++;
               }
            }
         }
         return peer;
      }

      public String getName() {
         return name;
      }

      public Set<String> getBgpAses() {
         return bgpAs;
      }

      public Set<String> getOspfProcs() {
         return ospfProcs;
      }

      public Set<String> getMstpRange() {
         return mstpInst;
      }

      public int CountIntraRef() {
         int count = 0;
         for(String[] ref: intraRefDst){
            String type = ref[0];
            String name = ref[1];
            if(type.equals("acl")){
               if(acls.contains(name))
                  count++;
            }
            else if(type.equals("routemap")){
               if(routemaps.contains(name))
                  count++;
            }
         }
         return count;
      }

      // mstp <range>
      Set<String> mstpInst = new HashSet<String>();
      public void AddMstpInst(String inst) {
         mstpInst.add(inst);
      }

      Set<String> acls = new HashSet<String>();
      public void AddAcl(String a) {
         acls.add(a);
      }

      // bgp as
      Set<String> bgpAs =new HashSet<String>();
      public void AddBgpAs(String as) {
         bgpAs.add(as);
      }

      // <local as, neighbor ip, neighbor mask, remote-as>
      List<String[]> bgpNeighbors = new ArrayList<String[]>();
      public void AddBgpNeighbor(String local, String ip, String mask,
            String remote) {
         if(ip.equals(null) || ip == null)
            return ;
         bgpNeighbors.add(new String[]{local, ip, mask, remote});
      }

      // iface address
      // <ip, mask>
      List<String[]> address = new ArrayList<String[]>();
      public void AddIfaceIp(String name, String ip, String mask) {
         address.add(new String[]{ip, mask});
      }

      // <type, name>
      List<String[]> intraRefDst = new ArrayList<String[]>();
      public void AddIntraRefDst(String dstType, String dstName) {
         intraRefDst.add(new String[]{ dstType, dstName });
      }

      List<String[]> ospfNetworks = new ArrayList<String[]>();
      public void AddOspfNetwork(String proc, String ip, String mask) {
         ospfNetworks.add(new String[]{proc,ip,mask});
      }

      // ospf proc
      Set<String> ospfProcs = new HashSet<String>();
      public void AddOspfProc(String ospf) {
         ospfProcs.add(ospf);
      }

      // routemap
      Set<String> routemaps = new HashSet<String>();
      public void AddRoutemap(String r) {
         routemaps.add(r);
      }      
   }
   
   private List<String[]> ReadFromFile(String file){
      List<String[]> ret = new ArrayList<String[]>();
      try {
         BufferedReader br = new BufferedReader(new FileReader(file));  
         String line = null;  
         while ((line = br.readLine()) != null)  {
            line=line.trim();
            ret.add(line.split("\t"));
         }
         br.close();
      } catch (IOException e1) {
         System.out.println("file: "+file);
         e1.printStackTrace();
      }
      return ret;
   }
   private void WriteToFile(String content, String file, boolean append){
      Writer writer  = null;
      try {
          writer = new BufferedWriter(new FileWriter(file, append));
          writer.write(content);
          writer.close();
      } catch (Exception ex) {
          System.out.println("cannot write to file "+file);
      }   
   }
}

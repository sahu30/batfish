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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import batfish.util.Util;

public class Postprocessor {

   Lock inputLock, outputLock;
   
   int numThreads = 5;
   int stampCount = 0;
   long startTime;
   private class processThread implements Runnable{
      int index;
      String stamp;
      @Override
      public void run() {
         while(true){
            // fetch
            inputLock.lock();
            if(stampsQueue.isEmpty()){
               inputLock.unlock();
               break;
            }
            stamp = stampsQueue.remove();
            stampCount++;
            index = stampCount;
            inputLock.unlock();
            // init
            this.Init();
            // process
            this.Process();
            // output
            outputLock.lock();
            this.Output();
            System.out.println("time(s): "+(System.nanoTime()-startTime)/1000000000+", index: "+index+", stamp "+stamp+" is done");
            outputLock.unlock();
         }
      }
      

      // int intraRefCount = 0;
      // int numDevice = 0;
      // Set<String> bgpInst = new HashSet<String>();
      // Set<String> ospfInst = new HashSet<String>();
      // Set<String> mstpInst = new HashSet<String>();
      // Map<String, List<String[]>> ospfKey_instances = new HashMap<String, List<String[]>>();
         // key: <ospfProc, ifaceSubnet, ifaceMask>
         // instance: <device, ifaceName, ospfProc, ifaceIp, ifaceMask, ospfNetworkIp, ospfNetworkMask>
      // int ospfInternal = 0;
      // int ospfExternal = 0;
      // int ospfTotal = 0;
         // <device, local-as, neighborIp, remote-as, internal, ibgp>
      // List<String[]> neighbors = new ArrayList<String[]>();
      // double bgpInternalIbgp = 0;
      // double bgpInternalEbgp = 0;
      // double bgpExternalIbgp = 0;
      // double bgpExternalEbgp = 0;
      // double bgpTotal = 0;
      // double bgpInternal = 0;
      // double bgpExternal = 0;
      // String stat, statMstp, statBgpInst, statOspfInst, statBgpPeering, statOspfPeering;
      private void Init() {
         intraRefCount = 0;
         numDevice = 0;
         bgpInst.clear();
         ospfInst.clear();
         mstpInst.clear();
         ospfKey_instances.clear();
         ospfInternal = 0;
         ospfExternal = 0;
         ospfTotal = 0;
         neighbors.clear();
         bgpInternalIbgp = 0;
         bgpInternalEbgp = 0;
         bgpExternalIbgp = 0;
         bgpExternalEbgp = 0;
         bgpTotal = 0;
         bgpInternal = 0;
         bgpExternal = 0;
      }

      // stat:
      // stamp, numDevice, mstpInst, 
      // totalPeering, totalInternal(ospfInternal+bgpInternal), totalExternal,
      // ospfInst, ospfPeering, avgOspfSize, 
      // bgpInst, bgpPeering, avgBgpSize 
      // ospfInternalPeering, ospfExternalPeering,
      // bgpInternalPeering, bgpExternalPeering,
      // ibgpInternal, ebgpInternal
      // ibgpExternal, ebgpExternal
      // intraRef
      

      // stat
      // statMstp
      // statBgpInst
      // statOspfInst
      // statBgpPeering
      // statOspfPeering
      String stat, statMstp, statBgpInst, statOspfInst, statBgpPeering, statOspfPeering;
      private void FormatOutput() {
         double ospfPeering = ospfInternal + ospfExternal;
         double avgOspfSize = ospfInst.size()==0? 0: ospfPeering/ospfInst.size();
         double bgpPeering = bgpInternal+bgpExternal;
         double avgBgpSize = bgpInst.size()==0? 0: bgpPeering/bgpInst.size();
         double totalPeering = ospfPeering+bgpPeering;
         double totalInternal = ospfInternal+bgpInternal;
         double totalExternal = ospfExternal+bgpExternal;
         stat = stamp+"\t"+numDevice+"\t"+(mstpInst.size()+1)+"\t"+
               totalPeering+"\t"+totalInternal+"\t"+totalExternal+"\t"+
               ospfInst.size()+"\t"+ospfPeering+"\t"+avgOspfSize+"\t"+
               bgpInst.size()+"\t"+bgpPeering+"\t"+avgBgpSize+"\t"+
               ospfInternal+"\t"+ospfExternal+"\t"+
               bgpInternal+"\t"+bgpExternal+"\t"+
               bgpInternalIbgp+"\t"+bgpInternalEbgp+"\t"+
               bgpExternalIbgp+"\t"+bgpExternalEbgp+"\t"+
               intraRefCount+"\n";
         
         statMstp = stamp +"\t" + SetToString(mstpInst)+"\n";
         statBgpInst = stamp+"\t" + SetToString(bgpInst)+"\n";
         statOspfInst = stamp+"\t" + SetToString(ospfInst)+"\n";
         // key: <ospfProc, ifaceSubnet, ifaceMask>
         // instance: <device, ifaceName, ospfProc, ifaceIp, ifaceMask, ospfNetworkIp, ospfNetworkMask>
         //  Map<String, List<String[]>> ospfKey_instances
         List<String[]> ospfInstances = new ArrayList<String[]>();
         for(List<String[]> i: ospfKey_instances.values()){
            ospfInstances.addAll(i);
         }
         statOspfPeering = RecordToColumn(stamp, ospfInstances);
         // <device, local-as, neighborIp, remote-as, internal, ibgp>  
         // List<String[]> neighbors
         statBgpPeering = RecordToColumn(stamp, neighbors );
      }
      private void Output(){
         WriteToFile(stat, path+"/stat.txt", true);
         WriteToFile(statMstp, path+"/statMstp.txt", true);
         WriteToFile(statBgpInst, path+"/statBgpInst.txt", true);
         WriteToFile(statOspfInst, path+"/statOspfInst.txt", true);
         WriteToFile(statBgpPeering, path+"/statBgpPeering.txt", true);
         WriteToFile(statOspfPeering, path+"/statOspfPeering.txt", true);
      }
      private String RecordToColumn(String prefix, List<String[]> records){
         String out="";
         for(String[] r: records){
            out+=prefix;
            for(int i=0; i<r.length;i++){
               out+="\t"+r[i];
            }
            out+="\n";
         }
         return out;
      }
      private String SetToString(Set<String> values){
         String out="";
         if(values.size()==0) return "NA";
         for(String v: values){
            if(out.equals("")){
               out=v;
            }
            else{
               out+=","+v;
            }
         }
         return out;
      }
      
      private void Process() {
         // count intra ref
         AggregateIntraRef();
         // count mstp
         AggregateMstpInst();
         // cout stamp ospf inst
         AggregateOspfInst();
         // count stamp bgp inst
         AggregateBgpInst();
         // check ospf peering
         OspfPeering();
         // check ibgp/ebgp, check internal/external   static -> ifaceIp or dynamic (internal); otherwise external
         BgpPeering();    
         // format output
         FormatOutput();         
      }
      // <device, dstDevice, local-as, neighborIp, remote-as, internal, ibgp>
      List<String[]> neighbors = new ArrayList<String[]>();
      double bgpInternalIbgp = 0;
      double bgpInternalEbgp = 0;
      double bgpExternalIbgp = 0;
      double bgpExternalEbgp = 0;
      double bgpTotal = 0;
      double bgpInternal = 0;
      double bgpExternal = 0;
      private void BgpPeering() {
         // aggregate iface Ips
         Map<String, List<String>> ipOfStamp = new HashMap<String, List<String>>();
         List<String[]> s_ifaceIp = stamp_ifaceIp.get(stamp);
         RecordsToKeyValue(ipOfStamp, s_ifaceIp, 5, 1, 5, "null", false);
         // check instances
         List<String[]> s_bgpNeighbor = stamp_bgpNeighbors.get(stamp);
         if(s_bgpNeighbor==null) return ;
         for(String[] n: s_bgpNeighbor){
            // filter dynamic bgp
            String neighborMask = n[6];
            if(!neighborMask.equals("255.255.255.255"))
               continue;
            String device = n[1];
            String localAs = n[4];
            String ip = n[5];
            String remoteAs = n[7];
            String internal = ipOfStamp.containsKey(ip)?"1":"0";
            String ibgp = localAs.equals(remoteAs)?"1":"0";
            String dstDevice;
            if(internal.equals("1")){
               dstDevice=ipOfStamp.get(ip).get(0);
            }
            else{
               dstDevice = "NA";
            }
            neighbors.add(new String[]{device, dstDevice, localAs, ip, remoteAs, internal, ibgp});
         }
         // count
         for(String[] n: neighbors){
            bgpTotal+=1;
            String internal = n[5];
            String ibgp = n[6];
            if(internal.equals("1")){
               if(ibgp.equals("1")) bgpInternalIbgp+=1;
               else bgpInternalEbgp+=1;
            }
            else{
               if(ibgp.equals("1")) bgpExternalIbgp +=1;
               else bgpExternalEbgp+=1;
            }
         }
         bgpInternal = (bgpInternalIbgp+bgpInternalEbgp)/2;
         bgpExternal = bgpExternalIbgp+bgpExternalEbgp;
      }
      
      Map<String, List<String[]>> ospfKey_instances = new HashMap<String, List<String[]>>();
      // key: <ospfProc, ifaceSubnet, ifaceMask>
      // instance: <device, ifaceName, ospfProc, ifaceIp, ifaceMask, ospfNetworkIp, ospfNetworkMask>
      int ospfInternal = 0;
      int ospfExternal = 0;
      int ospfTotal = 0;
      private void OspfPeering() {
         // aggegrate device ip
         Map<String, List<String[]>> device_ifaceIp = new HashMap<String, List<String[]>>();
         List<String[]> s_ifaceIp = stamp_ifaceIp.get(stamp);
         RecordByKey(device_ifaceIp, s_ifaceIp, 1);
         // aggegrate device networks
         List<String[]> s_ospfNetworks = stamp_ospfNetworks.get(stamp);
         Map<String, List<String[]>> device_networks = new HashMap<String, List<String[]>>();
         RecordByKey(device_networks, s_ospfNetworks, 1);
         // check and generate instances
         for(String device: device_networks.keySet()){
            List<String[]> networks = device_networks.get(device);
            List<String[]> ifaceIps = device_ifaceIp.get(device);
            if(ifaceIps==null) continue;
            for(String[] n: networks){
               for(String[] i: ifaceIps){
                  String ifacename = i[4];
                  String ifaceIp = i[5];
                  String ifaceMask = i[6];
                  String ospfNetworkIp = n[5];
                  String ospfNetworkMask = n[6];
                  String ospfProc = n[4];
                  long networkIp_l = Util.ipToLong(ospfNetworkIp);
                  long networkMask_l = Util.ipToLong(ospfNetworkMask);
                  long ifaceIp_l = Util.ipToLong(ifaceIp);
                  if( (ifaceIp_l & networkMask_l)!=(networkIp_l & networkMask_l) )
                     continue;
                  long ifaceMask_l = Util.ipToLong(ifaceMask);
                  long ifaceSubnet_l = ifaceMask_l & ifaceIp_l;
                  String ifaceSubnet = Util.longToIp(ifaceSubnet_l);
                  String key = ospfProc+"/"+ifaceSubnet+"/"+ifaceMask;
                  String[] instance = new String[]{device, ifacename, ospfProc, 
                        ifaceIp, ifaceMask, ospfNetworkIp, ospfNetworkMask};
                  List<String[]> instances;
                  if(ospfKey_instances.containsKey(key)){
                     instances = ospfKey_instances.get(key);
                  }
                  else{
                     instances = new ArrayList<String[]>();
                  }
                  instances.add(instance);
                  ospfKey_instances.put(key, instances);
               }
            }
         }
         // check
         for(List<String[]> instance: ospfKey_instances.values()){
            ospfTotal+=instance.size();
            if(instance.size()==1)
               ospfExternal++;
            else
               ospfInternal++;
         }
      }
      private void RecordByKey(Map<String, List<String[]>> map, List<String[]> records, int keyIndex){
         if(records==null) return ;
         for(String[] r: records){
            String key = r[keyIndex];
            List<String[]> value;
            if(map.containsKey(key)){
               value = map.get(key);
            }
            else{
               value = new ArrayList<String[]>();
            }
            value.add(r);
            map.put(key, value);
         }
      }
      int numDevice = 0;
      Set<String> bgpInst = new HashSet<String>();
      private void AggregateBgpInst() {
         List<String[]> s_bgpAs = stamp_bgpAs.get(stamp);
         numDevice = s_bgpAs.size();
         RecordsToValues(bgpInst, s_bgpAs, 4, "NA");
      }
      Set<String> ospfInst = new HashSet<String>();
      private void AggregateOspfInst() {
         List<String[]> s_ospfProc = stamp_ospfProc.get(stamp);
         RecordsToValues(ospfInst, s_ospfProc, 4, "NA");
      }
      Set<String> mstpInst = new HashSet<String>();
      private void AggregateMstpInst() {
         List<String[]> s_mstp = stamp_mstp.get(stamp);
         RecordsToValues(mstpInst, s_mstp, 5, "NA");
         mstpInst.remove("1-4096");
      }
      private void RecordsToValues(Set<String> values, List<String[]> records, int valueIndex, String filter_neg){
         if(records==null) return;
         for(String[] r: records){
            String value = r[valueIndex];
            if(value.equals(filter_neg)) continue;
            values.add(value);
         }
      }
      
      int intraRefCount = 0;
      private void AggregateIntraRef() {
         // source data
         List<String[]> s_acl = stamp_acl.get(stamp);
         List<String[]> s_routemap= stamp_routemap.get(stamp);
         List<String[]> s_intraRef = stamp_intraRef.get(stamp);
         // target data
         Map<String, Set<String>> device_acls = new HashMap<String, Set<String>>();
         Map<String, Set<String>> device_routemap = new HashMap<String, Set<String>>();
         Map<String, List<String>> device_dst_acls= new HashMap<String, List<String>>();
         Map<String, List<String>> device_dst_routemap = new HashMap<String, List<String>>();
         
         RecordsToKeyValue(device_acls, s_acl, 1, 5);
         RecordsToKeyValue(device_routemap, s_routemap, 1, 4);
         RecordsToKeyValue(device_dst_acls, s_intraRef, 1, 7, 6, "acl", true);
         RecordsToKeyValue(device_dst_routemap, s_intraRef, 1, 7, 6, "routemap", true);
         
         intraRefCount += CountRef(device_acls, device_dst_acls);
         intraRefCount += CountRef(device_routemap, device_dst_routemap);
         
      }
      private int CountRef(Map<String, Set<String>> source, Map<String, List<String>> dst){
         int c = 0;
         for(String device: dst.keySet()){
            List<String>refered = dst.get(device);
            Set<String> src = source.get(device);
            if(src==null) {
               continue;
            }
            for(String r: refered){
               if(src.contains(r)) c++;
            }
         }
         return c;
      }

      private void RecordsToKeyValue(Map<String, List<String>> map, List<String[]> records, int keyIndex, int valueIndex, int filterIndex, String filterValue, boolean eq){
         if(records==null) return;
         for(String[] r: records){
            String key = r[keyIndex];
            String value = r[valueIndex];
            String filter = r[filterIndex];
            if(eq){
               if(!filter.equals(filterValue))  
                  continue;
            }
            else{
               if(filter.equals(filterValue)){
                  continue;
               }
            }
            List<String> values;
            if(map.containsKey(key)){
               values = map.get(key);
            }
            else{
               values = new ArrayList<String>();
            }
            values.add(value);
            map.put(key, values);
         }
      }
      
      private void RecordsToKeyValue(Map<String, Set<String>> map, List<String[]> records, int keyIndex, int valueIndex){
         if(records==null) return;
         for(String[] r: records){
            String key = r[keyIndex];
            String value = r[valueIndex];
            Set<String> values;
            if(map.containsKey(key)){
               values = map.get(key);
            }
            else{
               values = new HashSet<String>();
            }
            values.add(value);
            map.put(key, values);
         }
      }
   
   }


   // stat
   // statMstp
   // statBgpInst
   // statOspfInst
   // statBgpPeering
   // statOspfPeering
   private void initFile(){
      String blank = "";
      WriteToFile(blank, path+"/stat.txt", false);
      WriteToFile(blank, path+"/statMstp.txt", false);
      WriteToFile(blank, path+"/statBgpInst.txt", false);
      WriteToFile(blank, path+"/statOspfInst.txt", false);
      WriteToFile(blank, path+"/statBgpPeering.txt", false);
      WriteToFile(blank, path+"/statOspfPeering.txt", false);
   }
   
   String path;
   String devicelist;
   public Postprocessor(String p, String dl){
      inputLock = new ReentrantLock();
      outputLock = new ReentrantLock();
      
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
   public void Process() throws InterruptedException{
      startTime = System.nanoTime();
      // read file
      ReadFiles();
      System.out.println("read files done, time(s): "+(System.nanoTime()-startTime)/1000000000);
      startTime = System.nanoTime();
      // prepare queue    
      CreateQueue();
      // clean file
      initFile();
      
      // start
      processThread p[] =new processThread[numThreads];
      for(int i=0;i<numThreads;i++){
         p[i] = new processThread();
      }
      Thread t[] = new Thread[numThreads];
      for(int i =0; i< numThreads; i++){
         t[i] =new Thread(p[i], "thread"+i);
      }
      for(int i = 0; i< numThreads; i++){
         t[i].start();
      }
      for(int i = 0; i< numThreads; i++){
         t[i].join();
      }
      
   }
   
   Map<String, List<String[]>> stamp_ifaceIp = new HashMap<String, List<String[]>>();
   Map<String, List<String[]>> stamp_ospfNetworks = new HashMap<String, List<String[]>>();
   Map<String, List<String[]>> stamp_bgpNeighbors = new HashMap<String, List<String[]>>();
   
   Map<String, List<String[]>> stamp_bgpAs = new HashMap<String, List<String[]>>();
   Map<String, List<String[]>> stamp_ospfProc = new HashMap<String, List<String[]>>();
   
   Map<String, List<String[]>> stamp_acl = new HashMap<String, List<String[]>>();
   Map<String, List<String[]>> stamp_routemap = new HashMap<String, List<String[]>>();
   Map<String, List<String[]>> stamp_intraRef = new HashMap<String, List<String[]>>();
   
   Map<String, List<String[]>> stamp_mstp = new HashMap<String, List<String[]>>();
   
   private void DumpFileToMapList(String file, Map<String, List<String[]>> mapList){
      try {
         BufferedReader br = new BufferedReader(new FileReader(file));  
         String line = null;  
         while ((line = br.readLine()) != null)  {
            line=line.trim();
            String[] fields = line.split("\t");
            String stamp = fields[0];
            List<String[]> l; 
            if(mapList.containsKey(stamp)){
               l = mapList.get(stamp);
            }
            else{
               l = new ArrayList<String[]>();
            }
            l.add(fields);
            mapList.put(stamp, l);
         }
         br.close();
      } catch (IOException e1) {
         System.out.println("file: "+file);
         e1.printStackTrace();
      }
   }
   
   private void ReadFiles(){
      // acl
      String acl = path+"/acl.txt";
      DumpFileToMapList(acl, stamp_acl);
      // bgpAs
      String bgpAs = path+"/bgpAs.txt";
      DumpFileToMapList(bgpAs, stamp_bgpAs);
      // bgpNeighbor
      String bgpNeighborAs = path+"/bgpNeighborAs.txt";
      DumpFileToMapList(bgpNeighborAs, stamp_bgpNeighbors);
      // iface Ip
      String ifaceIp = path+"/ifaceIp.txt";
      DumpFileToMapList(ifaceIp, stamp_ifaceIp);
      // intraRef
      String intraReference = path+"/intraReference.txt";
      DumpFileToMapList(intraReference, stamp_intraRef);
      // ospf network
      String ospfNetwork = path+"/ospfNetwork.txt";
      DumpFileToMapList(ospfNetwork, stamp_ospfNetworks);
      // osfp proc
      String ospfProc = path+"/ospfProc.txt";
      DumpFileToMapList(ospfProc, stamp_ospfProc);
      // routemap
      String routemap = path+"/routemap.txt";
      DumpFileToMapList(routemap, stamp_routemap);
      // mstp
      String mstpInstance = path+"/mstpInstance.txt";
      DumpFileToMapList(mstpInstance, stamp_mstp);
   }
   
   Queue<String> stampsQueue = new LinkedList<String>();
   private void CreateQueue(){
      stampsQueue.addAll(stamp_bgpAs.keySet());
   }
   // stamp, numDevice, mstpInst, ospfInst, ospfPeering, avgOspfSize, 
      // bgpInst, bgpPeering, avgBgpSize 
      // ospfInternalPeering, ospfExternalPeering,
      // bgpInternalPeering, bgpExternalPeering,
      // ibgpInternal, ebgpInternal
      // ibgpExternal, ibgpExternal
   
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

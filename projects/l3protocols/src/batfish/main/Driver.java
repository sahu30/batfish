package batfish.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import batfish.main.Preprocessor;

public class Driver {
   boolean debug = false;
   int fileIndex = 145;
   boolean test = false;
   
   int numThreads = 5;
   int count=0;
   
   private class batfishProcess implements Runnable{
      Lock inputLock, outputLock;
      Queue<String> files;
      String outputPath;
      String srcRoot;
      String name;
      long startTime;
      public batfishProcess(Lock i, Lock o, Queue<String> f, String outp, String srcR, String n, long st){
         inputLock = i;
         outputLock = o;
         files = f;
         outputPath = outp;
         srcRoot = srcR;
         name = n;
         startTime = st;
      }
      
      int countInRound;
      int localCount = 0;
      int REPORTSTEP = 100;
      int PROCESSSTEP = 10;
      // stat
      String stat ="";
      String warning = "";
      String failure = "";
      // ospf
      String ifaceIp = "";
      String ospfNetworkArea= "";
      String ospfPassiveIface = "";
      String ospfPassiveIfaceDefault= "";
      // bgp
      String bgpRule = "";
      String bgpNeighborAs = "";
      String bgpGroupAs = "";
      String bgpNeighborGroup = "";
      String bgpNetworks = "";
      String bgpNeighborTemplate = "";
      String bgpAddressFamily = "";
      String bgpTemplate = "";
      String bgpVrf = "";     

      Queue<String> lines = new LinkedList<String>();
      @Override
      public void run() {
         init();
         while(true){
            // input
            boolean hasData = input();
            if(!hasData) break;
            // process
            process();
            // output
            output(false);
         }
         output(true);
      }
      
      private void init(){// clean files
         // total
         WriteToFile(stat, outputPath+"/stat.txt", false);
         WriteToFile(warning, outputPath+"/warnings.txt", false);
         WriteToFile(failure, outputPath+"/failures.txt", false);
         // ospf
         WriteToFile(ifaceIp, outputPath+"/ifaceIp.txt", false);
         WriteToFile(ospfNetworkArea, outputPath+"/ospfNetworkArea.txt", false);
         WriteToFile(ospfPassiveIface, outputPath+"/ospfPassiveIface.txt", false);
         WriteToFile(ospfPassiveIfaceDefault, outputPath+"/ospfPassiveIfaceDefault", false);
         // bgp
         WriteToFile(bgpRule, outputPath+"/bgpRule.txt", false);
         WriteToFile(bgpNeighborAs, outputPath+"/bgpNeighborAs.txt", false);
         WriteToFile(bgpGroupAs, outputPath+"/bgpGroupAs.txt", false);
         WriteToFile(bgpNeighborGroup, outputPath+"/bgpNeighborGroup.txt", false);
         WriteToFile(bgpNetworks, outputPath+"/bgpNetworks.txt", false);
         WriteToFile(bgpNeighborTemplate, outputPath+"/bgpNeighborTemplate.txt", false);
         WriteToFile(bgpAddressFamily, outputPath+"/bgpAddressFamily.txt", false);
         WriteToFile(bgpTemplate, outputPath+"/bgpTemplate.txt", false);
         WriteToFile(bgpVrf, outputPath+"/bgpVrf.txt", false);         
      }
      private boolean input(){
         inputLock.lock();
         countInRound = 0;
         while(!files.isEmpty()){
            String file = files.remove();
            countInRound++;
            lines.add(file);
            if(countInRound>=PROCESSSTEP) break;
         }
         inputLock.unlock();
         if(countInRound==0) return false;
         else return true;
      }
      

      Preprocessor prep = new Preprocessor();
      private void process(){
         while(!lines.isEmpty()){
            String line = lines.remove();
            localCount++;
            if(debug){
               if(localCount<fileIndex)
                  continue;
               else if(localCount>fileIndex)
                  break;
            }
            
            
            String[] fields = line.split("\t");
            if(fields.length!=4){
               failure += line+"\n";
               continue;
            }
            String stamp = fields[0];
            String device = fields[1];
            String config = fields[2];
            String vendor = fields[3];
            String file = srcRoot+'/'+stamp+'/'+device+'/'+config;
            String content;
            try {
               content = prep.Process(vendor, file);
            } catch (IOException e) {
               failure += line+"\n";
               e.printStackTrace();
               continue;
            }
            Batfish b = new Batfish(vendor, content);
            boolean success = b.parseVendorConfigurations();
            if(success){
               String prefix = line+'\t';
               // ospf
               ifaceIp += b.OutputIfaceIp(prefix);
               ospfNetworkArea += b.OutputOspfNetworkArea(prefix);
               ospfPassiveIface += b.OutputOspfPassiveIface(prefix);
               ospfPassiveIfaceDefault += b.OutputOspfPassiveIfaceDefault(prefix);
               // bgp
               bgpRule += b.OutputRuleList(prefix);
               bgpNeighborAs += b.OutputNeighborAs(prefix);
               bgpGroupAs+= b.OutputGroupAs(prefix);
               bgpNeighborGroup += b.OutputGroupNeighbor(prefix);
               bgpNetworks += b.OutputNetworks(prefix);
               bgpNeighborTemplate += b.OutputNeighborTemplate(prefix);
               bgpAddressFamily += b.OutputAddressFamily(prefix);
               bgpTemplate += b.OutputTemplateRemoteAs(prefix);
               bgpVrf += b.OutputVrf(prefix); 
               // total
               stat += b.OutputStat(prefix);
               warning += b.OutputWarning(prefix);
            }
            else{
               System.out.println("failure, file index: "+localCount);
               failure += line+"\n";
            }
            if(debug){
               WriteToFile(content, outputPath+"/debug.cfg", false);
            }
         }
      }
      private void output(boolean last){
         outputLock.lock();
         count+=countInRound;
    //     localCount+=countInRound;
         if(last || count % REPORTSTEP==0){
            // total
            WriteToFile(stat, outputPath+"/stat.txt", true);
            WriteToFile(warning, outputPath+"/warnings.txt", true);
            WriteToFile(failure, outputPath+"/failures.txt", true);
            // ospf
            WriteToFile(ifaceIp, outputPath+"/ifaceIp.txt", true);
            WriteToFile(ospfNetworkArea, outputPath+"/ospfNetworkArea.txt", true);
            WriteToFile(ospfPassiveIface, outputPath+"/ospfPassiveIface.txt", true);
            WriteToFile(ospfPassiveIfaceDefault, outputPath+"/ospfPassiveIfaceDefault.txt", true);
            // bgp
            WriteToFile(bgpRule, outputPath+"/bgpRule.txt", true);
            WriteToFile(bgpNeighborAs, outputPath+"/bgpNeighborAs.txt", true);
            WriteToFile(bgpGroupAs, outputPath+"/bgpGroupAs.txt", true);
            WriteToFile(bgpNeighborGroup, outputPath+"/bgpNeighborGroup.txt", true);
            WriteToFile(bgpNetworks, outputPath+"/bgpNetworks.txt", true);
            WriteToFile(bgpNeighborTemplate, outputPath+"/bgpNeighborTemplate.txt", true);
            WriteToFile(bgpAddressFamily, outputPath+"/bgpAddressFamily.txt", true);
            WriteToFile(bgpTemplate, outputPath+"/bgpTemplate.txt", true);
            WriteToFile(bgpVrf, outputPath+"/bgpVrf.txt", true);

            
            
            long endTime = System.nanoTime();    
            long elapsed = (endTime - startTime)/1000000000; // in second
            System.out.println(count+" file processed in "+elapsed+" second, report from "+name+" localCount is "+localCount);
           // total
            // stat
            stat ="";
            warning = "";
            failure = "";
            // ospf
            ifaceIp = "";
            ospfNetworkArea= "";
            ospfPassiveIface = "";
            ospfPassiveIfaceDefault= "";
            // bgp
            bgpRule = "";
            bgpNeighborAs = "";
            bgpGroupAs = "";
            bgpNeighborGroup = "";
            bgpNetworks = "";
            bgpNeighborTemplate = "";
            bgpAddressFamily = "";
            bgpTemplate = "";
            bgpVrf = "";  
         }
         outputLock.unlock();
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
   
   // arguemnts: <file list> <root path> <output path>
   // file list format :  stamp, device, config, vendor
	public static void main(String []args) throws Exception{
	   Driver d = new Driver();
	   d.Start(args);
	}
   public void Start(String []args) throws Exception{
	   long startTime = System.nanoTime();    
		if(args.length!=3){
		   System.out.println("Error intput argument number: "+args.length);
			System.out.println("Usage: l3protocols <config_list file> <src root> <output path>");
			System.out.println("list_file format: stamp device config vendor");
			System.exit(1);
		}
      Lock inputLock = new ReentrantLock();
      Lock outputLock = new ReentrantLock();
      Queue<String> files = new LinkedList<String>();
      String outputPath = args[2];
      String srcRoot = args[1];      
      
		// prepare file_list
		String fileList = args[0];	
		if(test){
		   fileList = "testcase/test.txt";
		}
		BufferedReader br = new BufferedReader(new FileReader(fileList));  
		String line = null;  
		while ((line = br.readLine()) != null)  
		   files.add(line);
		if(debug){
		   numThreads = 1;
		}
		
		batfishProcess p[] = new batfishProcess[numThreads];
		for(int i = 0; i<numThreads; i++){
		   p[i] = new batfishProcess(inputLock, outputLock, files, outputPath, srcRoot, "thread"+i, startTime);
		}
		Thread t[] = new Thread[numThreads];
		for(int i = 0; i<numThreads; i++){
		   t[i] = new Thread(p[i], "thread"+i);
		}
		for(int i = 0; i< numThreads; i++){
		   t[i].start();
		}
		for(int i = 0; i< numThreads; i++){
		   t[i].join();
		}
	}
}

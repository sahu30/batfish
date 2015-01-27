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
   int fileIndex = 14;
   boolean test = false;
   boolean RefOnly = false;
   
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
      String warning = "";
      String failure = "";
      // acl
      String acl = "";
      // Routemap
      String routemap = "";
      // iface
      String ifaceIp = "";
      // bgp
      String bgpAs = "";
      String bgpNeighborAs = "";
      // ospf
      String ospfProc = "";
      String ospfNetwork = "";
      // mstp
      String mstpInstance = "";
      // reference
      String intraReference = "";
      
      Queue<String> lines = new LinkedList<String>();
      @Override
      public void run() {
//         init();
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
            //System.out.println(localCount);
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

               // stat
               warning += b.OutputWarning(prefix);
               // acl
               acl += b.OutputAcl(prefix);
               // Routemap
               routemap += b.OutputRoutemap(prefix);
               // iface
               ifaceIp += b.OutputIfaceIp(prefix);
               // bgp
               bgpAs += b.OutputBgpAs(prefix);
               bgpNeighborAs += b.OutputBgpNeighborAs(prefix);
               // ospf
               ospfProc += b.OutputOspfProc(prefix);
               ospfNetwork += b.OutputOspfNeitworks(prefix);
               // mspt
               mstpInstance +=b.OutputMstpInstance(prefix);
               // reference
               intraReference += b.OutputIntraReference(prefix);
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
         if(last || count % REPORTSTEP==0){         // total
            WriteToFile(warning, outputPath+"/warnings.txt", true);
            WriteToFile(failure, outputPath+"/failures.txt", true);
            // acl
            WriteToFile(acl, outputPath+"/acl.txt", true);
            // routemap
            WriteToFile(routemap, outputPath+"/routemap.txt", true);
            // iface
            WriteToFile(ifaceIp, outputPath+"/ifaceIp.txt", true);
            // bgp
            WriteToFile(bgpAs, outputPath+"/bgpAs.txt", true);
            WriteToFile(bgpNeighborAs, outputPath+"/bgpNeighborAs.txt", true);
            // ospf
            WriteToFile(ospfProc, outputPath+"/ospfProc.txt", true);
            WriteToFile(ospfNetwork, outputPath+"/ospfNetwork.txt", true);
            // mstp
            WriteToFile(mstpInstance, outputPath+"/mstpInstance.txt", true);
            // itnraReference
            WriteToFile(intraReference, outputPath+"/intraReference.txt", true);
            
      //      System.out.print(routemap);
            
            long endTime = System.nanoTime();    
            long elapsed = (endTime - startTime)/1000000000; // in second
            System.out.println(count+" file processed in "+elapsed+" second, report from "+name+" localCount is "+localCount);
            // total      // stat
            warning = "";
            failure = "";
            // acl
            acl = "";
            // Routemap
            routemap = "";
            // iface
            ifaceIp = "";
            // bgp
            bgpAs = "";
            bgpNeighborAs = "";
            // ospf
            ospfProc = "";
            ospfNetwork = "";
            // mstp
            mstpInstance = "";
            // reference
            intraReference = "";
         }
         outputLock.unlock();
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
			System.out.println("Usage: reference <config_list file> <src root> <output path>");
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
		
		if(RefOnly){
		   PostProcess(outputPath, fileList);
		   return ;
		}
		
		BufferedReader br = new BufferedReader(new FileReader(fileList));  
		String line = null;  
		while ((line = br.readLine()) != null)  
		   files.add(line);
		if(debug){
		   numThreads = 1;
		}
		
		// prepare output
		init(outputPath);
		// process
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
		// post process
		PostProcess(outputPath,fileList);

	}

   private void PostProcess(String outputPath, String fileList){
      long startTime = System.nanoTime();    
      System.out.println("start to count references");
      Postprocessor post = new Postprocessor(outputPath, fileList);
      post.Process();
      System.out.println("done");
      System.out.println("time(s): "+ (System.nanoTime()-startTime)/1000000000);
   }
   
   private void init(String outputPath){// clean files
      String blank = "";
      // total
      WriteToFile(blank, outputPath+"/warnings.txt", false);
      WriteToFile(blank, outputPath+"/failures.txt", false);
      // acl
      WriteToFile(blank, outputPath+"/acl.txt", false);
      // routemap
      WriteToFile(blank, outputPath+"/routemap.txt", false);
      // iface
      WriteToFile(blank, outputPath+"/ifaceIp.txt", false);
      // bgp
      WriteToFile(blank, outputPath+"/bgpAs.txt", false);
      WriteToFile(blank, outputPath+"/bgpNeighborAs.txt", false);
      // ospf
      WriteToFile(blank, outputPath+"/ospfProc.txt", false);
      WriteToFile(blank, outputPath+"/ospfNetwork.txt", false);
      // mstp
      WriteToFile(blank, outputPath+"/mstpInstance.txt", false);
      // itnraReference
      WriteToFile(blank, outputPath+"/intraReference.txt", false);
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

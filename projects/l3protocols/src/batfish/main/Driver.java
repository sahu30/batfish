package batfish.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import batfish.main.Preprocessor;

public class Driver {
   
   

   
   private class thread implements Runnable{
      Lock inputLock, outputLock;
      Queue<String> files;
      Integer count;
      
      public thread(Lock i, Lock o, Queue<String> f, Integer c){
         inputLock = i;
         outputLock = o;
         files = f;
         count = c;
      }
      
      private void init(){}
      private boolean input(){
         return false;
      }
      private void process(){}
      private void output(){}
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
            output();
         }
         output();
      }
   }
   
   // arguemnts: <file list> <root path> <output path>
   // file list format :  stamp, device, config, vendor
	public static void main(String []args) throws Exception{
	   boolean test = false;
	   
	   boolean debug = false;
	   int debugFileIndex = 63;
	   
	   long startTime = System.nanoTime();    
		if(args.length!=3){
		   System.out.println("Error intput argument number: "+args.length);
			System.out.println("Usage: l3protocols <config_list file> <src root> <output path>");
			System.out.println("list_file format: stamp device config vendor");
			System.exit(1);
		}
		
		int count = 0;
		int STEP = 100;
		
		String fileList = args[0];
		String srcRoot = args[1];
		String outputPath = args[2];		
		if(test){
		   fileList = "testcase/test.txt";
		}		
		Preprocessor prep = new Preprocessor();
		
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
		
		// clean files
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
      
		BufferedReader br = new BufferedReader(new FileReader(fileList));  
		String line = null;  
		while ((line = br.readLine()) != null)  
		{
         count++;
  //       System.out.println(count);
         if(debug){
            if(count!=debugFileIndex)
               continue;
         }
         
		   
		   String[] fields = line.split("\t");
		   if(fields.length!=4)
		      continue;
		   String stamp = fields[0];
		   String device = fields[1];
		   String config = fields[2];
		   String vendor = fields[3];
		   String file = srcRoot+'/'+stamp+'/'+device+'/'+config;
		   String content = prep.Process(vendor, file);
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
	         System.out.println("failure, file index: "+count);
	         failure += line+"\n";
	      }

	      if(debug){
	         WriteToFile(content, outputPath+"/debug.cfg", false);
	         System.exit(0);
	      }
	      
	      
	      if(count%STEP==0){
	         // total
	         WriteToFile(stat, outputPath+"/stat.txt", true);
	         WriteToFile(warning, outputPath+"/warnings.txt", true);
	         WriteToFile(failure, outputPath+"/failures.txt", true);
	         // ospf
	         WriteToFile(ifaceIp, outputPath+"/ifaceIp.txt", true);
	         WriteToFile(ospfNetworkArea, outputPath+"/ospfNetworkArea.txt", true);
	         WriteToFile(ospfPassiveIface, outputPath+"/ospfPassiveIface.txt", true);
	         WriteToFile(ospfPassiveIfaceDefault, outputPath+"/ospfPassiveIfaceDefault", true);
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
	         
	         long estimatedTime = System.nanoTime() - startTime;
	         System.out.println(count+" files processed, time elapsed(s): "+estimatedTime/1000000000);
	      }
		} 

      // total
      WriteToFile(stat, outputPath+"/stat.txt", true);
      WriteToFile(warning, outputPath+"/warnings.txt", true);
      WriteToFile(failure, outputPath+"/failures.txt", true);
      // ospf
      WriteToFile(ifaceIp, outputPath+"/ifaceIp.txt", true);
      WriteToFile(ospfNetworkArea, outputPath+"/ospfNetworkArea.txt", true);
      WriteToFile(ospfPassiveIface, outputPath+"/ospfPassiveIface.txt", true);
      WriteToFile(ospfPassiveIfaceDefault, outputPath+"/ospfPassiveIfaceDefault", true);
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
      
	   long estimatedTime = System.nanoTime() - startTime;
	   System.out.println("finally, "+count+" files processed,time elapsed(s): "+estimatedTime/1000000000);
	}

   private static void WriteToFile(String content, String file, boolean append){
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

package batfish.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import batfish.main.Preprocessor;

public class Driver {

   
   // arguemnts: <file list> <root path> <output path>
   // file list format :  stamp, device, config, vendor
	public static void main(String []args) throws Exception{
	   
	   boolean debug = false;
	   int debugFileIndex = 33;
	   
	   
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
		Preprocessor prep = new Preprocessor();
		
		String stat ="";
		String ifaceIp = "";
		String ospfNetworkArea= "";
		String ospfPassiveIface = "";
		String ospfPassiveIfaceDefault= "";
		String warning = "";
      String failure = "";
		
		// clean files
      WriteToFile(stat, outputPath+"/stat.txt", false);
      WriteToFile(ifaceIp, outputPath+"/ifaceIp.txt", false);
      WriteToFile(ospfNetworkArea, outputPath+"/ospfNetworkArea.txt", false);
      WriteToFile(ospfPassiveIface, outputPath+"/ospfPassiveIface.txt", false);
      WriteToFile(ospfPassiveIfaceDefault, outputPath+"/ospfPassiveIfaceDefault", false);
      WriteToFile(warning, outputPath+"/warnings.txt", false);
      WriteToFile(failure, outputPath+"/failures.txt", false);
		
		BufferedReader br = new BufferedReader(new FileReader(fileList));  
		String line = null;  
		while ((line = br.readLine()) != null)  
		{
         count++;
      //   System.out.println(count);
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
	         stat += b.OutputStat(prefix);
	         ifaceIp += b.OutputIfaceIp(prefix);
	         ospfNetworkArea += b.OutputOspfNetworkArea(prefix);
	         ospfPassiveIface += b.OutputOspfPassiveIface(prefix);
	         ospfPassiveIfaceDefault += b.OutputOspfPassiveIfaceDefault(prefix);
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
	         WriteToFile(stat, outputPath+"/stat.txt", true);
	         WriteToFile(ifaceIp, outputPath+"/ifaceIp.txt", true);
	         WriteToFile(ospfNetworkArea, outputPath+"/ospfNetworkArea.txt", true);
	         WriteToFile(ospfPassiveIface, outputPath+"/ospfPassiveIface.txt", true);
	         WriteToFile(ospfPassiveIfaceDefault, outputPath+"/ospfPassiveIfaceDefault", true);
	         WriteToFile(warning, outputPath+"/warnings.txt", true);
            WriteToFile(failure, outputPath+"/failures.txt", true);
	         stat ="";
	         ifaceIp = "";
	         ospfNetworkArea= "";
	         ospfPassiveIface = "";
	         ospfPassiveIfaceDefault = "";
	         warning = "";
            failure = "";
	         
	         long estimatedTime = System.nanoTime() - startTime;
	         System.out.println(count+" files processed, time elapsed(ms): "+estimatedTime/1000000);
	      }
		} 

      WriteToFile(stat, outputPath+"/stat.txt", true);
      WriteToFile(ifaceIp, outputPath+"/ifaceIp.txt", true);
      WriteToFile(ospfNetworkArea, outputPath+"/ospfNetworkArea.txt", true);
      WriteToFile(ospfPassiveIface, outputPath+"/ospfPassiveIface.txt", true);
      WriteToFile(ospfPassiveIfaceDefault, outputPath+"/ospfPassiveIfaceDefault", true);
      WriteToFile(warning, outputPath+"/warnings.txt", true);
      WriteToFile(failure, outputPath+"/failures.txt", true);
      
	   long estimatedTime = System.nanoTime() - startTime;
	   System.out.println("finally, "+count+" files processed,time elapsed(ms): "+estimatedTime/1000000);
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

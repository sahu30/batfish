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
	   
	   long startTime = System.nanoTime();    
		if(args.length!=3){
		   System.out.println("Error intput argument number: "+args.length);
			System.out.println("Usage: l2protocols <config_list file> <src root> <output path>");
			System.out.println("list_file format: stamp device config vendor");
			System.exit(1);
		}
		
		int count = 0;
		int STEP = 100;
		
		String fileList = args[0];
		String srcRoot = args[1];
		String outputPath = args[2];
		Preprocessor prep = new Preprocessor();
		
		String vlanInstanceCount = "";
		String vlanUsed = "";
		String vlanIfaceRanges = "";
		String failure = "";
		
		// clean files
      WriteToFile(vlanInstanceCount, outputPath+"/vlanInstanceCount.txt", false);
      WriteToFile(vlanUsed, outputPath+"/vlanUsed.txt", false);
      WriteToFile(vlanIfaceRanges, outputPath+"/IfaceVlanRanges.txt", false);
      WriteToFile(failure, outputPath+"/failures.txt", false);
		
		BufferedReader br = new BufferedReader(new FileReader(fileList));  
		String line = null;  
		while ((line = br.readLine()) != null)  
		{  
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
	         vlanInstanceCount += b.OutputVlanInstanceCount(line+"\t");
	         vlanUsed += b.OutputVlans(line+"\t");
	         vlanIfaceRanges += b.OutputIfaceRanges(line+"\t");
	      }
	      else{
	         failure += line+"\n";
	      }

	      count++;
	      if(count%STEP==0){

	         WriteToFile(vlanInstanceCount, outputPath+"/vlanInstanceCount.txt", true);
	         WriteToFile(vlanUsed, outputPath+"/vlanUsed.txt", true);
	         WriteToFile(vlanIfaceRanges, outputPath+"/IfaceVlanRanges.txt", true);
	         WriteToFile(failure, outputPath+"/failures.txt", true);
	         
	         vlanInstanceCount = "";
	         vlanUsed = "";
	         vlanIfaceRanges = "";
	         failure = "";
	         
	         long estimatedTime = System.nanoTime() - startTime;
	         System.out.println(count+" files processed, time elapsed(ms): "+estimatedTime/1000000);
	      }
		} 


      WriteToFile(vlanInstanceCount, outputPath+"/vlanInstanceCount.txt", true);
      WriteToFile(vlanUsed, outputPath+"/vlanUsed.txt", true);
      WriteToFile(vlanIfaceRanges, outputPath+"/IfaceVlanRanges.txt", true);
      WriteToFile(failure, outputPath+"/failures.txt", true);
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

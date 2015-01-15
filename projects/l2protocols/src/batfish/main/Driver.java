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
		String l2out="";
		String linksout = "";
		String failure = "";
		
		// clean files
      WriteToFile(failure, outputPath+"/failures.txt", false);
      WriteToFile(l2out, outputPath+"/l2protocols.txt", false);
      WriteToFile(linksout, outputPath+"/links.txt", false);
		
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
	         l2out+=b.OutputL2(line+"\t");
	         linksout+=b.OutputLinks(line+"\t");
	      }
	      else{
	         failure += line+"\n";
	      }

	      count++;
	      if(count%STEP==0){
	         WriteToFile(failure, outputPath+"/failures.txt", true);
	         WriteToFile(l2out, outputPath+"/l2protocols.txt", true);
	         WriteToFile(linksout, outputPath+"/links.txt", true);
	         failure = "";
	         linksout="";
	         l2out="";
	         long estimatedTime = System.nanoTime() - startTime;
	         System.out.println(count+" files processed, time elapsed(ms): "+estimatedTime/1000000);
	      }
		} 
		
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

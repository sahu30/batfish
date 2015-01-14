package batfish.main;

public class Driver {

	public static void main(String []args) throws Exception{
		if(args.length!=1){
			System.out.println("Usage: complexity <path to configs>");
			System.exit(1);
		}
		
		Batfish b=new Batfish();
		String configPath;
		configPath = args[0];
		//path = "./";
		b.readConfigurationFiles(configPath);
		b.parseVendorConfigurations();
	}
}

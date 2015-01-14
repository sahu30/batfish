package batfish.l2;

public class protocols {
   private int hasUDLD = 0;
   private int hasMSTP = 0;
   private int hasDOT1Q = 0;
   private int hasLACP = 0;
   private int hasDHCP = 0;
   private int hasHSRP = 0;
   
   private int VLANInstance = 0;
   private int UDLDInstance = 0;
   private int MSTPInstance = 0;
   private int DOT1QInstance = 0;
   private int LACPInstance = 0;
   private int DHCPInstance = 0;
   private int HSRPInstance = 0;
   
   public void FindMST(){
      hasMSTP = 1;
   }
   
   public void FindUDLD(){
      hasUDLD = 1;
   }
   
   public void FindVLANInstance(){
      VLANInstance++;
   }
   public void FindUDLDInstance(){
      hasUDLD = 1;
      UDLDInstance ++;
   }
   public void FindMSTPInstance(){
      hasMSTP = 1;
      MSTPInstance ++;
   }
   public void FindDOT1QInstance(){
      hasDOT1Q = 1;
      DOT1QInstance++;
   }
   public void FindLACPInstance(){
      hasLACP=1;
      LACPInstance++;
   }
   public void FindDHCPInstance(){
      hasDHCP=1;
      DHCPInstance++;
   }
   public void FindHSRPInstance(){
      hasHSRP=1;
      HSRPInstance++;
   }
   @Override
   public String toString(){
      String out="";
      int protocolCount = hasUDLD+hasMSTP+hasDOT1Q
            +hasLACP+hasDHCP+hasHSRP;
      out+=protocolCount;
      out+="\t"+VLANInstance;
      out+="\t"+hasUDLD;
      out+="\t"+hasMSTP;
      out+="\t"+hasDOT1Q;
      out+="\t"+hasLACP;
      out+="\t"+hasDHCP;
      out+="\t"+hasHSRP;
      out+="\t"+UDLDInstance;
      out+="\t"+MSTPInstance;
      out+="\t"+DOT1QInstance;
      out+="\t"+LACPInstance;
      out+="\t"+DHCPInstance;
      out+="\t"+HSRPInstance;
      return out;
   }
}

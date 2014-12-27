package batfish.grammar.cisco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import batfish.representation.Ip;

public class complexityUtil {
   /************************************************************
    * Inter-file reference count
    ***********************************************************/
   // OSPF
   String currentIface = null;
   
   private Map<String, subnet> ifaceToSubnet = new HashMap<String, subnet>();
   public Set<subnet> subnetOfOspfIface = new HashSet<subnet>();
//   public Set<String> ifaceOfOspf = new HashSet<String>();
   
   public void enterOSPF(String name) {
      Assert(ospf==null, "enterOSPF with inOSPF true");
      ospf = name;
   }

   public void exitOSPF() {
      Assert(ospf!=null, "exitOSPF with inOSPF false");
   //   ospf = null;
   }

   public void enterIface(String name) {
      Assert(currentIface == null, "enterIface with currentIface not null");
      currentIface = name;
   }

   public void exitIface() {
      Assert(currentIface !=null, "exitIface with currentIface null");
      currentIface = null;
   }
   
   public void addIfaceSubnet(String prefix) {
      Assert(currentIface!=null, "addIfaceSubnet with iface null");
      subnet s = new subnet(prefix);
      ifaceToSubnet.put(currentIface, s);
   }
   
   public void addIfaceSubnet(String ip, String mask) {
      Assert(currentIface!=null, "addIfaceSubnet with iface null");
      subnet s = new subnet(ip, mask);
      ifaceToSubnet.put(currentIface, s);
   }

   public void addOSPFIface(String name) {
      Assert(ospf!=null, "addOSPFIface with ospf null");
 //     ifaceOfOspf.add(name);
      subnet sub = ifaceToSubnet.get(name);
      Assert(sub!=null, "addOSPFIface subnet of Iface is null");
      subnetOfOspfIface.add(sub);
   }

   public boolean OspfReferenced(complexityUtil other) {
      System.out.println("==== OSPF Reference ====");
      // printOSPF();
      // other.printOSPF();
      for (subnet sub : subnetOfOspfIface) {
         if (other.subnetOfOspfIface.contains(sub))
            return true;
      }
      return false;
   }
  
   // BGP
   public Set<String> ibgpNeighbors = new HashSet<String>();
   public List<String> ebgpNeighbors = new ArrayList<String>();
   public Map<String, String> templateToAs = new HashMap<String, String>();
   
   public String bgpAs=null;
   public String currentNeighbor=null;
   public String currentTemplate=null;
   String ospf=null;   

   public void enterBGP(String as) {
      Assert(bgpAs==null, "enterBGP with bgpAs not null");
      bgpAs = as;
   }

   public void exitBGP() {
      Assert(bgpAs!=null, "exitBGP with bgpAs null");
  //    bgpAs = null;
   }

   public void enterTemplate(String name) {
      Assert(bgpAs!=null, "enterTemplate with bgpAs null");
      Assert(currentTemplate==null, "enterTemplate with currentTemplate not null");
      currentTemplate = name;
   }

   public void exitTemplate() {
      Assert(bgpAs!=null, "exitTemplate with bgpAs null");
      Assert(currentTemplate!=null, "exitTemplate with currentTemplate null");
      currentTemplate = null;
   }   

   public void enterNeighbor(String nei) {
      Assert(bgpAs!=null, "enterTemplate with bgpAs null");
      Assert(currentNeighbor==null, "enterNeighbor with currentNeighbor not null");
      currentNeighbor = nei;
   }

   public void exitNeighbor() {
      Assert(bgpAs!=null, "enterTemplate with bgpAs null");
      Assert(currentNeighbor==null, "exitNeighbor with currentNeighbor null");
      currentNeighbor = null;
   }
   
   public void addTemplateAs(String as) {
      Assert(bgpAs!=null, "addTemplateAs with bgpAs null");
      Assert(currentTemplate!=null, "addTemplateAs with currentTemplate null");
      templateToAs.put(currentTemplate, as);
   }

   public void addBGPNeighbor(String neiAs, String ip) {
      Assert(bgpAs!=null, "addBGPNeighbor with bgpAs null");
      if (neiAs.equals(bgpAs)) {
         ibgpNeighbors.add(ip);
      } else {
         ebgpNeighbors.add(neiAs);
      }
   }

   public void addBGPNeighborByTemplate(String temp_name) {
      Assert(bgpAs!=null, "addBGPNeighborByTemplate with bgpAs null");
      Assert(currentNeighbor!=null, "addBGPNeighborByTemplate with currentNeighbor null");
      String as = templateToAs.get(temp_name);
      Assert(as!=null, "addBGPNeighborByTemplate without the template as");
      addBGPNeighbor(as, currentNeighbor);
   }

   public int ebgpReferences(HashSet<String> ases) {
      System.out.println("===== BGP Reference ====");
      // printBGP();
      // other.printBGP();
      int count = 0;
      for(String n: ebgpNeighbors){
         if(ases.contains(n))
            count++;
      }
      return count;
   }

   /************************************************************
    * Intra-file reference count
    ***********************************************************/
   enum stanza_type {
      IFACE, ACL, ROUTEMAP, ROUTER
   };

   Set<stanza> stanzas = new HashSet<stanza>();
   stanza current = null;

   public void enterStanza(String type) {
      Assert(current==null, "enter a new stanza without exiting the previous");
      stanza_type t = null;
      if (type.equals("router")) {
         t = stanza_type.ROUTER;
      } else if (type.equals("iface")) {
         t = stanza_type.IFACE;
      } else if (type.equals("routemap")) {
         t = stanza_type.ROUTEMAP;
      } else if (type.equals("acl")) {
         t = stanza_type.ACL;
      } else {
         Assert(false, "unknown stanza type");
      }
      current = new stanza(t);
   }

   public void exitStanza(String name) {
      Assert(current!=null, "exit a null stanza");
      current.name = name;
      stanzas.add(current);
      current = null;
   }

   public void addStanzaReference(String type, String name) {
      Assert(current!=null, "current is null, reference to another stanza");
      stanza_type t = null;
      if (type.equals("router")) {
         t = stanza_type.ROUTER;
      } else if (type.equals("iface")) {
         t = stanza_type.IFACE;
      } else if (type.equals("routemap")) {
         t = stanza_type.ROUTEMAP;
      } else if (type.equals("acl")) {
         t = stanza_type.ACL;
      } else {
         Assert(false, "addStanzaReference unknown stanza type");
      }
      current.AddReference(t, name);
   }

   public Integer getIntraComplexity() {
      int totalReferences = 0;
      for (stanza s : stanzas) {
         List<reference_to> references = s.references;
         for (reference_to to : references) {
            stanza dst = new stanza(to.type, to.name);
            if (!stanzas.contains(dst)) {
               System.out.println(s + " references to a non-existing stanza: "
                     + dst);
            } else {
               totalReferences++;
            }
         }
      }
      return totalReferences;
   }

   /************************************************************
    * helper
    ***********************************************************/

   class subnet {
      public Ip ip, mask;

      public subnet(String prefix) {
         String tokens[] = prefix.split("/");
         if (tokens.length != 2) {
            System.out.println("subnet should be a prefix, but is an IP");
            assert false;
         }
         mask = new Ip(Long.parseLong(tokens[1]));
         ip = new Ip(tokens[0]);
      }

      public subnet(String _ip, String _mask) {
         ip = new Ip(_ip);
         mask = new Ip(_mask);
      }

      @Override
      public int hashCode() {
         return ip.hashCode() + mask.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof subnet)) {
            return false;
         }
         subnet other = (subnet) obj;
         return ip == other.ip && mask == other.mask;
      }

      @Override
      public String toString() {
         return ip.toString() + "/" + mask.toString();
      }
   }

   class stanza {
      public stanza_type type;
      public String name;
      List<reference_to> references;

      public stanza(stanza_type t) {
         type = t;
         name = null;
         references = new ArrayList<reference_to>();
      }

      public stanza(stanza_type t, String n) {
         type = t;
         name = n;
      }

      public void AddReference(stanza_type type, String name) {
         references.add(new reference_to(type, name));
      }

      @Override
      public int hashCode() {
         return type.hashCode() + name.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof stanza) {
            stanza stanza_obj = (stanza) obj;
            return stanza_obj.type == this.type
                  && stanza_obj.name.equals(this.name);
         }
         return false;
      }

      @Override
      public String toString() {
         return "stanza:" + name + "(" + type.name() + ")";
      }
   }

   class reference_to {
      public stanza_type type;
      public String name;

      public reference_to(stanza_type t, String n) {
         type = t;
         name = n;
      }
   }
   
   private void printBGP() {
      if (bgpAs == null)
         return;
      String out = "";
      out += bgpAs + " (ebgp): ";
      for (String n : ebgpNeighbors) {
         out += n + " ";
      }
      out += ", (ibgp): ";
      for (String n : ibgpNeighbors) {
         out += n + " ";
      }
      System.out.println(out);
   }

   private void printOSPF() {
      String out = "";
      for (subnet s : subnetOfOspfIface) {
         out += s.toString() + " ";
      }
      System.out.println(out);
   }


   private void Assert(boolean exp, String info){
      if(!exp){
         System.out.println(info);
         assert false;
      }
   }
}

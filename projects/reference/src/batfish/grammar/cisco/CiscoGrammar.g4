parser grammar CiscoGrammar;

import
CiscoGrammarCommonParser, CiscoGrammar_interface, CiscoGrammar_ospf, CiscoGrammar_bgp, CiscoGrammar_acl, CiscoGrammar_routemap;

options {
   tokenVocab = CiscoGrammarCommonLexer;
}

@header {
package batfish.grammar.cisco;


import java.util.Map;
import batfish.reference.reference;
}

@members {
// ACL type
final String ACCESS_T="access-list";
final String COMMUNITY_T="community-list";
final String PREFIX_T="prefix-list";

// Stanza type
final String ACL_T = "acl";
final String ROUTEMAP_T = "routemap";
final String IFACE_T = "iface";
final String OSPF_T = "ospf";
final String BGP_T = "bgp";

// address format
final String IP_F = "ip";
final String PREFIX_F = "subnet";
final String IPMASK_F = "ip/mask";


public String OutputAcl(String prefix){ return ref.OutputAcl(prefix);  }
public String OutputRoutemap(String prefix){ return ref.OutputRoutemap(prefix);  }   
public String OutputBgpAs(String prefix){ return ref.OutputBgpAs(prefix);  }   
public String OutputOspfProc(String prefix){ return ref.OutputOspfProc(prefix);  }   
public String OutputIntraReference(String prefix){  return ref.OutputIntraReference(prefix); }   
public String OutputIfaceIp(String prefix){ return ref.OutputIfaceIp(prefix);  }
public String OutputBgpNeighborAs(String prefix){  return ref.OutputBgpNeighborAs(prefix); }   
public String OutputOspfNetworks(String prefix){ return ref.OutputOspfNetworks(prefix);  }
public String OutputWarning(String prefix){ return ref.OutputWarning(prefix); }

batfish.reference.reference ref = new batfish.reference.reference();
// Acl
public void FindAcl(String name, String type){ ref.FindAcl(name, type);  }  
// Routemap
public void EnterRoutemap(String name){  ref.EnterRoutemap(name); }
public void ExitRoutemap(){  ref.ExitRoutemap(); }
public void RoutemapAcl(String acl){ ref.RoutemapAcl(acl);  }
// interface
public void EnterIface(String iface){ ref.EnterIface(iface);  }
public void ExitIface(){ ref.ExitIface();  }
public void IfaceRoutemap(String routemap){ ref.IfaceRoutemap(routemap);  }
public void IfaceAcl(String acl){ ref.IfaceAcl(acl);  }
public void IfaceOspf(String ospf){ ref.IfaceOspf(ospf);  }
public void IfaceIp(String format, String value){ref.IfaceIp(format, value); }
// BGP
public void EnterBgp(String asNum){ ref.EnterBgp(asNum);  }
public void ExitBgp(){ ref.ExitBgp(); }
public void BgpRoutemap(String routemap){ ref.BgpRoutemap(routemap);  }
public void BgpAcl(String acl){ ref.BgpAcl(acl);  }   
public void BgpEnterTemplate(String name){ ref.BgpEnterTemplate(name);  }
public void BgpExitTemplate(){ ref.BgpExitTemplate();  }
public void BgpTemplateAs(String as){ ref.BgpTemplateAs(as);  }
public void BgpEnterGroup(String name){ ref.BgpEnterGroup(name);  }
public void BgpExitGroup(){ ref.BgpExitGroup();  }
public void BgpGroupAs(String as){ ref.BgpGroupAs(as);  }
public void BgpEnterNeighbor(String format, String name){ ref.BgpEnterNeighbor(format, name);  }
public void BgpExitNeighbor(){ ref.BgpExitNeighbor();  }
public void BgpNeighborAs(String as){ ref.BgpNeighborAs(as);  }   

public void BgpNeighborOrGroupAs(String asNum) { ref.BgpNeighborOrGroupAs(asNum); }
public void BgpNeighborGroup(String group) { ref.BgpNeighborGroup(group); }
public void BgpNeighborNoAs(String asNum) { ref.BgpNeighborNoAs(asNum); }
public void BgpNeighborInherit(String template) { ref.BgpNeighborInherit(template); }
public void BgpTemplateInherit(String template) { ref.BgpTemplateInherit(template); }

// OSPF
public void EnterOspf(String proc){ ref.EnterOspf(proc);  }
public void ExitOspf(){ ref.ExitOspf(); }
public void OspfRoutemap(String routemap){ ref.OspfRoutemap(routemap);  }
public void OspfAcl(String acl){  ref.OspfAcl(acl); }
public void OspfNetwork(String format, String value){ ref.OspfNetwork(format, value); }
}

cisco_configuration
:
   (
      acl_stanza
      | interface_stanza
      | null_stanza
      | route_map_stanza
      | router_bgp_stanza
      | router_ospf_stanza
   )* NEWLINE* EOF
;

mstp_instance_substanza
:
   INSTANCE DEC VLAN ~NEWLINE* NEWLINE
;

mstp_null_substanza
:
   (
      NAME
      | REVISION
   ) ~NEWLINE* NEWLINE
;

mstp_stanza
:
   SPANNING_TREE MODE MST NEWLINE
;

mstp_configuration_stanza
:
   SPANNING_TREE MST CONFIGURATION NEWLINE
   (
      mstp_instance_substanza
      | mstp_null_substanza
   )*
;

null_stanza
:
   (
      HARDWARE
   ) ~NEWLINE* NEWLINE
;

udld_stanza
:
   UDLD AGGRESSIVE NEWLINE
;


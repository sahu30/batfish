parser grammar CiscoGrammar;

import
CiscoGrammarCommonParser, CiscoGrammar_interface, CiscoGrammar_ospf, CiscoGrammar_bgp;

options {
   tokenVocab = CiscoGrammarCommonLexer;
}

@header {
package batfish.grammar.cisco;


import java.util.Map;
}

@members {
public String OutputStat(String prefix) { return proto.OutputStat(prefix); }
public String OutputWarning(String prefix) { return proto.OutputWarning(prefix); }

public String OutputIfaceIp(String prefix) { return proto.OutputIfaceIp(prefix); }
public String OutputOspfNetworkArea(String prefix) { return proto.OutputOspfNetworkArea(prefix); }
public String OutputOspfPassiveIface(String prefix) { return proto.OutputOspfPassiveIface(prefix);}
public String OutputOspfPassiveIfaceDefault(String prefix){ return proto.OutputOspfPassiveIfaceDefault(prefix); }

public String OutputRuleList(String prefix){ return proto.OutputRuleList(prefix); }
public String OutputNeighborAs(String prefix){ return proto.OutputNeighborAs(prefix); }
public String OutputGroupAs(String prefix){ return proto.OutputGroupAs(prefix); }
public String OutputGroupNeighbor(String prefix){ return proto.OutputGroupNeighbor(prefix); }
public String OutputNetworks(String prefix){ return proto.OutputNetworks(prefix); }
public String OutputNeighborTemplate(String prefix){ return proto.OutputNeighborTemplate(prefix); }
public String OutputAddressFamily(String prefix){ return proto.OutputAddressFamily(prefix); }
public String OutputTemplateRemoteAs(String prefix){ return proto.OutputTemplateRemoteAs(prefix); }
public String OutputVrf(String prefix){ return proto.OutputVrf(prefix); }

batfish.l3.protocols proto = new batfish.l3.protocols();
private void EnterIface(String ifacename){ proto.EnterIface(ifacename); }
private void IfaceOspfArea(String procNum, String area) { proto.IfaceOspfArea(procNum, area); }
private void IfaceIp(String ip, String mask, boolean secondary){ proto.IfaceIp(ip, mask, secondary); }
private void IfaceIp(String ip_prefix, boolean secondary){ proto.IfaceIp(ip_prefix, secondary); }
private void IfacePassive(boolean yes){ proto.IfacePassive(yes); }
private void ExitIface(){ proto.ExitIface(); }
private void EnterOspf(String ospfID){ proto.EnterOspf(ospfID); }
private void OspfNetworkArea(String addr, String subnet, String area){ proto.OspfNetworkArea(addr, subnet, area); }
private void OspfNetworkArea(String prefix, String area){ proto.OspfNetworkArea(prefix, area); }
private void OspfRedistribute(){ proto.OspfRedistribute(); }
private void OspfPassiveDefault(){ proto.OspfPassiveDefault(); }
private void OspfPassiveIface(boolean yes, String ifacename){ proto.OspfPassiveIface(yes, ifacename); }	
private void ExitOspf(){ proto.ExitOspf(); }
private void Warning(String w){ proto.Warning(w); }


// !!!!!!!!!!!! bgp
// neighborType
final String IPV4_ADDR_T = "ipv4_addr";
final String IPV6_ADDR_T = "ipv6_addr";
final String IPV4_PREFIX_T = "ipv4_prefix";
final String IPV6_PREFIX_T = "ipv6_prefix";
final String GROUP_T = "group";
// netType
final String IPV4_MASK_T = "ipv4_mask";
final String IPV4_T = "ipv4";
final String IPv4_PREFIX_T = "ipv4_prefix";
final String IPV6_T = "ipv6";

private void BgpAfHeader(String afType){ proto.BgpAfHeader(afType); }
private void Bgp(String asNum){ proto.Bgp(asNum); }
private void BgpExit(){ proto.BgpExit(); }

private void BgpAf(){ proto.BgpAf(); }
private void BgpAfExit(){ proto.BgpAfExit(); }
private void BgpAfNetwork(String type, String net){ proto.BgpAfNetwork(type, net); }
private void BgpAfRedistribute(){ proto.BgpAfRedistribute(); }
private void BgpAfNeighbor(String type, String neighbor){ proto.BgpAfNeighbor(type, neighbor); }
private void BgpAfNeighborExit(){ proto.BgpAfNeighborExit(); }
private void BgpAfList(String type, String name){ proto.BgpAfList(type, name); }
private void BgpAfPeerGroup(String group){ proto.BgpAfPeerGroup(group); }
private void BgpAfNeighborRemoteAs(String asNum){ proto.BgpAfNeighborRemoteAs(asNum); }

private void BgpNetwork(String type, String net){ proto.BgpNetwork(type, net); }
private void BgpNeighbor(String type, String neighbor){ proto.BgpNeighbor(type, neighbor); }
private void BgpNeighborExit(){ proto.BgpNeighborExit(); }
private void BgpList(String type, String name){ proto.BgpList(type, name); }
private void BgpPeerGroup(String group){ proto.BgpPeerGroup(group); }
private void BgpNeighborRemoteAs(String asNum){ proto.BgpNeighborRemoteAs(asNum); }
private void BgpRedistribute(){ proto.BgpRedistribute(); }

private void BgpNeighborNexus(String type, String net, String asNum){ proto.BgpNeighborNexus(type, net, asNum); }
private void BgpNeighborNexusExit(){ proto.BgpNeighborNexusExit(); }
private void BgpNeighborNexusAf(){ proto.BgpNeighborNexusAf(); }
private void BgpNeighborNexusAfExit(){ proto.BgpNeighborNexusAfExit(); }
private void BgpNeighborNexusAfList(String type, String name){ proto.BgpNeighborNexusAfList(type, name); }
private void BgpNeighborNexusInherit(String template){ proto.BgpNeighborNexusInherit(template); }
private void BgpNeighborNexusRemoteAs(String asNum){ proto.BgpNeighborNexusRemoteAs(asNum); }
private void BgpNeighborNexusNoAs(String asNum){ proto.BgpNeighborNexusNoAs(asNum); }

private void BgpTemplate(String template){ proto.BgpTemplate(template); }
private void BgpTemplateExit(){ proto.BgpTemplateExit(); }
private void BgpTemplateAf(){ proto.BgpTemplateAf(); }
private void BgpTemplateAfExit(){ proto.BgpTemplateAfExit(); }
private void BgpTemplateAfList(String type, String name){ proto.BgpTemplateAfList(type, name); }
private void BgpTemplateRemoteAs(String asNum){ proto.BgpTemplateRemoteAs(asNum); }
private void BgpTemplateInherit(String inherit){ proto.BgpTemplateInherit(inherit); }

private void BgpVrfNexus(String name){ proto.BgpVrfNexus(name); }
private void BgpVrfNexusExit(){ proto.BgpVrfNexusExit(); }
private void BgpVrfAf(){ proto.BgpVrfAf(); }
private void BgpVrfAfExit(){ proto.BgpVrfAfExit(); }
private void BgpVrfAfNetwork(String type, String net){ proto.BgpVrfAfNetwork(type, net); } 
private void BgpVrfNeighborNexus(String type, String net){ proto.BgpVrfNeighborNexus(type, net); }
private void BgpVrfNeighborNexusExit(){ proto.BgpVrfNeighborNexusExit(); }
private void BgpVrfNeighborNexusInherit(String template){ proto.BgpVrfNeighborNexusInherit(template); }

}

cisco_configuration
:
   (
      interface_stanza
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

udld_stanza
:
   UDLD AGGRESSIVE NEWLINE
;


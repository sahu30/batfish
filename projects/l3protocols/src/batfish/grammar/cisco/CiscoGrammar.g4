parser grammar CiscoGrammar;

import
CiscoGrammarCommonParser, CiscoGrammar_interface, CiscoGrammar_ospf;

options {
   tokenVocab = CiscoGrammarCommonLexer;
}

@header {
package batfish.grammar.cisco;


import java.util.Map;
}

@members {
public String OutputStat(String prefix) {
   return proto.OutputStat(prefix);
}

public String OutputIfaceIp(String prefix) {
   return proto.OutputIfaceIp(prefix);
}

public String OutputOspfNetworkArea(String prefix) {
   return proto.OutputOspfNetworkArea(prefix);
}

public String OutputOspfPassiveIface(String prefix) {
   return proto.OutputOspfPassiveIface(prefix);
}

public String OutputOspfPassiveIfaceDefault(String prefix){
   return proto.OutputOspfPassiveIfaceDefault(prefix);
}

public String OutputWarning(String prefix) {
   return proto.OutputWarning(prefix);
}


batfish.l3.protocols proto = new batfish.l3.protocols();
private void EnterIface(String ifacename)  {
   proto.EnterIface(ifacename);
}
private void IfaceOspfArea(String procNum, String area)  {
   proto.IfaceOspfArea(procNum, area);
}
private void IfaceIp(String ip, String mask, boolean secondary)  {
   proto.IfaceIp(ip, mask, secondary);
}
private void IfaceIp(String ip_prefix, boolean secondary)  {
   proto.IfaceIp(ip_prefix, secondary);
}
private void IfacePassive(boolean yes)  {
   proto.IfacePassive(yes);
}

private void ExitIface()  {
   proto.ExitIface();
}

private void EnterOspf(String ospfID)  {
   proto.EnterOspf(ospfID);
}

private void OspfNetworkArea(String addr, String subnet, String area) {
   proto.OspfNetworkArea(addr, subnet, area);
}

private void OspfNetworkArea(String prefix, String area)  {
   proto.OspfNetworkArea(prefix, area);
}

private void OspfRedistribute()  {
   proto.OspfRedistribute();
}

private void OspfPassiveDefault()  {
   proto.OspfPassiveDefault();
}

private void OspfPassiveIface(boolean yes, String ifacename)  {
   proto.OspfPassiveIface(yes, ifacename);
}
	
private void ExitOspf()  {
   proto.ExitOspf();
}

private void Warning(String w){
   proto.Warning(w);
}

}

cisco_configuration
:
   (
      interface_stanza
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


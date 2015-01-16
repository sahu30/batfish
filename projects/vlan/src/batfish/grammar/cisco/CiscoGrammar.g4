parser grammar CiscoGrammar;

import
CiscoGrammarCommonParser, CiscoGrammar_interface;

options {
   tokenVocab = CiscoGrammarCommonLexer;
}

@header {
package batfish.grammar.cisco;


import java.util.Map;
}

@members {

public int OutputVlanInstance(){
   return vlanStat.OutputIntances();
}
public String OutputVlans(){
   return vlanStat.OutputVlans();
}
public String OutputIfaceVlans(String prefix){
   return vlanStat.OutputIfaceRanges(prefix);
}
private batfish.l2.vlans vlanStat = new batfish.l2.vlans();

private String currentInterface = null;
private void EnterInterfaceStanza(String IFname){
   Assert(currentInterface == null, "EnterIFStanza with currentInterface not null");
   currentInterface = IFname;
   vlanStat.EnterIface(IFname);
}
private void ExitInterfaceStanza(){
   Assert(currentInterface!=null, "ExitIFStanza with currentInterface null");
   currentInterface = null;
   vlanStat.ExitIface();
}
		
private void FindVLAN(String vlan){
   vlanStat.FindVLAN(vlan);
}
		
private void FindVLANRange(String range){
   vlanStat.FindVLANRange(range);
}

private void Assert(boolean judge, String info){
   if(!judge){
      System.out.println(info);
      assert false;
   }
}

}

cisco_configuration
:
   (
      interface_stanza
      | mstp_configuration_stanza
      | mstp_stanza
      | udld_stanza
      | vlan_stanza
   )+ NEWLINE* EOF
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

vlan_stanza
@after{ FindVLAN(_localctx.v.getText()); }
:
   VLAN v = DEC NEWLINE
;
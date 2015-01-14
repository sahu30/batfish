parser grammar CiscoGrammar;

import
CiscoGrammarCommonParser, CiscoGrammar_interface;

options {
   tokenVocab = CiscoGrammarCommonLexer;
}

@header {
package batfish.grammar.cisco;
}

@members {

	public String OutputL2(){
		return l2proto.toString();
	}
	
	public String OutputLinks(){
		return linkCount.toString();
	}
	
	private batfish.l2.protocols l2proto = new batfish.l2.protocols();
	private batfish.topology.LinkCount linkCount = new batfish.topology.LinkCount();
	
	private String currentInterface = null;
	private void EnterInterfaceStanza(String IFname){
	   Assert(currentInterface == null, "EnterIFStanza with currentInterface not null");
	   currentInterface = IFname;
	}
	private void ExitInterfaceStanza(){
	   Assert(currentInterface!=null, "ExitIFStanza with currentInterface null");
	   currentInterface = null;
	}
	private void FindInterfaceDescription(String desc){
	   if(desc.contains("INFRA")){
	      Assert(currentInterface!=null, "find physical link with currentInterface null");
	      linkCount.AddLink(currentInterface, desc);
	   }
	}
	
	private void FindMST(){
	   l2proto.FindMST();
	}
	private void FindUDLD(){
	   l2proto.FindUDLD();
	}
	
	private void FindVLANInstance(){
	   l2proto.FindVLANInstance();
	}
	private void FindUDLDInstance(){
	   l2proto.FindUDLDInstance();
	}
	private void FindMSTPInstance(){
	   l2proto.FindMSTPInstance();
	}
   private void FindDOT1QInstance(){
      l2proto.FindDOT1QInstance();
   }
   private void FindLACPInstance(){
      l2proto.FindLACPInstance();
   }
   private void FindDHCPInstance(){
      l2proto.FindDHCPInstance();
   }
   private void FindHSRPInstance(){
      l2proto.FindHSRPInstance();
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
@after{ FindMSTPInstance(); }
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
   { FindMST(); }
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
@init{ FindUDLD(); }
:
   UDLD AGGRESSIVE NEWLINE
;

vlan_stanza
:
   VLAN DEC NEWLINE  { FindVLANInstance(); }
   vlan_substanza*
;

vlan_substanza
:
   (
      NAME
   ) ~NEWLINE* NEWLINE
;

parser grammar CiscoGrammar_ospf;

import CiscoGrammarCommonParser;

options {
   tokenVocab = CiscoGrammarCommonLexer;
}

area_ipv6_ro_stanza
:
   AREA ~NEWLINE* NEWLINE
;

area_nssa_ro_stanza
:
   AREA
   (
      area_int = DEC
      | area_ip = IP_ADDRESS
   ) NSSA
   (
      NO_SUMMARY
      | DEFAULT_INFORMATION_ORIGINATE
   )* NEWLINE
;

default_information_ipv6_ro_stanza
:
   DEFAULT_INFORMATION ~NEWLINE* NEWLINE
;

default_information_ro_stanza
@after{ if(_localctx.map!=null) AddReference(stanza_type.ROUTEMAP, _localctx.map.getText()); }
:
   DEFAULT_INFORMATION ORIGINATE
   (
      (
         METRIC metric = DEC
      )
      |
      (
         METRIC_TYPE metric_type = DEC
      )
      | ALWAYS
      |
      (
         ROUTE_MAP map = VARIABLE
      )
   )* NEWLINE
;

distribute_list_stanza
@after{ AddReference(stanza_type.ACL, _localctx.name.getText()); }
:
   NO? DISTRIBUTE_LIST name = ~NEWLINE ( IN | OUT ) ~NEWLINE
;

ipv6_ro_stanza
:
   null_ipv6_ro_stanza
   | passive_interface_ipv6_ro_stanza
   | redistribute_ipv6_ro_stanza
;

ipv6_router_ospf_stanza
@init{ enterStanza(stanza_type.ROUTER); }
@after{ exitStanza("ospf_"+_localctx.procnum.getText()); }
:
   IPV6 ROUTER OSPF procnum = DEC NEWLINE
   (
      rosl += ipv6_ro_stanza
   )+
;

log_adjacency_changes_ipv6_ro_stanza
:
   LOG_ADJACENCY_CHANGES NEWLINE
;

maximum_paths_ro_stanza
:
   MAXIMUM_PATHS ~NEWLINE* NEWLINE
;

network_ro_stanza
:
   NETWORK ip = IP_ADDRESS wildcard = IP_ADDRESS AREA
   (
      area_int = DEC
      | area_ip = IP_ADDRESS
   ) NEWLINE
;

null_ipv6_ro_stanza
:
   area_ipv6_ro_stanza
   | default_information_ipv6_ro_stanza
   | log_adjacency_changes_ipv6_ro_stanza
   | router_id_ipv6_ro_stanza
;

null_ro_stanza
:
   null_standalone_ro_stanza
;

null_standalone_ro_stanza
:
   NO?
   (
      (
         AREA
         (
            DEC
            | IP_ADDRESS
         ) AUTHENTICATION
      )
      | AUTO_COST
      | BFD
//      | DISTRIBUTE_LIST
      | LOG_ADJACENCY_CHANGES
      | NSF
   ) ~NEWLINE* NEWLINE
;

passive_interface_ipv6_ro_stanza
:
   NO? PASSIVE_INTERFACE ~NEWLINE* NEWLINE
;

passive_interface_default_ro_stanza
:
   PASSIVE_INTERFACE DEFAULT NEWLINE
;

passive_interface_ro_stanza
@after{ AddReference(stanza_type.IFACE, _localctx.i.getText()); 
addOSPFIface(_localctx.i.getText()); 
}
:
   NO? PASSIVE_INTERFACE i = VARIABLE NEWLINE
;

redistribute_bgp_ro_stanza
@after{ if(_localctx.map!=null) AddReference(stanza_type.ROUTEMAP, _localctx.map.getText()); }
:
   REDISTRIBUTE BGP as = DEC
   (
      (
         METRIC metric = DEC
      )
      |
      (
         METRIC_TYPE type = DEC
      )
      |
      (
         ROUTE_MAP map = VARIABLE
      )
      | subnets = SUBNETS
      |
      (
         TAG tag = DEC
      )
   )* NEWLINE
;

redistribute_ipv6_ro_stanza
:
   REDISTRIBUTE ~NEWLINE* NEWLINE
;

redistribute_connected_ro_stanza
@after{ if(_localctx.map!=null) AddReference(stanza_type.ROUTEMAP, _localctx.map.getText()); }
:
   REDISTRIBUTE CONNECTED
   (
      (
         METRIC metric = DEC
      )
      |
      (
         METRIC_TYPE type = DEC
      )
      |
      (
         ROUTE_MAP map = VARIABLE
      )
      | subnets = SUBNETS
      |
      (
         TAG tag = DEC
      )
   )* NEWLINE
;

redistribute_rip_ro_stanza
:
   REDISTRIBUTE RIP ~NEWLINE* NEWLINE
;

redistribute_static_ro_stanza
@after{ if(_localctx.map!=null) AddReference(stanza_type.ROUTEMAP, _localctx.map.getText()); }
:
   REDISTRIBUTE STATIC
   (
      (
         METRIC metric = DEC
      )
      |
      (
         METRIC_TYPE type = DEC
      )
      |
      (
         ROUTE_MAP map = VARIABLE
      )
      | subnets = SUBNETS
      |
      (
         TAG tag = DEC
      )
   )* NEWLINE
;

ro_stanza
:
   area_nssa_ro_stanza
   | default_information_ro_stanza
   | distribute_list_stanza
   | maximum_paths_ro_stanza
   | network_ro_stanza
   | null_ro_stanza
   | passive_interface_default_ro_stanza
   | passive_interface_ro_stanza
   | redistribute_bgp_ro_stanza
   | redistribute_connected_ro_stanza
   | redistribute_rip_ro_stanza
   | redistribute_static_ro_stanza
   | router_id_ro_stanza
;

router_id_ipv6_ro_stanza
:
   ROUTER_ID ~NEWLINE* NEWLINE
;

router_id_ro_stanza
:
   ROUTER_ID ip = IP_ADDRESS NEWLINE
;

router_ospf_stanza
@init{ enterStanza(stanza_type.ROUTER); }
@after{ exitStanza("ospf_"+_localctx.procnum.getText()); }
:
   ROUTER OSPF procnum = DEC NEWLINE  { enterOSPF(_localctx.procnum.getText()); }
   router_ospf_stanza_tail
   { exitOSPF(); }
;

router_ospf_stanza_tail
:
   (
      rosl += ro_stanza
   )+
;

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
   NETWORK 
   (
      ( ip = IP_ADDRESS wildcard = IP_ADDRESS ) 
      | prefix = IP_PREFIX
   )
   AREA
   (
      area_int = DEC
      | area_ip = IP_ADDRESS
   ) NEWLINE
   { String area = (_localctx.area_int==null? _localctx.area_ip.getText() : _localctx.area_int.getText());
     if(_localctx.ip==null) OspfNetworkArea(_localctx.prefix.getText(), area);
     else OspfNetworkArea(_localctx.ip.getText(), _localctx.wildcard.getText(), area); 
   }
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
      | LOG_ADJACENCY_CHANGES
      | NSF
      | MAX_METRIC
   ) ~NEWLINE* NEWLINE
;

passive_interface_ipv6_ro_stanza
:
   NO? PASSIVE_INTERFACE ~NEWLINE* NEWLINE
;

passive_interface_default_ro_stanza
@after{ OspfPassiveDefault(); }
:
   PASSIVE_INTERFACE DEFAULT NEWLINE
;

passive_interface_ro_stanza
@after{
  boolean yes = (_localctx.no==null? true: false);
  OspfPassiveIface(yes, _localctx.i.getText());
} 
:
   no = NO? PASSIVE_INTERFACE i = interface_name NEWLINE
;

redistribute_bgp_ro_stanza
@after{ OspfRedistribute(); }
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
@after{ OspfRedistribute(); }
:
   REDISTRIBUTE ~NEWLINE* NEWLINE
;

redistribute_connected_ro_stanza
@after{ OspfRedistribute(); }
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
@after{ OspfRedistribute(); }
:
   REDISTRIBUTE RIP ~NEWLINE* NEWLINE
;

redistribute_static_ro_stanza
@after{ OspfRedistribute(); }
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
:
   ROUTER OSPF procnum = DEC ( VRF VARIABLE )? NEWLINE 
   { EnterOspf(_localctx.procnum.getText()); }
   router_ospf_stanza_tail
   { ExitOspf(); }
;

router_ospf_stanza_tail
:
   (
      rosl += ro_stanza
   )+
;

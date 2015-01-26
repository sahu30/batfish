parser grammar CiscoGrammar_bgp;

import CiscoGrammarCommonParser;

options {
   tokenVocab = CiscoGrammarCommonLexer;
}

address_family_header
:
   ADDRESS_FAMILY
   (
      ipv4 = IPV4
      | ipv6 = IPV6
      | vpnv4 = VPNV4
      | vpnv6 = VPNV6
   )
   (
      unicast = UNICAST
      | multicast = MULTICAST
      | mdt = MDT
      | ( vrf = VRF vrf_name = VARIABLE )
   )? NEWLINE
;

address_family_footer
:
   EXIT_ADDRESS_FAMILY NEWLINE
;

address_family_substanza
:
   address_family_header 
   (
      af_neighbor_substanza
      | af_network_substanza
      | af_no_neighbor_substanza
      | af_null_substanza
      | af_rb_substanza
   )* address_family_footer? 
;

af_neighbor_distribute_list_tail
:
   DISTRIBUTE_LIST num = DEC ( IN | OUT )? NEWLINE
   { BgpAcl(_localctx.num.getText()); }
;

af_neighbor_filter_list_tail
:
   FILTER_LIST list = DEC ( IN | OUT )? NEWLINE
   { BgpAcl(_localctx.list.getText()); }
;

af_neighbor_peer_group_tail
:
   PEER_GROUP group = VARIABLE NEWLINE
   { BgpNeighborGroup(_localctx.group.getText()); }
;

af_neighbor_prefix_list_tail
:
   PREFIX_LIST list = VARIABLE ( IN | OUT ) NEWLINE
   { BgpAcl(_localctx.list.getText()); }
;

af_neighbor_remote_as_tail
:
   ( LOCAL_AS | REMOTE_AS ) asNum = DEC NEWLINE
   { BgpNeighborOrGroupAs(_localctx.asNum.getText()); }
;

af_neighbor_route_map_tail
:
   ROUTE_MAP map = VARIABLE ( IN | OUT )? NEWLINE
   { BgpRoutemap(_localctx.map.getText()); }
;

af_neighbor_substanza
:
  NEIGHBOR 
  ( 
     ( ip = IP_ADDRESS { BgpEnterNeighbor(IP_F, _localctx.ip.getText()); } )
     | ipv6 = IPV6_ADDRESS
     | ( group = VARIABLE { BgpEnterGroup(_localctx.group.getText()); }  )
  )
  (
     af_neighbor_distribute_list_tail
     | af_neighbor_filter_list_tail
     | af_neighbor_null_tail
     | af_neighbor_peer_group_tail
     | af_neighbor_prefix_list_tail
     | af_neighbor_remote_as_tail
     | af_neighbor_route_map_tail
  )
  { 
  	if(_localctx.ip!=null) BgpExitNeighbor();
  	else if(_localctx.group!=null) BgpExitGroup();
  }
;

af_network_substanza
:
   NETWORK 
   (
      ( ip = IP_ADDRESS ( MASK mask =IP_ADDRESS )? ) 
      | prefix = IP_PREFIX
      | ipv6 = IPV6_PREFIX
   ) 
   ( ROUTE_MAP VARIABLE )?
   NEWLINE
;

af_no_neighbor_substanza
:
   no_neighbor_substanza
;

af_rb_substanza
:
   REDISTRIBUTE
   (
      af_rb_connected_tail
      | af_rb_direct_tail
      | af_rb_static_tail
   )
;

af_rb_connected_tail
:
   CONNECTED ( ROUTE_MAP map = VARIABLE  { BgpRoutemap(_localctx.map.getText()); } )? NEWLINE
;

af_rb_direct_tail
:
   DIRECT ROUTE_MAP map = VARIABLE NEWLINE
   { BgpRoutemap(_localctx.map.getText()); }
;

af_rb_static_tail
:
   STATIC ( ROUTE_MAP map = VARIABLE  { BgpRoutemap(_localctx.map.getText()); } )? NEWLINE
;

neighbor_nexus_address_family_substanza
:
   address_family_header
   (
      neighbor_nexus_af_filter_list_substanza
      | neighbor_nexus_af_prefix_list_substanza
      | neighbor_nexus_af_route_map_substanza
      | neighbor_nexus_af_null_substanza
   )*
;

neighbor_filter_list_tail
:
   FILTER_LIST list = DEC ( IN | OUT )? NEWLINE
   { BgpAcl(_localctx.list.getText()); }
;

neighbor_nexus_af_filter_list_substanza
:
   FILTER_LIST list = DEC ( IN | OUT )? NEWLINE
   { BgpAcl(_localctx.list.getText()); }
;

neighbor_nexus_af_prefix_list_substanza
:
   PREFIX_LIST list = VARIABLE ( IN | OUT )? NEWLINE
   { BgpAcl(_localctx.list.getText()); }
;

neighbor_nexus_af_route_map_substanza
:
   ROUTE_MAP map = VARIABLE ( IN | OUT )? NEWLINE
   { BgpRoutemap(_localctx.map.getText()); }
;

neighbor_nexus_inherit_substanza
:
   INHERIT PEER peer = VARIABLE NEWLINE
   { BgpNeighborInherit(_localctx.peer.getText()); }
;

neighbor_nexus_no_remote_as_substanza
:
   NO REMOTE_AS asNum = DEC NEWLINE
   { BgpNeighborNoAs(_localctx.asNum.getText()); }
;

neighbor_nexus_remote_as_substanza
:
   REMOTE_AS asNum = DEC NEWLINE
   { BgpNeighborAs(_localctx.asNum.getText()); }
;

neighbor_nexus_substanza
:
   NEIGHBOR 
   (
      ( ip = IP_ADDRESS { BgpEnterNeighbor(IP_F, _localctx.ip.getText()); } )
      | ipv6 = IPV6_ADDRESS
      | ( prefix = IP_PREFIX { BgpEnterNeighbor(PREFIX_F, _localctx.prefix.getText()); } )
      | prefixv6 = IPV6_PREFIX
   ) 
   ( REMOTE_AS asNum = DEC { BgpNeighborAs(_localctx.asNum.getText()); } )? NEWLINE
   (  
      neighbor_nexus_address_family_substanza
      | neighbor_nexus_inherit_substanza
      | neighbor_nexus_no_remote_as_substanza
      | neighbor_nexus_null_substanza
      | neighbor_nexus_remote_as_substanza
   )*
   { if(_localctx.ip!=null || _localctx.prefix!=null) BgpExitNeighbor(); }
;

af_neighbor_null_tail
:
   neighbor_null_tail
;

template_af_null_subsubstanza
:
   neighbor_null_tail
;

template_null_substanza
:
   neighbor_null_tail
   | ( NO 
         (
            DESCRIPTION
            | SHUTDOWN
         ) ~NEWLINE* NEWLINE 
     )
;

neighbor_empty_tail
:
   NEWLINE
;

neighbor_nexus_af_null_substanza
:
   neighbor_null_tail
;

neighbor_nexus_null_substanza
:
   neighbor_null_tail
   | ( 
        NO 
        (
           ROUTE_MAP
           | SHUTDOWN
        ) ~NEWLINE* NEWLINE 
     ) 
;

neighbor_null_tail
:
   (
      ACTIVATE
      | ALLOWAS_IN
      | DEFAULT_ORIGINATE
      | DESCRIPTION
      | DONT_CAPABILITY_NEGOTIATE
      | EBGP_MULTIHOP
      | MAXIMUM_PEERS
      | MAXIMUM_PREFIX
      | NEXT_HOP_SELF
      | PASSWORD
      | REMOVE_PRIVATE_AS
      | ROUTE_REFLECTOR_CLIENT
      | SEND_COMMUNITY
      | SEND_LABEL
      | SHUTDOWN
      | SOFT_RECONFIGURATION
      | TIMERS
      | UPDATE_SOURCE
      | VERSION
   ) ~NEWLINE* NEWLINE
;

neighbor_peer_group_tail
:
   PEER_GROUP ( group = VARIABLE   { BgpNeighborGroup(_localctx.group.getText()); } )? NEWLINE  
;

neighbor_prefix_list_tail
:
   PREFIX_LIST list = VARIABLE ( IN | OUT )? NEWLINE
   { BgpAcl(_localctx.list.getText()); }
;

neighbor_remote_as_tail
:
   REMOTE_AS asNum = DEC NEWLINE
   { BgpNeighborOrGroupAs(_localctx.asNum.getText()); }
;

neighbor_route_map_tail
:
   ROUTE_MAP map = VARIABLE ( IN | OUT )? NEWLINE
   { BgpRoutemap(_localctx.map.getText()); }
;

neighbor_standalone_substanza
:
   NEIGHBOR
   (
      ( ip = IP_ADDRESS { BgpEnterNeighbor(IP_F, _localctx.ip.getText()); } )
      | ip6 = IPV6_ADDRESS
      | ( peergroup = VARIABLE { BgpEnterGroup(_localctx.peergroup.getText()); } )
   )
   (
      neighbor_filter_list_tail
      | neighbor_null_tail
      | neighbor_peer_group_tail
      | neighbor_prefix_list_tail
      | neighbor_remote_as_tail
      | neighbor_route_map_tail
   )
   { 
   	if(_localctx.ip!=null) BgpExitNeighbor(); 
   	else if(_localctx.peergroup!=null) BgpExitGroup();
   }
;

network_substanza
:
   NETWORK ( ip = IP_ADDRESS ( MASK mask = IP_ADDRESS )? ) 
   ( ROUTE_MAP map = VARIABLE  { BgpRoutemap(_localctx.map.getText()); } )?
   NEWLINE
;

no_neighbor_substanza
:
   NO NEIGHBOR ( IP_ADDRESS | VARIABLE | IPV6_ADDRESS )
   (
      ACTIVATE
      | ACTIVE
      | TRANSPORT
   ) ~NEWLINE* NEWLINE
;

af_null_substanza
:
   null_bgp_substanza
;

null_bgp_substanza
:
   NO?
   (
      AGGREGATE_ADDRESS
      | AUTO_SUMMARY
      | BESTPATH
      | BGP
      | DEFAULT_INFORMATION
      | LOG_NEIGHBOR_CHANGES
      | MAXIMUM_PATHS
      | SHUTDOWN
      | SYNCHRONIZATION
      | ( TEMPLATE PEER_SESSION )
   ) ~NEWLINE* NEWLINE
;

redistribute_substanza
:
   REDISTRIBUTE ( STATIC | CONNECTED ) ( ROUTE_MAP map = VARIABLE { BgpRoutemap(_localctx.map.getText()); } )? NEWLINE
;

// !!!!!!!!!!!!!!! router bgp
router_bgp_stanza
:
   ROUTER BGP asNum = DEC NEWLINE   { EnterBgp(_localctx.asNum.getText()); }
   (
      address_family_substanza
      | neighbor_nexus_substanza
      | neighbor_standalone_substanza
      | network_substanza
      | no_neighbor_substanza
      | null_bgp_substanza
      | redistribute_substanza
      | template_peer_substanza
      | vrf_nexus_substanza
   )*
   { ExitBgp(); }
;
//!!!!!!!!!! template 
template_peer_substanza
:
   TEMPLATE PEER name = VARIABLE NEWLINE
   { BgpEnterTemplate(_localctx.name.getText()); }
   (
      template_address_family_substanza
      | template_inherit_substanza
      | template_null_substanza
      | template_remote_as_substanza
   )*
   { BgpExitTemplate(); }
;

template_address_family_header
:
   address_family_header
;

template_address_family_substanza
:
   template_address_family_header
   (
      template_af_null_subsubstanza
      | template_af_prefix_list_substanza
      | template_af_route_map_subsubstanza
   )*
;

template_af_prefix_list_substanza
:
   PREFIX_LIST list = VARIABLE ( IN | OUT )? NEWLINE
   { BgpAcl(_localctx.list.getText()); }
;

template_af_route_map_subsubstanza
:
   ROUTE_MAP map = VARIABLE ( IN | OUT )? NEWLINE
   { BgpRoutemap(_localctx.map.getText()); }
;

template_inherit_substanza
:
   INHERIT PEER_SESSION inherit = VARIABLE NEWLINE
   { BgpTemplateInherit(_localctx.inherit.getText()); }
;

template_remote_as_substanza
:
   REMOTE_AS asNum = DEC NEWLINE
   { BgpTemplateAs(_localctx.asNum.getText()); }
;

vrf_address_family_nexus_stanza
:
   address_family_header
   (
      vrf_af_network_substanza
     | vrf_af_null_substanza
   )*
;

vrf_af_network_substanza
:
   NETWORK 
   (
      prefix = IP_PREFIX
      | ipv6 = IPV6_ADDRESS
   ) NEWLINE
;

vrf_af_null_substanza
:
   (
      MAXIMUM_PATHS
   ) ~NEWLINE* NEWLINE
;

vrf_neighbor_nexus_stanza
:
   NEIGHBOR
   (
      ( ip = IP_ADDRESS { BgpEnterNeighbor(IP_F, _localctx.ip.getText()); } )
      | ipv6 = IPV6_ADDRESS
   )
   NEWLINE
   (
      vrf_neighbor_inherit_substanza
      | vrf_neighbor_null_substanza
   )*
   { if(_localctx.ip!=null) BgpExitNeighbor(); }
;

vrf_neighbor_inherit_substanza
:
   INHERIT PEER peer = VARIABLE NEWLINE
   { BgpNeighborInherit(_localctx.peer.getText()); }
;

vrf_neighbor_null_substanza
:
   (
      DESCRIPTION
      | DEFAULT
   ) ~NEWLINE* NEWLINE
;

vrf_nexus_substanza
:
   VRF name = VARIABLE NEWLINE
   (      
      vrf_address_family_nexus_stanza
      | vrf_neighbor_nexus_stanza
      | vrf_null_stanza
   )*
;

vrf_null_stanza
:
   (
      ROUTER_ID
   ) ~NEWLINE* NEWLINE
;

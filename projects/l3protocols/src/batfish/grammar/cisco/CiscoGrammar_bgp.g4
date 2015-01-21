parser grammar CiscoGrammar_bgp;

import CiscoGrammarCommonParser;

options {
   tokenVocab = CiscoGrammarCommonLexer;
}

address_family_header
@after{ 
String f;
if(_localctx.ipv4!=null){ f = _localctx.ipv4.getText(); }
else if(_localctx.ipv6!=null){ f = _localctx.ipv6.getText(); }
else if(_localctx.vpnv4!=null){ f = _localctx.vpnv4.getText(); }
else{ f = _localctx.vpnv6.getText(); }
String n = "NA";
if(_localctx.unicast!=null){ n = "unicast"; }
else if(_localctx.multicast!=null){ n = "multicast"; }
else if(_localctx.mdt!=null){ n = "mdt"; }
else if(_localctx.vrf!=null){ n = "vrf_"+_localctx.vrf_name.getText(); }
String afType = f+":"+n;
BgpAfHeader(afType); 
}
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
   address_family_header { BgpAf(); }
   (
      af_neighbor_substanza
      | af_network_substanza
      | af_no_neighbor_substanza
      | af_null_substanza
      | af_rb_substanza
   )* address_family_footer? { BgpAfExit(); }
;

af_neighbor_distribute_list_tail
@after{ BgpAfList("distribute-list", _localctx.num.getText()); }
:
   DISTRIBUTE_LIST num = DEC ( IN | OUT )? NEWLINE
;

af_neighbor_filter_list_tail
@after{ BgpAfList("filter-list", _localctx.list.getText()); }
:
   FILTER_LIST list = DEC ( IN | OUT )? NEWLINE
;

af_neighbor_peer_group_tail
@after{ BgpAfPeerGroup(_localctx.group.getText()); }
:
   PEER_GROUP group = VARIABLE NEWLINE
;

af_neighbor_prefix_list_tail
@after{ BgpAfList("prefix-list", _localctx.list.getText()); }
:
   PREFIX_LIST list = VARIABLE ( IN | OUT ) NEWLINE
;

af_neighbor_remote_as_tail
@after{ BgpAfNeighborRemoteAs(_localctx.asNum.getText()); }
:
   ( LOCAL_AS | REMOTE_AS ) asNum = DEC NEWLINE
;

af_neighbor_route_map_tail
@after{ BgpAfList("route-map", _localctx.map.getText()); }
:
   ROUTE_MAP map = VARIABLE ( IN | OUT )? NEWLINE
;

af_neighbor_substanza
:
  NEIGHBOR 
  ( 
     ip = IP_ADDRESS
     | ipv6 = IPV6_ADDRESS
     | group = VARIABLE
  ) 
  {
     String neighborType;
     String value;
     if(_localctx.ip!=null){ neighborType = IPV4_ADDR_T; value = _localctx.ip.getText(); }
     else if(_localctx.ipv6!=null){ neighborType = IPV6_ADDR_T; value = _localctx.ipv6.getText(); }
     else{ neighborType = GROUP_T; value = _localctx.group.getText(); }
     BgpAfNeighbor(neighborType, value); 
  }
  (
     af_neighbor_distribute_list_tail
     | af_neighbor_filter_list_tail
     | af_neighbor_null_tail
     | af_neighbor_peer_group_tail
     | af_neighbor_prefix_list_tail
     | af_neighbor_remote_as_tail
     | af_neighbor_route_map_tail
  )
  { BgpAfNeighborExit(); }
;

af_network_substanza
@after{
   String addressType;
   String value;
   if(_localctx.ip!=null && _localctx.mask !=null){ addressType = IPV4_MASK_T; value = _localctx.ip.getText()+"/"+_localctx.mask.getText(); }
   else if(_localctx.ip!=null && _localctx.mask == null){ addressType = IPV4_T; value = _localctx.ip.getText(); }
   else if(_localctx.prefix!=null){ addressType = IPV4_PREFIX_T; value = _localctx.prefix.getText(); }
   else{ addressType = IPV6_T; value = _localctx.ipv6.getText(); }
   BgpAfNetwork(addressType, value); 
}
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
@after{ BgpAfRedistribute(); }
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
   CONNECTED ( ROUTE_MAP map = VARIABLE )? NEWLINE
;

af_rb_direct_tail
:
   DIRECT ROUTE_MAP map = VARIABLE NEWLINE
;

af_rb_static_tail
:
   STATIC ( ROUTE_MAP map = VARIABLE )? NEWLINE
;

neighbor_nexus_address_family_substanza
:
   address_family_header
   { BgpNeighborNexusAf(); }
   (
      neighbor_nexus_af_filter_list_substanza
      | neighbor_nexus_af_prefix_list_substanza
      | neighbor_nexus_af_route_map_substanza
      | neighbor_nexus_af_null_substanza
   )*
   { BgpNeighborNexusAfExit(); }
;

neighbor_filter_list_tail
@after{ BgpList("filter-list", _localctx.list.getText()); } 
:
   FILTER_LIST list = DEC ( IN | OUT )? NEWLINE
;

neighbor_nexus_af_filter_list_substanza
@after{ BgpNeighborNexusAfList("filter-list", _localctx.list.getText()); }
:
   FILTER_LIST list = DEC ( IN | OUT )? NEWLINE
;

neighbor_nexus_af_prefix_list_substanza
@after{ BgpNeighborNexusAfList("prefix-list", _localctx.list.getText()); }
:
   PREFIX_LIST list = VARIABLE ( IN | OUT )? NEWLINE
;

neighbor_nexus_af_route_map_substanza
@after{ BgpNeighborNexusAfList("route-map", _localctx.map.getText()); }
:
   ROUTE_MAP map = VARIABLE ( IN | OUT )? NEWLINE
;

neighbor_nexus_inherit_substanza
@after{ BgpNeighborNexusInherit(_localctx.peer.getText()); }
:
   INHERIT PEER peer = VARIABLE NEWLINE
;

neighbor_nexus_no_remote_as_substanza
@after{ BgpNeighborNexusNoAs(_localctx.asNum.getText()); }
:
   NO REMOTE_AS asNum = DEC NEWLINE
;

neighbor_nexus_remote_as_substanza
@after{ BgpNeighborNexusRemoteAs(_localctx.asNum.getText()); }
:
   REMOTE_AS asNum = DEC NEWLINE
;

neighbor_nexus_substanza
:
   NEIGHBOR 
   (
      ip = IP_ADDRESS
      | ipv6 = IPV6_ADDRESS
      | prefix = IP_PREFIX
      | prefixv6 = IPV6_PREFIX
   ) 
   ( REMOTE_AS asNum = DEC )? NEWLINE
   {
      String neighborType;
      String value;
      if(_localctx.ip!=null){ neighborType = IPV4_ADDR_T; value = _localctx.ip.getText(); }
      else if(_localctx.ipv6!=null){ neighborType = IPV6_ADDR_T; value = _localctx.ipv6.getText(); }
      else if(_localctx.prefix!=null){ neighborType = IPV4_PREFIX_T; value = _localctx.prefix.getText(); }
      else{ neighborType = IPV6_PREFIX_T; value = _localctx.prefixv6.getText(); }
      String as = _localctx.asNum==null? null: _localctx.asNum.getText();
      BgpNeighborNexus(neighborType, value, as); 
   }
   (  
      neighbor_nexus_address_family_substanza
      | neighbor_nexus_inherit_substanza
      | neighbor_nexus_no_remote_as_substanza
      | neighbor_nexus_null_substanza
      | neighbor_nexus_remote_as_substanza
   )*
   { BgpNeighborNexusExit(); }
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
@after{ 
String g = (_localctx.group == null? null: _localctx.group.getText());
BgpPeerGroup(g); 
}
:
   PEER_GROUP ( group = VARIABLE )? NEWLINE
;

neighbor_prefix_list_tail
@after{ BgpList("prefix-list", _localctx.list.getText()); }
:
   PREFIX_LIST list = VARIABLE ( IN | OUT )? NEWLINE
;

neighbor_remote_as_tail
@after{ BgpNeighborRemoteAs(_localctx.asNum.getText()); }
:
   REMOTE_AS asNum = DEC NEWLINE
;

neighbor_route_map_tail
@after{ BgpList("route-map", _localctx.map.getText()); }
:
   ROUTE_MAP map = VARIABLE ( IN | OUT )? NEWLINE
;

neighbor_standalone_substanza
:
   NEIGHBOR
   (
      ip = IP_ADDRESS
      | ip6 = IPV6_ADDRESS
      | peergroup = VARIABLE
   )
   {
      String neighborType;
      String value;
      if(_localctx.ip!=null){ neighborType = IPV4_ADDR_T; value = _localctx.ip.getText(); }
      else if(_localctx.ip6!=null){ neighborType = IPV6_ADDR_T; value = _localctx.ip6.getText(); }
      else{ neighborType = GROUP_T; value = _localctx.peergroup.getText(); } 
      BgpNeighbor(neighborType, value); 
   }
   (
      neighbor_filter_list_tail
      | neighbor_null_tail
      | neighbor_peer_group_tail
      | neighbor_prefix_list_tail
      | neighbor_remote_as_tail
      | neighbor_route_map_tail
   )
   { BgpNeighborExit(); }
;

network_substanza
@after{ 
   String addressType;
   String value;
   if(_localctx.mask==null){ addressType = IPV4_T; value = _localctx.ip.getText(); }
   else{ addressType = IPV4_MASK_T; value = _localctx.ip.getText()+"/"+_localctx.mask.getText(); }
   BgpNetwork(addressType, value); 
}
:
   NETWORK ( ip = IP_ADDRESS ( MASK mask = IP_ADDRESS )? ) 
   ( ROUTE_MAP VARIABLE )?
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
@after{ BgpRedistribute(); }
:
   REDISTRIBUTE ( STATIC | CONNECTED ) ( ROUTE_MAP VARIABLE )? NEWLINE
;

// !!!!!!!!!!!!!!! router bgp
router_bgp_stanza
:
   ROUTER BGP asNum = DEC NEWLINE   { Bgp(_localctx.asNum.getText()); }
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
   { BgpExit(); }
;
//!!!!!!!!!! template 
template_peer_substanza
:
   TEMPLATE PEER name = VARIABLE NEWLINE
   { BgpTemplate(_localctx.name.getText()); }
   (
      template_address_family_substanza
      | template_inherit_substanza
      | template_null_substanza
      | template_remote_as_substanza
   )*
   { BgpTemplateExit(); }
;

template_address_family_header
:
   address_family_header
;

template_address_family_substanza
:
   template_address_family_header
   { BgpTemplateAf(); }
   (
      template_af_null_subsubstanza
      | template_af_prefix_list_substanza
      | template_af_route_map_subsubstanza
   )*
   { BgpTemplateAfExit(); }
;

template_af_prefix_list_substanza
@after{ BgpTemplateAfList("prefix-list", _localctx.list.getText()); }
:
   PREFIX_LIST list = VARIABLE ( IN | OUT )? NEWLINE
;

template_af_route_map_subsubstanza
@after{ BgpTemplateAfList("route-map", _localctx.map.getText()); }
:
   ROUTE_MAP map = VARIABLE ( IN | OUT )? NEWLINE
;

template_inherit_substanza
@after{ BgpTemplateInherit(_localctx.inherit.getText()); }
:
   INHERIT PEER_SESSION inherit = VARIABLE NEWLINE
;

template_remote_as_substanza
@after{ BgpTemplateRemoteAs(_localctx.asNum.getText()); }
:
   REMOTE_AS asNum = DEC NEWLINE
;

vrf_address_family_nexus_stanza
:
   address_family_header
   { BgpVrfAf(); }
   (
      vrf_af_network_substanza
     | vrf_af_null_substanza
   )*
   { BgpVrfAfExit(); }
;

vrf_af_network_substanza
@after{
   String type = null;
   String value = null;
   if(_localctx.prefix!=null){ type = IPV4_PREFIX_T; value = _localctx.prefix.getText(); }
   else{ type = IPV6_ADDR_T; value = _localctx.ipv6.getText(); }
   BgpVrfAfNetwork(type, value); 
}
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
      ip = IP_ADDRESS
      | ipv6 = IPV6_ADDRESS
   )
   NEWLINE
   { 
      String type, value; 
      if(_localctx.ip!=null){ type = IPV4_ADDR_T; value = _localctx.ip.getText(); }
      else{ type = IPV6_ADDR_T; value = _localctx.ipv6.getText(); }
      BgpVrfNeighborNexus(type, value); 
   }
   (
      vrf_neighbor_inherit_substanza
      | vrf_neighbor_null_substanza
   )*
   { BgpVrfNeighborNexusExit(); }
;

vrf_neighbor_inherit_substanza
@after{ BgpVrfNeighborNexusInherit(_localctx.peer.getText()); }
:
   INHERIT PEER peer = VARIABLE NEWLINE
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
   { BgpVrfNexus(_localctx.name.getText()); }
   (      
      vrf_address_family_nexus_stanza
      | vrf_neighbor_nexus_stanza
      | vrf_null_stanza
   )*
   { BgpVrfNexusExit(); }
;

vrf_null_stanza
:
   (
      ROUTER_ID
   ) ~NEWLINE* NEWLINE
;

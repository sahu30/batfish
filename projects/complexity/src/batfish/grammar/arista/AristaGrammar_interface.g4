parser grammar AristaGrammar_interface;

import AristaGrammarCommonParser;

options {
   tokenVocab = AristaGrammarCommonLexer;
}

description_if_stanza
:
   description_line
;

hsrp_stanza
:
   HSRP group = DEC NEWLINE hsrp_stanza_tail
   | HSRP VERSION DEC NEWLINE
;

hsrp_stanza_tail
:
   (
      hsrpcl += hsrpc_stanza
   )*
;

hsrpc_stanza
:
   preempt_stanza
   | priority_stanza
   | ip_address_stanza
   | null_standalone_hsrpc_stanza
;

if_stanza
:
   description_if_stanza
   | ip_access_group_if_stanza
   | ip_address_if_stanza
   | ip_address_secondary_if_stanza
   | ip_ospf_cost_if_stanza
   | ip_ospf_dead_interval_if_stanza
   | ip_ospf_dead_interval_minimal_if_stanza
   | ip_ospf_router_if_stanza
   | ip_policy_if_stanza
   | mac_access_group_if_stanza
   | no_ip_address_if_stanza
   | null_if_stanza
   | shutdown_if_stanza
   | switchport_access_if_stanza
   | switchport_trunk_native_if_stanza
   | switchport_trunk_encapsulation_if_stanza
   | switchport_trunk_allowed_if_stanza
   | switchport_mode_access_stanza
   | switchport_mode_dynamic_auto_stanza
   | switchport_mode_dynamic_desirable_stanza
   | switchport_mode_trunk_stanza
   | vrf_forwarding_if_stanza
   | vrf_member_if_stanza
;

interface_stanza
@init{ enterStanza("iface"); 
//System.out.println("enter interface stanza");
}
@after{ exitStanza(_localctx.iname.getText()); 
//System.out.println("exit if stanza");
}
:
//   INTERFACE iname = interface_name MULTIPOINT? NEWLINE interface_stanza_tail
//   INTERFACE iname = interface_name  ~NEWLINE* NEWLINE interface_stanza_tail
   INTERFACE iname = interface_name  (MULTIPOINT | MODULE DEC | POINT_TO_POINT )? NEWLINE 
   { enterIface(_localctx.iname.getText()); }
   interface_stanza_tail
   { exitIface(); }
//   { System.out.println("interface_stanza,"+_localctx.iname.getText() ); }
;

interface_stanza_tail
:
   (
      ifsl += if_stanza
   )*
;

ip_access_group_if_stanza
@after{ addStanzaReference("acl", _localctx.name.getText()); }
:
   IP PORT? ACCESS_GROUP name = .
   (
      IN
      | OUT
   ) NEWLINE
;

ip_address_stanza
:
   IP ip = IP_ADDRESS NEWLINE
;

ip_address_if_stanza
:
   IP ADDRESS
   (
      ip = IP_ADDRESS subnet = IP_ADDRESS  { addIfaceSubnet(_localctx.ip.getText(), _localctx.subnet.getText()); }
      | prefix = IP_PREFIX { addIfaceSubnet(_localctx.prefix.getText()); }
   )
   (
      STANDBY IP_ADDRESS
   )? NEWLINE
;

ip_address_secondary_if_stanza
:
   IP ADDRESS ((ip = IP_ADDRESS subnet = IP_ADDRESS) | IP_PREFIX) SECONDARY NEWLINE
;

ip_ospf_cost_if_stanza
:
   IP OSPF COST cost = DEC NEWLINE
;

ip_ospf_dead_interval_if_stanza
:
   IP OSPF DEAD_INTERVAL seconds = DEC NEWLINE
;

ip_ospf_dead_interval_minimal_if_stanza
:
   IP OSPF DEAD_INTERVAL MINIMAL HELLO_MULTIPLIER mult = DEC NEWLINE
;

ip_ospf_router_if_stanza
@init{ addOSPFSubnetInIface(); }
:
   IP ROUTER OSPF procnum = DEC ~NEWLINE* NEWLINE
;

ip_policy_if_stanza
@after{ addStanzaReference("routemap", _localctx.name.getText()); }
:
   IP POLICY ROUTE_MAP name = ~NEWLINE NEWLINE
;

mac_access_group_if_stanza
@after{ addStanzaReference("acl", _localctx.name.getText()); }
:
   MAC PORT? ACCESS_GROUP name = . NEWLINE
;

no_ip_address_if_stanza
:
   NO IP ADDRESS NEWLINE
;

null_standalone_hsrpc_stanza
:
   TRACK
   ~NEWLINE* NEWLINE  
//   { System.out.println("hsrp track substanza"); }
;

null_block_if_stanza
:
   (
      FRAME_RELAY
   ) ~NEWLINE* NEWLINE
   null_block_if_substanza+
;

null_block_if_substanza
:
   (
      CLASS
   ) ~NEWLINE* NEWLINE
;

null_if_stanza
:
   hsrp_stanza
   |
   (
      NO? SWITCHPORT NEWLINE
   )
   | null_block_if_stanza
   | null_standalone_if_stanza
;

null_standalone_if_stanza
:
   NO?
   (
      ARP
      | ASYNC
      | ATM
      | AUTO
      | AUTOSTATE
      | BANDWIDTH
      | BEACON
      | CABLELENGTH
      | CDP
      | CHANNEL
      | CHANNEL_GROUP
      | CHANNEL_PROTOCOL
      | CLNS
      | CLOCK
      | COUNTER
      | CRYPTO
      | DCBX
      | DESCRIPTION
      |
      (
         DSU BANDWIDTH
      )
      | DUPLEX
      | ENCAPSULATION
      | FAIR_QUEUE
      | FLOWCONTROL
      | FRAMING
      | FULL_DUPLEX
      | GLBP
      | GROUP_RANGE
      | HALF_DUPLEX
      | HOLD_QUEUE
      |
      (
         IP  
//         {System.out.println("null_standalone_stanza, IP");}
         (
            ACCOUNTING
            | ARP
            | BROADCAST_ADDRESS
            | CGMP
            | DHCP 
//            {System.out.println("DHCP in IF null_standalone");}
            | DVMRP
//            | DEFAULT_GATEWAY {System.out.println("in interface stanza, default-gateway"); }
            |
            (
               DIRECTED_BROADCAST
            )
            | FLOW
            | HELPER_ADDRESS
            | IGMP
            | IRDP
            | LOAD_SHARING
            | MROUTE_CACHE
            | MTU
            | MULTICAST
            |
            (
               OSPF
               (
                  AUTHENTICATION
                  | MESSAGE_DIGEST_KEY
                  | NETWORK
                  | PRIORITY
                  | PASSIVE_INTERFACE
               )
            )
            | NAT
            | PIM
            | PORT_UNREACHABLE
            | PROXY_ARP
            | REDIRECTS
            | RIP
            | ROUTE_CACHE
//            | ROUTER
            | TCP
            | UNNUMBERED
            | UNREACHABLES
            | VERIFY
            | VIRTUAL_REASSEMBLY
            | VIRTUAL_ROUTER
            | VRF
         )
      )
      | IPV6
      | ISDN
      | ISIS
      | KEEPALIVE
      | LANE
      | LAPB
      | LACP
      | LLDP
      | LOAD_INTERVAL
      | LOGGING
      | LRE
      | MAC_ADDRESS
      | MACRO
      | MANAGEMENT_ONLY
      | MAP_GROUP
      | MDIX
      | MEDIA_TYPE
      | MEMBER
      | MLAG
      | MLS
      | MOBILITY
      | MOP
      | MPLS
      | MTU
      | NAMEIF
      | NEGOTIATE
      | NEGOTIATION
      |
      (
         NTP BROADCAST
      )
      | PEER
      | PHYSICAL_LAYER
      | PORT_CHANNEL
      | POWER
      | PPP
      | PRIORITY
      | PRIORITY_FLOW_CONTROL
      | PRIORITY_QUEUE
      | QOS
      | QUEUE_SET
      | RANDOM_DETECT
      | RATE_LIMIT
      | RATE_MODE
      | RCV_QUEUE
      | ROUTE_CACHE
      | SCRAMBLE
      | SECURITY_LEVEL
      | SERIAL
      | SERVICE_MODULE
      | SERVICE_POLICY
      | SONET
      | SPANNING_TREE
      | SPEED
      | SNMP
      | SRR_QUEUE
      | STACK_MIB
      | STANDBY
      | STORM_CONTROL
      |
      (
         SWITCHPORT
         (
            EMPTY
            |
            (
               MODE PRIVATE_VLAN
            )
            | MONITOR
            | NONEGOTIATE
            | PORT_SECURITY
            |
            (
               TRUNK GROUP
            )
            | VOICE
            | VLAN
         )
      )
      | TAG_SWITCHING
      | TCAM
      | TRUST
      | TUNNEL
      | TX_QUEUE
      | UC_TX_QUEUE
      | UDLD
      | VPC
      | VRRP
      | WRR_QUEUE
      | X25
      | XCONNECT
   ) ~NEWLINE* NEWLINE
;

preempt_stanza
:
   PREEMPT NEWLINE
;

priority_stanza
:
   PRIORITY value = DEC NEWLINE
;

shutdown_if_stanza
:
   NO? SHUTDOWN NEWLINE
;

switchport_access_if_stanza
:
   SWITCHPORT ACCESS VLAN vlan = DEC NEWLINE
;

switchport_mode_access_stanza
:
   SWITCHPORT MODE ACCESS NEWLINE
;

switchport_mode_dynamic_auto_stanza
:
   SWITCHPORT MODE DYNAMIC AUTO NEWLINE
;

switchport_mode_dynamic_desirable_stanza
:
   SWITCHPORT MODE DYNAMIC DESIRABLE NEWLINE
;

switchport_mode_trunk_stanza
//@init { System.out.println("switchport mode trunk"); }
:
   SWITCHPORT MODE TRUNK NEWLINE
;

switchport_trunk_allowed_if_stanza
:
   SWITCHPORT TRUNK ALLOWED VLAN ADD? r = range NEWLINE
;

switchport_trunk_encapsulation_if_stanza
:
   SWITCHPORT TRUNK ENCAPSULATION e = switchport_trunk_encapsulation NEWLINE
;

switchport_trunk_native_if_stanza
:
   SWITCHPORT TRUNK NATIVE VLAN vlan = DEC NEWLINE
;

vrf_forwarding_if_stanza
@after{ System.out.println("VRF reference, not hanled yet."); }
:
   VRF FORWARDING name=~NEWLINE NEWLINE
;

vrf_member_if_stanza
@after{ System.out.println("VRF reference, not hanled yet."); }
:
   VRF MEMBER name=~NEWLINE NEWLINE
;

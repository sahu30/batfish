parser grammar CiscoGrammar;

import
CiscoGrammarCommonParser, CiscoGrammar_acl, CiscoGrammar_bgp, CiscoGrammar_interface, CiscoGrammar_ospf, CiscoGrammar_rip, CiscoGrammar_routemap;

options {
//  superClass = 'batfish.grammar.BatfishParser';
   tokenVocab = CiscoGrammarCommonLexer;
}

@header {
package batfish.grammar.cisco;

import java.util.HashSet;
import java.util.Set;
import java.util.*;

import batfish.representation.*;
}

@members {

   public complexityUtil comp = new complexityUtil();
   // intra
   void enterStanza(String type){
      comp.enterStanza(type);
   }
   void exitStanza(String name){
      comp.exitStanza(name);
   }
   
   void addStanzaReference(String type, String name){
      comp.addStanzaReference(type, name);
   }
   
   // inter, bgp
   void enterBGP(String as){
      comp.enterBGP(as);
   }
   
   void exitBGP(){
      comp.exitBGP();
   }
   
   void enterTemplate(String name){
      comp.enterTemplate(name);
   }
   
   void exitTemplate(){
      comp.exitTemplate();
   }
   
   void enterNeighbor(String nei){
      comp.enterNeighbor(nei);
   }
  
   void exitNeighbor(){
      comp.exitNeighbor();
   }
   
   void addTemplateAs(String as){
      comp.addTemplateAs(as);
   }
   
   void addBGPNeighbor(String neiAs, String ip){
      comp.addBGPNeighbor(neiAs, ip);
   }
   
   void addBGPNeighborByTemplate(String template){
      comp.addBGPNeighborByTemplate(template);
   }
   
   // inter, ospf
   void enterOSPF(String name){
      comp.enterOSPF(name);
   }
   
   void exitOSPF(){
      comp.exitOSPF();
   }
   
   void enterIface(String name){
      comp.enterIface(name);
   }
   void exitIface(){
      comp.exitIface();
   }
   
   void addIfaceSubnet(String prefix){
      comp.addIfaceSubnet(prefix);
   }
   
   void addIfaceSubnet(String ip, String mask){
      comp.addIfaceSubnet(ip, mask);
   } 
   
   void addOSPFSubnetInIface(){
      comp.addOSPFSubnetInIface();
   }
   void addOSPFIface(String name){
      comp.addOSPFIface(name);
   }

}


banner_stanza
:
   BANNER
   (
      (
         (
            ESCAPE_C ~ESCAPE_C* ESCAPE_C
         )
         |
         (
            POUND ~POUND* POUND
         )
         |
         (
            NEWLINE ~EOF_LITERAL* EOF_LITERAL
         )
      )
   ) NEWLINE
;

certificate_stanza
:
   CERTIFICATE ~QUIT* QUIT NEWLINE
;

cisco_configuration
:
   (
      sl += stanza
   )+ COLON? END? NEWLINE* EOF
;

ft_module_stanza
:
   FT GROUP ~NEWLINE* NEWLINE
   ft_module_substanza+
;

ft_module_substanza
:
   (
      FAILOVER
      | HEARTBEAT_TIME
      | PREEMPT
      | PRIORITY
   ) ~NEWLINE* NEWLINE
;

hostname_stanza
:
   HOSTNAME name = ~NEWLINE* NEWLINE
;

ip_default_gateway_stanza
:
   IP DEFAULT_GATEWAY gateway = IP_ADDRESS  
//   {System.out.println("ip_default_gateway_stanza");}
;

ip_route_stanza
:
   IP ROUTE
   (
      VRF vrf = ~NEWLINE
   )? ip_route_tail
;

ip_route_tail
:
   (
      (
         address = IP_ADDRESS mask = IP_ADDRESS
      )
      | prefix = IP_PREFIX
   )
   (
      nexthopip = IP_ADDRESS
      | nexthopint = interface_name
      | nexthopprefix = IP_PREFIX
      | distance = DEC
      |
      (
         TAG tag = DEC
      )
      | perm = PERMANENT
      |
      (
         TRACK track = DEC
      )
   )* ~NEWLINE* NEWLINE
;

ip_route_vrfc_stanza
:
   IP ROUTE ip_route_tail
;

macro_stanza
:
   MACRO ~NEWLINE* NEWLINE
;

module_stanza
:
   ft_module_stanza
   | natpool_module_stanza
   | policy_module_stanza
   | probe_module_stanza
   | serverfarm_module_stanza
   | sticky_module_stanza
   | vlan_module_stanza
   | vserver_module_stanza
   | xml_config_module_stanza
;

natpool_module_stanza
:
   NATPOOL ~NEWLINE* NEWLINE
;

no_ip_access_list_stanza
:
   NO IP ACCESS_LIST ~NEWLINE* NEWLINE
;

null_block_stanza
:
   NO?
   (
      AAA
      | ARCHIVE
      | ATM
      | BASH
      | CLI
      | CONTROL_PLANE
      | CONTROLLER
      | COPY
      |
      (
         CRYPTO
         (
            (
               ISAKMP
               (
                  KEY
                  | PEER
                  | POLICY
                  | PROFILE
               )
            )
            | KEY
            | KEYRING
            | MAP
            | PKI
         )
      )
      | DIAL_PEER
      | EVENT_HANDLER
      |
      (
         FLOW
         (
            EXPORTER
            | MONITOR
            | RECORD
         )
      )
      | GATEWAY
      | GROUP_POLICY
      |
      (
         IP
         (
            (
               ACCESS_LIST LOGGING
            )
            | ACCOUNTING_LIST
            | DECAP_GROUP
            | DHCP 
//            { System.out.println("IP DHCP in null standalone stanza"); }
            | FLOW_TOP_TALKERS
            | INSPECT
            | POLICY_LIST
            | SLA
            | SOURCE
            | VIRTUAL_ROUTER
            |
            (
               VRF ~( FORWARDING | NEWLINE )
            )
         )
      )
      | IPC
      |
      (
         IPV6 ACCESS_LIST
      )
      | L2TP_CLASS
      | LINE
      | MANAGEMENT
      | MAP_CLASS
      | MAP_LIST
      | MLAG
      | NO_BANNER
      | OPENFLOW
      | PLAT
      | POLICY_MAP
      | PSEUDOWIRE_CLASS
      | REDUNDANCY
      | ROLE
      |
      (
         SCCP CCM GROUP
      )
      | SPANNING_TREE
      |
      (
         STCAPP
         (
            FEATURE
            | SUPPLEMENTARY_SERVICES
         )
      )
      | TERMINAL
      | VDC
      |
      (
         VLAN DEC
      )
      | VOICE
      | VOICE_PORT
      | VPC
      | VPDN_GROUP
      |
      (
         VRF DEFINITION
      )
   ) ~NEWLINE* NEWLINE
   (
      null_block_substanza
      | description_line
   )*
;

null_block_substanza
:
   (
      NO?
      (
         ABSOLUTE_TIMEOUT
         | ACCEPT_DIALIN
         | ACCESS_CLASS
         | ACCOUNTING_SERVER_GROUP
         | ACTION
         | ACTIVATION_CHARACTER
         | ADDRESS_FAMILY
         | ADDRESS_POOL
         | ADMINISTRATIVE_WEIGHT
         | AESA
         | ANYCONNECT
         | ASSOCIATE
         | ASSOCIATION
         | AUTHENTICATION
         | AUTHENTICATION_SERVER_GROUP
         | AUTHORIZATION
         | AUTHORIZATION_REQUIRED
         | AUTHORIZATION_SERVER_GROUP
         | AUTO_SYNC
         | AUTOSELECT
         | BACKGROUND_ROUTES_ENABLE
         | BACKUPCRF
         | BANDWIDTH
         | BANNER
         | BIND
         | BRIDGE
         | CABLELENGTH
         | CACHE
         | CACHE_TIMEOUT
         | CALL
         | CALLER_ID
         | CAS_CUSTOM
         | CERTIFICATE
         | CHANNEL_GROUP
         | CHANNELIZED
         | CLASS
         | CLOCK
         | COLLECT
         | CONFORM_ACTION
         | CONGESTION_CONTROL
         | CPTONE
         | CRL
         | CRYPTO
         | DATABITS
         | DBL
         | DEFAULT_ACTION
         | DEFAULT_DOMAIN
         | DEFAULT_GROUP_POLICY
         | DEFAULT_ROUTER
         | DELAY
         | DENY
         | DESCRIPTION
         | DESTINATION
         | DIAGNOSTIC
         | DNS_SERVER
         | DOMAIN_ID
         | DROP
         | DS0_GROUP
         | DOMAIN_NAME
         | ENCAPSULATION
         | ENROLLMENT
         | ESCAPE_CHARACTER
         | EXCEED_ACTION
         | EXEC
         | EXEC_TIMEOUT
         | EXIT
         | EXIT_ADDRESS_FAMILY
         | EXPORT_PROTOCOL
         | EXPORTER
         | FABRIC
         | FAIR_QUEUE
         | FALLBACK_DN
         | FILE_BROWSING
         | FILE_ENTRY
         | FLUSH_AT_ACTIVATION
         | FQDN
         | FRAMING
         | GROUP_ALIAS
         | GROUP_POLICY
         | GROUP_URL
         | HIDDEN
         | HIDDEN_SHARES
         | HIDEKEYS
         | HIGH_AVAILABILITY
         | HISTORY
         | IDLE_TIMEOUT
         | INSPECT
         | INSTANCE
         | ( INTERFACE  POLICY)
         |
         (
            (
               IP
               | IPV6
            )
            (
               ACCESS_CLASS
               | ACCESS_GROUP
               | FLOW
               | VRF
            )
         )
         | IPSEC_UDP
         | IPX
         | IPV6_ADDRESS_POOL
         | ISAKMP
         | KEEPALIVE_ENABLE
         | KEYPAIR
         | KEYRING
         | L2TP
         | LENGTH
         | LIMIT_RESOURCE
         | LINE
         | LINECODE
         | LLDP
         | LOCAL_INTERFACE
         | LOCAL_IP
         | LOCAL_PORT
         | LOCATION
         | LOG
         | LOGGING
         | LOGIN
         | MAIN_CPU
         | MATCH
         | MAXIMUM
         | MESSAGE_LENGTH
         | MODE
         | MODEM
         | MTU
         | NAME
         | NEGOTIATE
         | NETWORK
         | NODE
         | NOTIFY
         | PARAMETERS
         | PARENT
         | PASSWORD
         | PASSWORD_STORAGE
         | PATH_JITTER
         | PAUSE
         | PEER_ADDRESS
         | PEER_CONFIG_CHECK_BYPASS
         | PEER_GATEWAY
         | PEER_KEEPALIVE
         | PEER_LINK
         | PERMIT
         | PICKUP
         | POLICE
         | POLICY_MAP
         | PORT
         | PREFIX
         | PRI_GROUP
         | PRIORITY
         | PRIVILEGE
         | PROTOCOL
         | QUEUE_BUFFERS
         | QUEUE_LIMIT
         | RANDOM_DETECT
         | RD
         | RECORD
         | RECORD_ENTRY
         | REDISTRIBUTE
         | RELOAD_DELAY
         | REMARK
         | REMOTE_IP
         | REMOTE_PORT
         | REMOTE_SPAN
         | REMOVED
         | RETRANSMIT
         | REVERSE_ROUTE
         | REVISION
         | RING
         | ROLE
         | ROTARY
         | ROUTE_TARGET
         | RULE
         | SCHEME
         | SEQUENCE
         | SERVER
         | SERVER_PRIVATE
         | SERVICE
         | SERVICE_POLICY
         | SERVICE_TYPE
         | SESSION_DISCONNECT_WARNING
         | SESSION_LIMIT
         | SESSION_TIMEOUT
         | SET
         | SHAPE
         | SHUTDOWN
         | SINGLE_ROUTER_MODE
         | SORT_BY
         | SOURCE
         | SPANNING_TREE
         | SPEED
         | SPLIT_TUNNEL_NETWORK_LIST
         | SPLIT_TUNNEL_POLICY
         | STOPBITS
         | STP
         | SUBJECT_NAME
         | SWITCHBACK
         | SWITCHPORT
         | SYNC
         | TB_VLAN1
         | TB_VLAN2
         | TERMINAL_TYPE
         | TIMEOUTS
         | TIMER
         | TIMING
         | TOP
         | TRANSPORT
         | TRIGGER
         | TRUNK
         | TUNNEL
         | TUNNEL_GROUP
         | USE_VRF
         | VIOLATE_ACTION
         | VIRTUAL_TEMPLATE
         | VPN_FILTER
         | VPN_IDLE_TIMEOUT
         | VPN_TUNNEL_PROTOCOL
         | VRF
         | WEBVPN
         | WINS_SERVER
         | WITHOUT_CSD
      )
      (
         remaining_tokens += ~NEWLINE
      )* NEWLINE
   )
;

null_standalone_stanza
:
   (
      NO
   )?
   (
      AAA
      | AAA_SERVER
      | ABSOLUTE_TIMEOUT
      | ACCESS_GROUP
      |
      (
         ACCESS_LIST
         (
            (
               DEC REMARK
            )
            | VARIABLE
         )
      )
      | ACCOUNTING_PORT
      | ACTION
      | ACTIVE
      | ADDRESS
      | ALERT_GROUP
      | ALIAS
      | ANYCONNECT
      | ANYCONNECT_ESSENTIALS
      | AP
      | AQM_REGISTER_FNF
      | ARP
      | ASA
      | ASDM
      | ASSOCIATE
      | ASYNC_BOOTP
      | AUTHENTICATION
      | AUTHENTICATION_PORT
      | AUTO
      | BOOT
      | BOOT_END_MARKER
      | BOOT_START_MARKER
      | BRIDGE
      | CALL
      | CALL_HOME
      | CARD
      | CCM_MANAGER
      | CDP
      | CFS
      | CHAT_SCRIPT
      | CIPC
      | CLASS_MAP
      | CLOCK
      | CLUSTER
      | CNS
      | CODEC
      | CONFIG_REGISTER
      | CONSOLE
      | CONTACT_EMAIL_ADDR
      | CRL
      |
      (
         CRYPTO
         (
            CA
            | IPSEC
            |
            (
               ISAKMP
               (
                  ENABLE
                  | KEY
                  | INVALID_SPI_RECOVERY
                  | KEEPALIVE
               )
            )
         )
      )
      | CTL_FILE
      | CTS
      | DEFAULT
      | DESCRIPTION
      | DESTINATION
      | DEVICE_SENSOR
      | DHCPD
      | DIAGNOSTIC
      | DIALER_LIST
      | DISABLE
      | DNS
      | DNS_GUARD
      | DOMAIN_NAME
      | DOT11
      | DSP
      | DSPFARM
      | DSS
      | DYNAMIC_ACCESS_POLICY_RECORD
      | ENABLE
      | ENCR
      | ENCRYPTION
      | ENROLLMENT
      | ENVIRONMENT
      | ERRDISABLE
      | ESCAPE_CHARACTER
      | EVENT
      | EXCEPTION
      | EXEC
      | FABRIC
      | FACILITY_ALARM
      | FAILOVER
      | FEATURE
      | FILE
      | FIREWALL
      | FIRMWARE
      | FLOWCONTROL
      | FRAME_RELAY
      | FREQUENCY
      | FQDN
      | FTP
      | FTP_SERVER
      | GATEKEEPER
      | GROUP
      | GROUP_OBJECT
      | HARDWARE
      | HASH
      | HISTORY
      | HOST
      | HTTP
      | HW_MODULE
      | ICMP
      | ICMP_ECHO
      | ICMP_OBJECT
      | IDENTITY
      | INACTIVITY_TIMER
      |
      (
         IP
         (
            ADDRESS_POOL
            | ADMISSION
            | ALIAS
            | ARP
            | AUDIT
            | AUTH_PROXY
            | BOOTP
            | BGP_COMMUNITY
            | CEF
            | CLASSLESS
            | DEFAULT_NETWORK
            | DEVICE
            | DOMAIN
            | DOMAIN_LIST
            | DOMAIN_LOOKUP
            | DOMAIN_NAME
            | DVMRP
            | EXTCOMMUNITY_LIST
            | FINGER
            | FLOW_CACHE
            | FLOW_EXPORT
            | FORWARD_PROTOCOL
            | FTP
            | GRATUITOUS_ARPS
            | HOST
            | HOST_ROUTING
            | HTTP
            | ICMP
            | IGMP
            | LOAD_SHARING
            | LOCAL
            | MFIB
            | MROUTE
            | MSDP
            | MULTICAST
            | MULTICAST_ROUTING
            | NAME_SERVER
            | NAT
            |
            (
               OSPF NAME_LOOKUP
            )
            | PIM
            | RADIUS
            | RCMD
            | ROUTING //might want to use this eventually

            | SAP
            | SCP
            | SLA
            | SOURCE_ROUTE
            | SSH
            | SUBNET_ZERO
            | TACACS
            | TCP
            | TELNET
            | TFTP
            | VERIFY
            | VRF
         )
      )
      | IP_ADDRESS_LITERAL
      |
      (
         IPV6
         (
            CEF
            | HOST
            | LOCAL
            | MFIB
            | MFIB_MODE
            | MLD
            | MULTICAST
            | MULTICAST_ROUTING
            | ND
            |
            (
               OSPF NAME_LOOKUP
            )
            | PIM
            | ROUTE
            | SOURCE_ROUTE
            | UNICAST_ROUTING
         )
      )
      | ISDN
      | KEEPOUT
      | KEYPAIR
      | KEYRING
      | LDAP_BASE_DN
      | LDAP_LOGIN
      | LDAP_LOGIN_DN
      | LDAP_NAMING_ATTRIBUTE
      | LDAP_SCOPE
      | LICENSE
      | LIFETIME
      | LLDP
      | LOCATION
      | LOGGING
//      | ( MAC x=~ACCESS_LIST  { System.out.println("MAC in null_standalone_stanza, x is |"+_localctx.x.getText()+"|"); })
      | MAC
      | MAC_ADDRESS_TABLE
      | MAIL_SERVER
      | MATCH
      | MAXIMUM
      | MEDIA_TERMINATION
      | MEMORY_SIZE
      | MGCP
      | MICROCODE
      | MLS
      | MODE
      | MODEM
      | MODULE
      | MONITOR
      | MPLS
      | MTA
      | MTU
      | MULTILINK
      | MVR
      | NAME_SERVER
      | NAME
      | NAMES
      | NAT
      | NAT_CONTROL
      | NETCONF
      | NETWORK_OBJECT
      | NETWORK_CLOCK_PARTICIPATE
      | NETWORK_CLOCK_SELECT
      | NTP
      | OBJECT
      | OBJECT_GROUP
      | OWNER
      | PAGER
      | PARSER
      | PARTICIPATE
      | PASSWORD
      | PERCENT
      | PHONE_PROXY
      | PLATFORM
      | PORT_CHANNEL
      | PORT_OBJECT
      | POWER
      | POWEROFF
      | PRE_SHARED_KEY
      | PRIORITY
      | PRIORITY_QUEUE
      | PRIVILEGE
      | PROCESS
      | PROFILE
      | PROMPT
      | PROTOCOL_OBJECT
      | QOS
      | QUIT
      | RADIUS_COMMON_PW
      | RADIUS_SERVER
      | RD
      | RECORD_ENTRY
      | REDIRECT_FQDN
      | RESOURCE
      | RESOURCE_POOL
      | REVERSE_ROUTE
      | REVOCATION_CHECK
      | RMON
      | ROUTE
      | ROUTE_TARGET
      | RSAKEYPAIR
      | RTR
      | SAME_SECURITY_TRAFFIC
      |
      (
         SCCP
         (
            (
               CCM IP_ADDRESS
            )
            | LOCAL
         )
      )
      | SCHEDULE
      | SCHEDULER
      | SCRIPTING
      | SDM
      | SECURITY
      | SENDER
      | SERIAL_NUMBER
      | SERVER
      | SERVER_TYPE
      | SERVICE
      | SERVICE_POLICY
      | SET
      | SETUP
      | SFLOW
      | SHELL
      | SHUTDOWN
      | SMTP_SERVER
      | SNMP
      | SNMP_SERVER
      | SOURCE
      | SOURCE_INTERFACE
      | SOURCE_IP_ADDRESS
      | SPANNING_TREE
      | SPD
      | SPE
      | SPEED
      | STOPBITS
      | SSH
      | SSL
      | STATIC
      |
      (
         STCAPP
         (
            CCM_GROUP
         )
      )
      | SUBJECT_NAME
      | SUBNET
      | SUBSCRIBER
      | SUBSCRIBE_TO
      | SUBSCRIBE_TO_ALERT_GROUP
      | SWITCH
      | SWITCHNAME
      | SYSOPT
      | SYSTEM
      | TABLE_MAP
      | TACACS_SERVER
      | TAG
      | TAG_SWITCHING
      | TELNET
      | TEMPLATE
      | TFTP_SERVER
      | THREAT_DETECTION
      | TIMEOUT
      | TLS_PROXY
      | TRACK
      | TRANSLATE
      | TRANSPORT
      | TUNNEL_GROUP_LIST
      | TYPE
      | UDLD
      | UNABLE
      | UPGRADE
      | USER_IDENTITY
      | USE_VRF
      | USERNAME
      | VALIDATION_USAGE
      | VERSION
      |
      (
         VLAN
         (
            ACCESS_LOG
            | CONFIGURATION
            | DOT1Q
            | INTERNAL
         )
      )
      | VMPS
      | VPDN
      | VPN
      | VTP
      | VOICE_CARD
      | WEBVPN
      | WLAN
      | WSMA
      | X25
      | X29
      | XLATE
      | XML SERVER
      | XX_HIDE
   )
   (
      remaining_tokens += ~NEWLINE
   )* NEWLINE
;

null_stanza
:
   banner_stanza
   | certificate_stanza
   | macro_stanza
   | no_ip_access_list_stanza
   | null_block_stanza
   | null_standalone_stanza
   |
   (
      (
         | SCCP
         | STCAPP
      ) NEWLINE
   )
   | vrf_context_stanza
;

policy_module_stanza
:
   POLICY ~NEWLINE* NEWLINE
   policy_module_substanza+
;

policy_module_substanza
:
   (
      CLIENT_GROUP
      | SERVERFARM
      | STICKY_GROUP
   ) ~NEWLINE* NEWLINE
;

probe_module_stanza
:
   PROBE ~NEWLINE* NEWLINE
   probe_module_substanza+
;

probe_module_substanza
:
   (
      ADDRESS
      | EXPECT
      | FAILED
      | INTERVAL
      | NAME
      | OPEN
      | PORT
      | RECEIVE
      | REQUEST
      | RETRIES
   ) ~NEWLINE* NEWLINE
;

serverfarm_module_stanza
:
   SERVERFARM ~NEWLINE* NEWLINE
   serverfarm_module_substanza+
;

serverfarm_module_substanza
:
   (
      NO? INSERVICE
      | NO? NAT
      | PREDICTOR
      | PROBE
      | REAL
   ) ~NEWLINE* NEWLINE
;

stanza
:
   appletalk_access_list_stanza
   | arp_access_list_stanza
   | extended_access_list_stanza
   | hostname_stanza
   | interface_stanza
   | ip_as_path_access_list_stanza
   | ip_community_list_expanded_stanza
   | ip_community_list_standard_stanza
   | ip_default_gateway_stanza
   | ip_prefix_list_stanza
   | ip_route_stanza
   | ipv6_router_ospf_stanza
   | ipx_sap_access_list_stanza
   | mac_access_list_stanza
   | module_stanza
   | nexus_access_list_stanza
   | nexus_prefix_list_stanza
   | null_stanza
   | protocol_type_code_access_list_stanza
   | route_map_stanza
   | router_bgp_stanza
   | router_ospf_stanza
   | router_rip_stanza
   | standard_access_list_stanza
   | switching_mode_stanza
;

sticky_module_stanza
:
   STICKY ~NEWLINE* NEWLINE
;

switching_mode_stanza
:
   SWITCHING_MODE ~NEWLINE* NEWLINE
;

vlan_module_stanza
:
   VLAN DEC ( SERVER | CLIENT ) NEWLINE
   vlan_module_substanza+
;

vlan_module_substanza
:
   (
      ALIAS
      | GATEWAY
      | IP ADDRESS
      | ROUTE
   ) ~NEWLINE* NEWLINE
;

vrfc_stanza
:
   ip_route_vrfc_stanza
;

vrf_context_stanza
:
   VRF CONTEXT name = ~NEWLINE NEWLINE vrfc_stanza*
;

vserver_module_stanza
:
   VSERVER ~NEWLINE* NEWLINE
   vserver_module_substanza+
;

vserver_module_substanza
:
   (
      IDLE
      | INSERVICE
      | REPLICATE
      | PERSISTENT
      | SERVERFARM
      | SLB_POLICY
      | VIRTUAL
      | VLAN
   ) ~NEWLINE* NEWLINE
;

xml_config_module_stanza
:
   XML_CONFIG NEWLINE
   xml_config_module_substanza+
;

xml_config_module_substanza
:
   (
      CREDENTIALS
      | INSERVICE
      | VLAN
   ) ~NEWLINE* NEWLINE
;
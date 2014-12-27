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


	class subnet{
		public Ip ip, mask;
		public subnet(String prefix){
			String tokens[] = prefix.split("/");
			if(tokens.length != 2){
            System.out.println("subnet should be a prefix, but is an IP");
            assert false;
         }
			mask = new Ip(Long.parseLong(tokens[1]));
			ip = new Ip(tokens[0]);
		}
		public subnet(String _ip, String _mask){
			ip = new Ip(_ip);
			mask = new Ip(_mask);
		}
		@Override 
		public int hashCode(){
			return ip.hashCode()+mask.hashCode();
		}
		@Override 
		public boolean equals(Object obj){
			if(!(obj instanceof subnet)){
				return false;
			}
			subnet other = (subnet)obj;
			return ip==other.ip && mask == other.mask;
		}
      @Override
      public String toString(){
         return ip.toString()+"/"+mask.toString();
   
      }
	}
	
	class bgp{
		public String asnum;
		public Set<String> ibgp_neighbors;
      public Set<String> ebgp_neighbors;
      public Map<String, String> template_to_as;

      public Ip current_neighbor;
      public String current_template;
		public bgp(String as){
			asnum = as;
			ibgp_neighbors = new HashSet<String>();
         ebgp_neighbors = new HashSet<String>();
         template_to_as = new HashMap<String, String>();

         current_neighbor=null;
         current_template=null;
		}
		public void addNeighbor(String as){
         if(as.equals(asnum)){
            // ibpg, please check
            System.out.println("add an ibgp, check. bgp as: "+asnum+", neighbor as: "+as);
         }
         else{
            ebgp_neighbors.add(as);
         }
//			neighbors.add(as);
		}
      @Override
      public String toString(){
         String out="";
         out+=asnum+"(ebgp):";
         for(String n: ebgp_neighbors){
            out+=n+" ";
         }
         out+=", (ibgp):";
         for(String n: ibgp_neighbors){
            out+=n+" ";
         }
         return out;
      }
	}

   public void enterTemplate(String name){
      if(bgp_router==null){
         System.out.println("enter Tamplate with bgp_router to be null");
         assert false;
      }
      if(bgp_router.current_template!=null){
         System.out.println("enter a template with template not null");
         assert false;
      }
      bgp_router.current_template = name;
   }
   
   public void exitTemplate(){
      if(bgp_router==null){
         System.out.println("exit template with bgp_router null");
         assert false;
      }
      if(bgp_router.current_template==null){
         System.out.println("exit template with current_teample null");
         assert false;
      }
      bgp_router.current_template = null;
   }

   public void addTemplateAs(String as){
      if(bgp_router==null){
         System.out.println("add template as with bgp_router null");
         assert false;
      }
      if(bgp_router.current_template==null){
         System.out.println("add template with current_template as null");
         assert false;
      }
      bgp_router.template_to_as.put(bgp_router.current_template, as);
   }

   public void enterNeighbor(String _ip){
      if(bgp_router==null){
         System.out.println("enter neighbor with bgp_router null");
         assert false;
      }
      if(bgp_router.current_neighbor!=null){
         System.out.println("enter neighbor with current_neighbor not null");
         assert false;
      }
      bgp_router.current_neighbor = new Ip(_ip);
   }

   public void exitNeighbor(){
      if( bgp_router==null){
         System.out.println("exit neighbor with bgp_router null");
         assert false;
      }
      if(bgp_router.current_neighbor==null){
         System.out.println("exit neighbor with current_neighbor null");
         assert false;
      }
      bgp_router.current_neighbor = null;
   }

   private void printBGP(){
      if(bgp_router==null) return;
         System.out.println(bgp_router);
   }
   private void printOSPF(){
      String out = "";
      for(subnet s: subnet_of_ospf_iface){
         out+=s.toString()+" ";
      }
      System.out.println(out);
   }
	
	Map<String, subnet> iface_to_subnet = new HashMap<String, subnet>();
//	Map<String, String> ospf_to_iface = new HashMap<String, String>();
//	Map<String, subnet> ospf_to_subnet = new HashMap<String, subnet>();
	public Set<subnet> subnet_of_ospf_iface = new HashSet<subnet>();
   public Set<String> iface_of_ospf = new HashSet<String> ();
	
	public bgp bgp_router = null;
   public String ospf_router = null;
   public String iface = null;
   public void enterBGP(String as){
      if(bgp_router!=null){
         System.out.println("enterBGP with bgp_router not null");
         assert false;
      }
      bgp_router = new bgp(as);
   }

   public void exitBGP(){
      if(bgp_router==null){
         System.out.println("exitBGP with bgp_router null");
         assert false;
      }
   //   bgp_router = null;
   }

   public void enterOSPF(String name){
      if(ospf_router!=null){
         System.out.println("enterOSPF with ospf_router not null");
         assert false;
      }
      ospf_router = name;
   }

   public void exitOSPF(){
      if(ospf_router==null){
         System.out.println("exitOSPF with osfp_router null");
         assert false;
      }
      ospf_router = null;
   }

   public void enterIface(String name){
      assert iface ==null;
      iface = name;
   }

   public void exitIface(){
      assert iface !=null;
      iface = null;
   }

   public void addBGPNeighbor(String neiAs){
      assert bgp_router!=null;
      bgp_router.addNeighbor(neiAs);
   }

   public void addBGPNeighborByTemplate(String temp_name){
      assert bgp_router!=null;
      String as = bgp_router.template_to_as.get(temp_name);
      assert as!=null;
      addBGPNeighbor(as);
   }

   public void addOSPFIface(String name){
      assert ospf_router!=null;
      iface_of_ospf.add(name);
//      ospf_to_iface.put(osfp_router, 
   }

   public void addIfaceSubnet(String prefix){
      assert iface != null;
      subnet s = new subnet(prefix);
      iface_to_subnet.put(iface, s);
   }

   public void addIfaceSubnet(String ip, String mask){
      assert iface!=null;
      subnet s =new subnet(ip, mask);
      iface_to_subnet.put(iface, s);
   }
	
	public void ProcessOspfReferences(){
		subnet_of_ospf_iface.clear();
		for(String iface: iface_of_ospf){
			subnet sub = iface_to_subnet.get(iface);
			if( sub != null){
//				ospf_to_subnet.put(ospf, sub);
				subnet_of_ospf_iface.add(sub);
			}
		}
	}
	
	public boolean BgpReferenced(CiscoGrammar other){
      System.out.println("===== BGP Reference ====");
//      printBGP();
//      other.printBGP();
		if(bgp_router==null || other.bgp_router==null) 
			return false;

      if(bgp_router.asnum.equals(other.bgp_router.asnum)){  // ibgp
         System.out.println("this is an ibgp example, look into it");
         return false;
      }
      else{ // ebgp
/*
for(String n: bgp_router.ebgp_neighbors){
   System.out.println("*"+n+"*");
}
System.out.println("++"+other.bgp_router.asnum+"++");
*/
		   return bgp_router.ebgp_neighbors.contains(other.bgp_router.asnum);
      }
	}
	
	public boolean OspfReferenced(CiscoGrammar other){
      System.out.println("==== OSPF Reference ====");
//      printOSPF();
//      other.printOSPF();
		for(subnet sub: subnet_of_ospf_iface){
			if(other.subnet_of_ospf_iface.contains(sub))
				return true;
		}
		return false;
	}
	
	enum stanza_type{IFACE, ACL, ROUTEMAP, ROUTER};
	class stanza{
		public stanza_type type;
		public String name;
		List<reference_to> references;
		public stanza(stanza_type t){
			type = t;
			name = null;
			references = new ArrayList<reference_to>();
		}
		public stanza(stanza_type t, String n){
			type = t;
			name = n;
		}
		public void AddReference(stanza_type type, String name){
			references.add(new reference_to(type, name));
		}
		@Override
		public int hashCode(){
			return type.hashCode()+name.hashCode();
		}
		@Override
		public boolean equals(Object obj){
			if( obj  instanceof stanza){
				stanza stanza_obj = (stanza) obj;
				return stanza_obj.type == this.type && stanza_obj.name.equals(this.name);
			}
			return false;
		}
		@Override
		public String toString(){
			return "stanza:"+name+"("+type.name()+")";
		}
	}
	class reference_to{
		public stanza_type type;
		public String name;
		public reference_to(stanza_type t, String n){
			type = t;
			name = n;
		}
	}
	

	Set<stanza> stanzas = new HashSet<stanza>();
	stanza current = null;
	
	public Integer getComplexity(){
//      return stanzas.size();
		int totalReferences=0;
		for(stanza s: stanzas){
			List<reference_to> references = s.references;
			for(reference_to to: references){
				stanza dst = new stanza(to.type, to.name);
				if(!stanzas.contains(dst)){
					System.out.println(s+" references to a non-existing stanza: "+dst);
				}
				else{
					totalReferences++;
				}
			}
		}
		return totalReferences;
	}

	
	private void enterStanza(stanza_type t){
		if(current !=null){
			System.out.println("enter a new stanza without exiting the previous, please check. "+current);
		}
		current = new stanza(t);
	}
	private void exitStanza(String name){
		if(current == null){
			System.out.println("exit a null stanza, please check.");
		}
		current.name = name;
		if(stanzas.contains(current)){
			System.out.println("duplicated stanzas, please check: "+current);
		}
		else{
			stanzas.add(current);
		}
		current = null;
	}
	private void AddReference(stanza_type type, String name) {
		current.AddReference(type, name);
//      System.out.println(current.type.name()+" references to "+type.name()+":"+name);
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

switching_mode_stanza
:
   SWITCHING_MODE ~NEWLINE* NEWLINE
;

vrfc_stanza
:
   ip_route_vrfc_stanza
;

vrf_context_stanza
:
   VRF CONTEXT name = ~NEWLINE NEWLINE vrfc_stanza*
;


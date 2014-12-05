package batfish.grammar.cisco.controlplane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import batfish.grammar.cisco.*;
import batfish.grammar.cisco.CiscoGrammar.Access_list_actionContext;
import batfish.grammar.cisco.CiscoGrammar.Access_list_ip_rangeContext;
import batfish.grammar.cisco.CiscoGrammar.Activate_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Address_family_footerContext;
import batfish.grammar.cisco.CiscoGrammar.Address_family_headerContext;
import batfish.grammar.cisco.CiscoGrammar.Address_family_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Aggregate_address_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Allowas_in_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Always_compare_med_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Appletalk_access_list_null_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Appletalk_access_list_numbered_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Appletalk_access_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Area_ipv6_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Area_nssa_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Auto_summary_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Banner_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Bgp_listen_range_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Certificate_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Cisco_configurationContext;
import batfish.grammar.cisco.CiscoGrammar.Cluster_id_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Cluster_id_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.CommunityContext;
import batfish.grammar.cisco.CiscoGrammar.Default_information_ipv6_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Default_information_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Default_metric_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Default_originate_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Description_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Description_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Description_lineContext;
import batfish.grammar.cisco.CiscoGrammar.Distance_rr_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Distribute_list_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Distribute_list_rr_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ebgp_multihop_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Exact_matchContext;
import batfish.grammar.cisco.CiscoGrammar.Exit_lineContext;
import batfish.grammar.cisco.CiscoGrammar.Extended_access_list_additional_featureContext;
import batfish.grammar.cisco.CiscoGrammar.Extended_access_list_named_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Extended_access_list_null_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Extended_access_list_numbered_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Extended_access_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Extended_access_list_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Filter_list_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Hostname_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Hsrp_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Hsrp_stanza_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Hsrpc_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.If_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Interface_nameContext;
import batfish.grammar.cisco.CiscoGrammar.Interface_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Interface_stanza_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_access_group_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_address_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_address_secondary_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_address_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_as_path_access_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_as_path_access_list_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_as_path_numbered_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_community_list_expanded_named_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_community_list_expanded_numbered_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_community_list_expanded_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_community_list_expanded_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_community_list_standard_named_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_community_list_standard_numbered_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_community_list_standard_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_community_list_standard_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_default_gateway_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_ospf_cost_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_ospf_dead_interval_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_ospf_dead_interval_minimal_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_policy_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_prefix_list_named_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_prefix_list_null_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_prefix_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_prefix_list_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_route_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_route_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Ip_route_vrfc_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ipv6_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ipv6_router_ospf_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ipx_sap_access_list_null_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Ipx_sap_access_list_numbered_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ipx_sap_access_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Log_adjacency_changes_ipv6_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Macro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Match_as_path_access_list_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Match_community_list_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Match_ip_access_list_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Match_ip_prefix_list_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Match_ipv6_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Match_length_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Match_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Match_tag_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Maximum_paths_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Maximum_prefix_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Neighbor_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Network6_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Network_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Network_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Network_rr_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Next_hop_self_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Nexus_access_list_null_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Nexus_access_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Nexus_access_list_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Nexus_neighbor_address_familyContext;
import batfish.grammar.cisco.CiscoGrammar.Nexus_neighbor_inheritContext;
import batfish.grammar.cisco.CiscoGrammar.Nexus_neighbor_no_shutdownContext;
import batfish.grammar.cisco.CiscoGrammar.Nexus_neighbor_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Nexus_prefix_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Nexus_vrf_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.No_ip_access_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.No_ip_address_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.No_neighbor_activate_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.No_neighbor_shutdown_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.No_redistribute_connected_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Null_block_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_block_substanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_ipv6_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_standalone_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_standalone_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_standalone_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Null_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Passive_interface_default_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Passive_interface_ipv6_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Passive_interface_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Passive_interface_rr_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Peer_group_assignment_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Peer_group_creation_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.PortContext;
import batfish.grammar.cisco.CiscoGrammar.Port_specifierContext;
import batfish.grammar.cisco.CiscoGrammar.Preempt_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Prefix_list_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Priority_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.ProtocolContext;
import batfish.grammar.cisco.CiscoGrammar.Protocol_type_code_access_list_null_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Protocol_type_code_access_list_numbered_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Protocol_type_code_access_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.RangeContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_aggregate_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_bgp_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_connected_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_connected_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_ipv6_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_ospf_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_rip_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_rr_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_static_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Redistribute_static_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Remote_as_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Remove_private_as_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Route_map_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Route_map_named_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Route_map_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Route_map_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Route_map_tail_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Route_reflector_client_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Router_bgp_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Router_id_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Router_id_ipv6_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Router_id_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Router_id_ro_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Router_ospf_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Router_ospf_stanza_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Router_rip_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Rr_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Send_community_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Set_as_path_prepend_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_comm_list_delete_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_community_additive_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_community_none_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_community_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_extcomm_list_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_interface_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_ip_df_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_ipv6_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_local_preference_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_metric_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_metric_type_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_mpls_label_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_next_hop_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_origin_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Set_weight_rm_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Shutdown_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Shutdown_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Standard_access_list_named_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Standard_access_list_null_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Standard_access_list_numbered_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Standard_access_list_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Standard_access_list_tailContext;
import batfish.grammar.cisco.CiscoGrammar.StanzaContext;
import batfish.grammar.cisco.CiscoGrammar.SubrangeContext;
import batfish.grammar.cisco.CiscoGrammar.Switching_mode_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Switchport_access_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Switchport_mode_access_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Switchport_mode_dynamic_auto_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Switchport_mode_dynamic_desirable_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Switchport_mode_trunk_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Switchport_trunk_allowed_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Switchport_trunk_encapsulationContext;
import batfish.grammar.cisco.CiscoGrammar.Switchport_trunk_encapsulation_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Switchport_trunk_native_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Template_peer_address_familyContext;
import batfish.grammar.cisco.CiscoGrammar.Template_peer_rb_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Update_source_bgp_tailContext;
import batfish.grammar.cisco.CiscoGrammar.Vrf_context_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Vrf_forwarding_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Vrf_member_if_stanzaContext;
import batfish.grammar.cisco.CiscoGrammar.Vrfc_stanzaContext;

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
public class CiscoControlPlaneComplexity implements CiscoGrammarListener{
	Set<stanza> stanzas = new HashSet<stanza>();
	stanza current = null;
	
	public Integer getComplexit(){
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
	}

	@Override
	public void enterEveryRule(ParserRuleContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitEveryRule(ParserRuleContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitErrorNode(ErrorNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTerminal(TerminalNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_ospf_dead_interval_if_stanza(
			Ip_ospf_dead_interval_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_ospf_dead_interval_if_stanza(
			Ip_ospf_dead_interval_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPassive_interface_default_ro_stanza(
			Passive_interface_default_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPassive_interface_default_ro_stanza(
			Passive_interface_default_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRouter_id_ro_stanza(Router_id_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRouter_id_ro_stanza(Router_id_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNetwork_bgp_tail(Network_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNetwork_bgp_tail(Network_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMaximum_paths_ro_stanza(Maximum_paths_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMaximum_paths_ro_stanza(Maximum_paths_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterLog_adjacency_changes_ipv6_ro_stanza(
			Log_adjacency_changes_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitLog_adjacency_changes_ipv6_ro_stanza(
			Log_adjacency_changes_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDefault_information_ipv6_ro_stanza(
			Default_information_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDefault_information_ipv6_ro_stanza(
			Default_information_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAggregate_address_bgp_tail(
			Aggregate_address_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAggregate_address_bgp_tail(
			Aggregate_address_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_ipv6_ro_stanza(Null_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_ipv6_ro_stanza(Null_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAccess_list_action(Access_list_actionContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAccess_list_action(Access_list_actionContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterHsrp_stanza_tail(Hsrp_stanza_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitHsrp_stanza_tail(Hsrp_stanza_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_route_tail(Ip_route_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_route_tail(Ip_route_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_community_list_standard_numbered_stanza(
			Ip_community_list_standard_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_community_list_standard_numbered_stanza(
			Ip_community_list_standard_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_if_stanza(Null_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_if_stanza(Null_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMatch_community_list_rm_stanza(
			Match_community_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMatch_community_list_rm_stanza(
			Match_community_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterStanza(StanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitStanza(StanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPeer_group_creation_rb_stanza(
			Peer_group_creation_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPeer_group_creation_rb_stanza(
			Peer_group_creation_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_community_none_rm_stanza(
			Set_community_none_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_community_none_rm_stanza(
			Set_community_none_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_next_hop_rm_stanza(Set_next_hop_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_next_hop_rm_stanza(Set_next_hop_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMatch_tag_rm_stanza(Match_tag_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMatch_tag_rm_stanza(Match_tag_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_metric_rm_stanza(Set_metric_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_metric_rm_stanza(Set_metric_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_aggregate_bgp_tail(
			Redistribute_aggregate_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_aggregate_bgp_tail(
			Redistribute_aggregate_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_community_list_standard_named_stanza(
			Ip_community_list_standard_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_community_list_standard_named_stanza(
			Ip_community_list_standard_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterVrfc_stanza(Vrfc_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitVrfc_stanza(Vrfc_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAppletalk_access_list_null_tail(
			Appletalk_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAppletalk_access_list_null_tail(
			Appletalk_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_as_path_access_list_tail(
			Ip_as_path_access_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_as_path_access_list_tail(
			Ip_as_path_access_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNetwork_ro_stanza(Network_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNetwork_ro_stanza(Network_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMaximum_prefix_bgp_tail(Maximum_prefix_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMaximum_prefix_bgp_tail(Maximum_prefix_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterInterface_name(Interface_nameContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitInterface_name(Interface_nameContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitchport_trunk_encapsulation(
			Switchport_trunk_encapsulationContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitchport_trunk_encapsulation(
			Switchport_trunk_encapsulationContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterProtocol_type_code_access_list_null_tail(
			Protocol_type_code_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitProtocol_type_code_access_list_null_tail(
			Protocol_type_code_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_community_list_expanded_numbered_stanza(
			Ip_community_list_expanded_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_community_list_expanded_numbered_stanza(
			Ip_community_list_expanded_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterShutdown_if_stanza(Shutdown_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitShutdown_if_stanza(Shutdown_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterExit_line(Exit_lineContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitExit_line(Exit_lineContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMatch_ip_prefix_list_rm_stanza(
			Match_ip_prefix_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMatch_ip_prefix_list_rm_stanza(
			Match_ip_prefix_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterHsrp_stanza(Hsrp_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitHsrp_stanza(Hsrp_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_community_list_standard_stanza(
			Ip_community_list_standard_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_community_list_standard_stanza(
			Ip_community_list_standard_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIf_stanza(If_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIf_stanza(If_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_ro_stanza(Null_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_ro_stanza(Null_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_ipv6_rm_stanza(Set_ipv6_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_ipv6_rm_stanza(Set_ipv6_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_community_list_expanded_named_stanza(
			Ip_community_list_expanded_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_community_list_expanded_named_stanza(
			Ip_community_list_expanded_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAddress_family_header(Address_family_headerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAddress_family_header(Address_family_headerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRoute_map_stanza(Route_map_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRoute_map_stanza(Route_map_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMatch_as_path_access_list_rm_stanza(
			Match_as_path_access_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMatch_as_path_access_list_rm_stanza(
			Match_as_path_access_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAddress_family_rb_stanza(
			Address_family_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAddress_family_rb_stanza(Address_family_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRo_stanza(Ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRo_stanza(Ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRouter_rip_stanza(Router_rip_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRouter_rip_stanza(Router_rip_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_community_rm_stanza(Set_community_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_community_rm_stanza(Set_community_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNeighbor_rb_stanza(Neighbor_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNeighbor_rb_stanza(Neighbor_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterBanner_stanza(Banner_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitBanner_stanza(Banner_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_comm_list_delete_rm_stanza(
			Set_comm_list_delete_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_comm_list_delete_rm_stanza(
			Set_comm_list_delete_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitchport_trunk_native_if_stanza(
			Switchport_trunk_native_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitchport_trunk_native_if_stanza(
			Switchport_trunk_native_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRoute_map_tail(Route_map_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRoute_map_tail(Route_map_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_address_secondary_if_stanza(
			Ip_address_secondary_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_address_secondary_if_stanza(
			Ip_address_secondary_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMatch_length_rm_stanza(Match_length_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMatch_length_rm_stanza(Match_length_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_standalone_ro_stanza(
			Null_standalone_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_standalone_ro_stanza(
			Null_standalone_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSend_community_bgp_tail(Send_community_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSend_community_bgp_tail(Send_community_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_prefix_list_stanza(Ip_prefix_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_prefix_list_stanza(Ip_prefix_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterStandard_access_list_numbered_stanza(
			Standard_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitStandard_access_list_numbered_stanza(
			Standard_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_rm_stanza(Set_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_rm_stanza(Set_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_rip_ro_stanza(
			Redistribute_rip_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_rip_ro_stanza(
			Redistribute_rip_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIpv6_router_ospf_stanza(Ipv6_router_ospf_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIpv6_router_ospf_stanza(Ipv6_router_ospf_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNo_redistribute_connected_rb_stanza(
			No_redistribute_connected_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNo_redistribute_connected_rb_stanza(
			No_redistribute_connected_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterEbgp_multihop_bgp_tail(Ebgp_multihop_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitEbgp_multihop_bgp_tail(Ebgp_multihop_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNexus_prefix_list_stanza(
			Nexus_prefix_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNexus_prefix_list_stanza(Nexus_prefix_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitchport_trunk_allowed_if_stanza(
			Switchport_trunk_allowed_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitchport_trunk_allowed_if_stanza(
			Switchport_trunk_allowed_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterBgp_listen_range_rb_stanza(
			Bgp_listen_range_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitBgp_listen_range_rb_stanza(
			Bgp_listen_range_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitchport_mode_dynamic_desirable_stanza(
			Switchport_mode_dynamic_desirable_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitchport_mode_dynamic_desirable_stanza(
			Switchport_mode_dynamic_desirable_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterExtended_access_list_stanza(
			Extended_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitExtended_access_list_stanza(
			Extended_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMatch_ip_access_list_rm_stanza(
			Match_ip_access_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMatch_ip_access_list_rm_stanza(
			Match_ip_access_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDefault_information_ro_stanza(
			Default_information_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDefault_information_ro_stanza(
			Default_information_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNexus_neighbor_inherit(Nexus_neighbor_inheritContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNexus_neighbor_inherit(Nexus_neighbor_inheritContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_community_list_standard_tail(
			Ip_community_list_standard_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_community_list_standard_tail(
			Ip_community_list_standard_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_address_stanza(Ip_address_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_address_stanza(Ip_address_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_address_if_stanza(Ip_address_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_address_if_stanza(Ip_address_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPreempt_stanza(Preempt_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPreempt_stanza(Preempt_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNexus_access_list_stanza(
			Nexus_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNexus_access_list_stanza(Nexus_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_stanza(Null_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_stanza(Null_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_route_vrfc_stanza(Ip_route_vrfc_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_route_vrfc_stanza(Ip_route_vrfc_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_as_path_prepend_rm_stanza(
			Set_as_path_prepend_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_as_path_prepend_rm_stanza(
			Set_as_path_prepend_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitchport_mode_access_stanza(
			Switchport_mode_access_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitchport_mode_access_stanza(
			Switchport_mode_access_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_bgp_tail(Null_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_bgp_tail(Null_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPassive_interface_ipv6_ro_stanza(
			Passive_interface_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPassive_interface_ipv6_ro_stanza(
			Passive_interface_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRr_stanza(Rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRr_stanza(Rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPeer_group_assignment_rb_stanza(
			Peer_group_assignment_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPeer_group_assignment_rb_stanza(
			Peer_group_assignment_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_default_gateway_stanza(
			Ip_default_gateway_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_default_gateway_stanza(
			Ip_default_gateway_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_policy_if_stanza(Ip_policy_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_policy_if_stanza(Ip_policy_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNexus_vrf_rb_stanza(Nexus_vrf_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNexus_vrf_rb_stanza(Nexus_vrf_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIpx_sap_access_list_stanza(
			Ipx_sap_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIpx_sap_access_list_stanza(
			Ipx_sap_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_static_ro_stanza(
			Redistribute_static_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_static_ro_stanza(
			Redistribute_static_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNexus_neighbor_no_shutdown(
			Nexus_neighbor_no_shutdownContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNexus_neighbor_no_shutdown(
			Nexus_neighbor_no_shutdownContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDistribute_list_bgp_tail(
			Distribute_list_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDistribute_list_bgp_tail(Distribute_list_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNetwork_rr_stanza(Network_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNetwork_rr_stanza(Network_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterVrf_context_stanza(Vrf_context_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitVrf_context_stanza(Vrf_context_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRouter_id_bgp_tail(Router_id_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRouter_id_bgp_tail(Router_id_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIpx_sap_access_list_null_tail(
			Ipx_sap_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIpx_sap_access_list_null_tail(
			Ipx_sap_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPrefix_list_bgp_tail(Prefix_list_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPrefix_list_bgp_tail(Prefix_list_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_as_path_access_list_stanza(
			Ip_as_path_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_as_path_access_list_stanza(
			Ip_as_path_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_block_stanza(Null_block_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_block_stanza(Null_block_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRemove_private_as_bgp_tail(
			Remove_private_as_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRemove_private_as_bgp_tail(
			Remove_private_as_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterProtocol(ProtocolContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitProtocol(ProtocolContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterArea_ipv6_ro_stanza(Area_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitArea_ipv6_ro_stanza(Area_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterCommunity(CommunityContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitCommunity(CommunityContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_rm_stanza(Null_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_rm_stanza(Null_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_ip_df_rm_stanza(Set_ip_df_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_ip_df_rm_stanza(Set_ip_df_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterStandard_access_list_tail(
			Standard_access_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitStandard_access_list_tail(
			Standard_access_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIpv6_ro_stanza(Ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIpv6_ro_stanza(Ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterStandard_access_list_stanza(
			Standard_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitStandard_access_list_stanza(
			Standard_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_ospf_bgp_tail(
			Redistribute_ospf_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_ospf_bgp_tail(
			Redistribute_ospf_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMatch_ipv6_rm_stanza(Match_ipv6_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMatch_ipv6_rm_stanza(Match_ipv6_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRouter_ospf_stanza(Router_ospf_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRouter_ospf_stanza(Router_ospf_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDescription_line(Description_lineContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDescription_line(Description_lineContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPriority_stanza(Priority_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPriority_stanza(Priority_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAuto_summary_bgp_tail(Auto_summary_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAuto_summary_bgp_tail(Auto_summary_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDefault_originate_bgp_tail(
			Default_originate_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDefault_originate_bgp_tail(
			Default_originate_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterCertificate_stanza(Certificate_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitCertificate_stanza(Certificate_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAccess_list_ip_range(Access_list_ip_rangeContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAccess_list_ip_range(Access_list_ip_rangeContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_rr_stanza(Redistribute_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_rr_stanza(Redistribute_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitchport_mode_trunk_stanza(
			Switchport_mode_trunk_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitchport_mode_trunk_stanza(
			Switchport_mode_trunk_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_bgp_ro_stanza(
			Redistribute_bgp_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_bgp_ro_stanza(
			Redistribute_bgp_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitchport_access_if_stanza(
			Switchport_access_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitchport_access_if_stanza(
			Switchport_access_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDescription_if_stanza(Description_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDescription_if_stanza(Description_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPassive_interface_rr_stanza(
			Passive_interface_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPassive_interface_rr_stanza(
			Passive_interface_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_local_preference_rm_stanza(
			Set_local_preference_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_local_preference_rm_stanza(
			Set_local_preference_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_block_substanza(Null_block_substanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_block_substanza(Null_block_substanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitchport_mode_dynamic_auto_stanza(
			Switchport_mode_dynamic_auto_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitchport_mode_dynamic_auto_stanza(
			Switchport_mode_dynamic_auto_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_connected_bgp_tail(
			Redistribute_connected_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_connected_bgp_tail(
			Redistribute_connected_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNo_neighbor_shutdown_rb_stanza(
			No_neighbor_shutdown_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNo_neighbor_shutdown_rb_stanza(
			No_neighbor_shutdown_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterStandard_access_list_named_stanza(
			Standard_access_list_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitStandard_access_list_named_stanza(
			Standard_access_list_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRouter_id_ipv6_ro_stanza(
			Router_id_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRouter_id_ipv6_ro_stanza(Router_id_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterHostname_stanza(Hostname_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitHostname_stanza(Hostname_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPassive_interface_ro_stanza(
			Passive_interface_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPassive_interface_ro_stanza(
			Passive_interface_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNo_ip_access_list_stanza(
			No_ip_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNo_ip_access_list_stanza(No_ip_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_community_list_expanded_stanza(
			Ip_community_list_expanded_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_community_list_expanded_stanza(
			Ip_community_list_expanded_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRoute_map_tail_tail(Route_map_tail_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRoute_map_tail_tail(Route_map_tail_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIpx_sap_access_list_numbered_stanza(
			Ipx_sap_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIpx_sap_access_list_numbered_stanza(
			Ipx_sap_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNexus_neighbor_rb_stanza(
			Nexus_neighbor_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNexus_neighbor_rb_stanza(Nexus_neighbor_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAddress_family_footer(Address_family_footerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAddress_family_footer(Address_family_footerContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_ipv6_ro_stanza(
			Redistribute_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_ipv6_ro_stanza(
			Redistribute_ipv6_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRouter_bgp_stanza(Router_bgp_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRouter_bgp_stanza(Router_bgp_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPort(PortContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPort(PortContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_static_bgp_tail(
			Redistribute_static_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_static_bgp_tail(
			Redistribute_static_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterUpdate_source_bgp_tail(Update_source_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitUpdate_source_bgp_tail(Update_source_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_interface_rm_stanza(Set_interface_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_interface_rm_stanza(Set_interface_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterInterface_stanza_tail(Interface_stanza_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitInterface_stanza_tail(Interface_stanza_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRange(RangeContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRange(RangeContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_as_path_numbered_stanza(
			Ip_as_path_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_as_path_numbered_stanza(
			Ip_as_path_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterExtended_access_list_null_tail(
			Extended_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitExtended_access_list_null_tail(
			Extended_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNo_ip_address_if_stanza(No_ip_address_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNo_ip_address_if_stanza(No_ip_address_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitchport_trunk_encapsulation_if_stanza(
			Switchport_trunk_encapsulation_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitchport_trunk_encapsulation_if_stanza(
			Switchport_trunk_encapsulation_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDefault_metric_bgp_tail(Default_metric_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDefault_metric_bgp_tail(Default_metric_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_origin_rm_stanza(Set_origin_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_origin_rm_stanza(Set_origin_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDescription_bgp_tail(Description_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDescription_bgp_tail(Description_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_extcomm_list_rm_stanza(
			Set_extcomm_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_extcomm_list_rm_stanza(
			Set_extcomm_list_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_route_stanza(Ip_route_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_route_stanza(Ip_route_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterStandard_access_list_null_tail(
			Standard_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitStandard_access_list_null_tail(
			Standard_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterActivate_bgp_tail(Activate_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitActivate_bgp_tail(Activate_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAllowas_in_bgp_tail(Allowas_in_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAllowas_in_bgp_tail(Allowas_in_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMatch_rm_stanza(Match_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMatch_rm_stanza(Match_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNexus_neighbor_address_family(
			Nexus_neighbor_address_familyContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNexus_neighbor_address_family(
			Nexus_neighbor_address_familyContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_prefix_list_null_tail(
			Ip_prefix_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_prefix_list_null_tail(Ip_prefix_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterProtocol_type_code_access_list_numbered_stanza(
			Protocol_type_code_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitProtocol_type_code_access_list_numbered_stanza(
			Protocol_type_code_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_prefix_list_tail(Ip_prefix_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_prefix_list_tail(Ip_prefix_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterCisco_configuration(Cisco_configurationContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitCisco_configuration(Cisco_configurationContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterMacro_stanza(Macro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitMacro_stanza(Macro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_standalone_if_stanza(
			Null_standalone_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_standalone_if_stanza(
			Null_standalone_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterVrf_forwarding_if_stanza(
			Vrf_forwarding_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitVrf_forwarding_if_stanza(Vrf_forwarding_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterBgp_tail(Bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitBgp_tail(Bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterExact_match(Exact_matchContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitExact_match(Exact_matchContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRouter_ospf_stanza_tail(Router_ospf_stanza_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRouter_ospf_stanza_tail(Router_ospf_stanza_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterVrf_member_if_stanza(Vrf_member_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitVrf_member_if_stanza(Vrf_member_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSubrange(SubrangeContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSubrange(SubrangeContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterPort_specifier(Port_specifierContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitPort_specifier(Port_specifierContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_mpls_label_rm_stanza(
			Set_mpls_label_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_mpls_label_rm_stanza(Set_mpls_label_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterTemplate_peer_address_family(
			Template_peer_address_familyContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitTemplate_peer_address_family(
			Template_peer_address_familyContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRedistribute_connected_ro_stanza(
			Redistribute_connected_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRedistribute_connected_ro_stanza(
			Redistribute_connected_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRouter_id_rb_stanza(Router_id_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRouter_id_rb_stanza(Router_id_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_metric_type_rm_stanza(
			Set_metric_type_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_metric_type_rm_stanza(
			Set_metric_type_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterInterface_stanza(Interface_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitInterface_stanza(Interface_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterExtended_access_list_named_stanza(
			Extended_access_list_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitExtended_access_list_named_stanza(
			Extended_access_list_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterFilter_list_bgp_tail(Filter_list_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitFilter_list_bgp_tail(Filter_list_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAppletalk_access_list_numbered_stanza(
			Appletalk_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAppletalk_access_list_numbered_stanza(
			Appletalk_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_ospf_dead_interval_minimal_if_stanza(
			Ip_ospf_dead_interval_minimal_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_ospf_dead_interval_minimal_if_stanza(
			Ip_ospf_dead_interval_minimal_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAlways_compare_med_rb_stanza(
			Always_compare_med_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAlways_compare_med_rb_stanza(
			Always_compare_med_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNext_hop_self_bgp_tail(Next_hop_self_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNext_hop_self_bgp_tail(Next_hop_self_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_ospf_cost_if_stanza(Ip_ospf_cost_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_ospf_cost_if_stanza(Ip_ospf_cost_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRoute_map_bgp_tail(Route_map_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRoute_map_bgp_tail(Route_map_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDistance_rr_stanza(Distance_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDistance_rr_stanza(Distance_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_access_group_if_stanza(
			Ip_access_group_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_access_group_if_stanza(
			Ip_access_group_if_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterExtended_access_list_tail(
			Extended_access_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitExtended_access_list_tail(
			Extended_access_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterHsrpc_stanza(Hsrpc_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitHsrpc_stanza(Hsrpc_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRemote_as_bgp_tail(Remote_as_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRemote_as_bgp_tail(Remote_as_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterShutdown_bgp_tail(Shutdown_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitShutdown_bgp_tail(Shutdown_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterArea_nssa_ro_stanza(Area_nssa_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitArea_nssa_ro_stanza(Area_nssa_ro_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_community_list_expanded_tail(
			Ip_community_list_expanded_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_community_list_expanded_tail(
			Ip_community_list_expanded_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterProtocol_type_code_access_list_stanza(
			Protocol_type_code_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitProtocol_type_code_access_list_stanza(
			Protocol_type_code_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNexus_access_list_tail(Nexus_access_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNexus_access_list_tail(Nexus_access_list_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRm_stanza(Rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRm_stanza(Rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNetwork6_bgp_tail(Network6_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNetwork6_bgp_tail(Network6_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRoute_map_named_stanza(Route_map_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRoute_map_named_stanza(Route_map_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterCluster_id_bgp_tail(Cluster_id_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitCluster_id_bgp_tail(Cluster_id_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterExtended_access_list_numbered_stanza(
			Extended_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitExtended_access_list_numbered_stanza(
			Extended_access_list_numbered_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterExtended_access_list_additional_feature(
			Extended_access_list_additional_featureContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitExtended_access_list_additional_feature(
			Extended_access_list_additional_featureContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNo_neighbor_activate_rb_stanza(
			No_neighbor_activate_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNo_neighbor_activate_rb_stanza(
			No_neighbor_activate_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterAppletalk_access_list_stanza(
			Appletalk_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitAppletalk_access_list_stanza(
			Appletalk_access_list_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterTemplate_peer_rb_stanza(Template_peer_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitTemplate_peer_rb_stanza(Template_peer_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterCluster_id_rb_stanza(Cluster_id_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitCluster_id_rb_stanza(Cluster_id_rb_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterDistribute_list_rr_stanza(
			Distribute_list_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitDistribute_list_rr_stanza(
			Distribute_list_rr_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterIp_prefix_list_named_stanza(
			Ip_prefix_list_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitIp_prefix_list_named_stanza(
			Ip_prefix_list_named_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNexus_access_list_null_tail(
			Nexus_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNexus_access_list_null_tail(
			Nexus_access_list_null_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSwitching_mode_stanza(Switching_mode_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSwitching_mode_stanza(Switching_mode_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterNull_standalone_stanza(Null_standalone_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitNull_standalone_stanza(Null_standalone_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_weight_rm_stanza(Set_weight_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_weight_rm_stanza(Set_weight_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterSet_community_additive_rm_stanza(
			Set_community_additive_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitSet_community_additive_rm_stanza(
			Set_community_additive_rm_stanzaContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterRoute_reflector_client_bgp_tail(
			Route_reflector_client_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitRoute_reflector_client_bgp_tail(
			Route_reflector_client_bgp_tailContext ctx) {
		// TODO Auto-generated method stub
		
	}

}

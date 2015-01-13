parser grammar AristaGrammar_isis;

import AristaGrammarCommonParser;

options {
   tokenVocab = AristaGrammarCommonLexer;
}

null_router_isis_stanza
//@init{ System.out.println("in null_router_isis_stanza"); }
//@after{ System.out.println("end of null_router_isis_stanza"); }
:
   (
      ADVERTISE
      | AUTHENTICATION
      | FAST_FLOOD
      | IS_TYPE
      | LOG_ADJACENCY_CHANGES
      | LSP_GEN_INTERVAL
      | LSP_REFRESH_INTERVAL
      | METRIC_STYLE
      | MAX_LSP_LIFETIME
      | NET
      | NO HELLO
      | PASSIVE_INTERFACE
      | PRC_INTERVAL
      | REDISTRIBUTE
      | SET_OVER
      | SET_OVERLOAD_BIT
      | SPF_INTERVAL
   ) ~NEWLINE* NEWLINE
;

router_isis_stanza
//@init{ System.out.println("router isis, not processed yet!!"); }
//@after{ System.out.println("end of router_isis_stanza"); }
:
   ROUTER ISIS name = ~NEWLINE NEWLINE
   router_isis_stanza_tail+
;

router_isis_stanza_tail
//@init{ System.out.println("in router_isis_stanza_tail"); }
:
   null_router_isis_stanza
;

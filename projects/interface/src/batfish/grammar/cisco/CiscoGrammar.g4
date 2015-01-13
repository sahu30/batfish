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

}

cisco_configuration
:
   (
      interface_stanza
   )+ NEWLINE* EOF
;

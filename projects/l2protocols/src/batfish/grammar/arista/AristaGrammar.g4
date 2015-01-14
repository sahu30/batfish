parser grammar AristaGrammar;

import
AristaGrammarCommonParser, AristaGrammar_interface;

options {
//  superClass = 'batfish.grammar.BatfishParser';
   tokenVocab = AristaGrammarCommonLexer;
}

@header {
package batfish.grammar.arista;
}

@members {

}



arista_configuration
:
   (
      interface_stanza
   )+ COLON? END? NEWLINE* EOF
;

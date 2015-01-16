parser grammar QuantaGrammar;

import
QuantaGrammarCommonParser, QuantaGrammar_interface;

options {
//  superClass = 'batfish.grammar.BatfishParser';
   tokenVocab = QuantaGrammarCommonLexer;
}

@header {
package batfish.grammar.quanta;
}

@members {

}


quanta_configuration
:
   (
      interface_stanza
   )+ COLON? END? NEWLINE* EOF
;

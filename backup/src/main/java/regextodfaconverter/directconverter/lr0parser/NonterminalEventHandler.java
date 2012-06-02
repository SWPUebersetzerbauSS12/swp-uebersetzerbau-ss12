package regextodfaconverter.directconverter.lr0parser;

import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;

public interface NonterminalEventHandler {

	
	Object handle( Object sender, Nonterminal nonterminal) throws Exception;
}

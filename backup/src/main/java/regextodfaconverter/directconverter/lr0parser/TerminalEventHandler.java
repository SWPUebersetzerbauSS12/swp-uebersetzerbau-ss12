package regextodfaconverter.directconverter.lr0parser;

import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;

public interface TerminalEventHandler {

	
	Object handle( Object sender, Terminal terminal) throws Exception;
}

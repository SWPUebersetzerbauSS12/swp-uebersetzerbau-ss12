package regextodfaconverter.directconverter.lr0parser;


import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;

public class ReadAction<Element extends Comparable<Element>> extends Action<Element> implements EventHandler {

	public ReadAction(ItemAutomata<Element> itemAutomata) {
		super( itemAutomata);
	}

	public Object handle(Object sender) throws ReadException {
		if (itemAutomata.getInputQueue().isEmpty())
			throw new ReadException( "There are no more characters to read.");
		
		
		Terminal<Element> readedTerminal = new Terminal<Element>( itemAutomata.getInputQueue().poll());
		itemAutomata.getSymbolStack().push( readedTerminal);
		
		return readedTerminal;
	}

}

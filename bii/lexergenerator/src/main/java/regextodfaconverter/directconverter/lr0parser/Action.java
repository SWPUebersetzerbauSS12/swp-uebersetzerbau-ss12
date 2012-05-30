package regextodfaconverter.directconverter.lr0parser;

import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;


public abstract class Action<Element> {
	
	protected ItemAutomata<Element> itemAutomata;

	public Action(ItemAutomata<Element> itemAutomata) {
		super();
		this.itemAutomata = itemAutomata;
	}
}

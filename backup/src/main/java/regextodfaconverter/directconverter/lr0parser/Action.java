package regextodfaconverter.directconverter.lr0parser;

import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;


public abstract class Action<Element extends Comparable<Element>> {
	
	protected ItemAutomataInterior<Element> itemAutomata;

	public Action(ItemAutomataInterior<Element> itemAutomata) {
		super();
		this.itemAutomata = itemAutomata;
	}
}

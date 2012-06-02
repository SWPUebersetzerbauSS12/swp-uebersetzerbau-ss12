package regextodfaconverter.directconverter.lr0parser;

import java.util.List;
import java.util.Queue;
import java.util.Stack;

import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;

public interface ItemAutomata<Element extends Comparable<Element>> {

	boolean match( List<Element> input);
		
	/**
	 * - Keine shift-reduce Konflikte
	 * - Keine reduce-reduce Konflikte
	 * - reduce-Aktionen über Followmengen plaziert
	 * @return
	 */
  boolean isSLR1();
	
}

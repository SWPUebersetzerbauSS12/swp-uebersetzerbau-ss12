package regextodfaconverter.directconverter.lr0parser;

import java.util.List;
import java.util.Queue;
import java.util.Stack;

import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;

public interface ItemAutomata<Element> {

	boolean match( List<Element> input);
		
	Stack<RuleElement> getSymbolStack();
	
	Stack<Closure> getClosureStack();

	Queue<Element> getInputQueue();
	
}

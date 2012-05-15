package regextodfaconverter.directconverter.lr0parser;

import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionMap;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementSequenz;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;
import regextodfaconverter.directconverter.lr0parser.itemset.Item;
import regextodfaconverter.directconverter.lr0parser.itemset.ItemSet;
import utils.Test;


public class Lr0ItemAutomata {
	
	private HashSet<Closure> closures = new HashSet<Closure>();
	private Closure currentClosure = null;
	private ContextFreeGrammar grammar;
	
	public Lr0ItemAutomata( ContextFreeGrammar grammar) {
		super();
		this.grammar = grammar;
	}
	
	private Closure calcStartClosure() {
		Nonterminal startSymbol = grammar.getStartSymbol();
		Item startItem = new Item( new Nonterminal(), startSymbol);
		return calcClosureForStartItem( startItem, grammar);
	}
	
	private void InitializeAutomata() {
		Closure startClosure = calcStartClosure();
		closures.add( startClosure);
		currentClosure = startClosure;
		
		
	}
	
	private void ResetAutomata() {
		closures = new HashSet<Closure>();
		currentClosure = null;
		InitializeAutomata();
	}
	
	public <Symbol> boolean match( List<Symbol> input) {
		ResetAutomata();
		
		Stack<Symbol> symbolStack = new Stack<Symbol>();
		Stack<Closure> closureStack = new Stack<Closure>();
		closureStack.add( currentClosure);
		
		ProductionMap currentClosureProductions = currentClosure.toProductionMap();
		for ( Symbol symbol : input) {
			currentClosureProductions.
		}
		
	}

	private static Closure calcClosureForStartItem( Item startItem, ContextFreeGrammar grammar) {
		ItemSet itemSet = new ItemSet();
		itemSet.add( startItem);
		Closure closure = calcClosureOfItemSet( itemSet, grammar);
		closure.putAsKernelItem( startItem);
		return closure;
	}


	private static Closure calcClosureOfItemSet( ItemSet itemSet, ContextFreeGrammar grammar) {
		Closure result = new Closure();
		for ( Item item : itemSet) {
			if ( item.getAnalysePosition() == 0)
				result.addAsNonkernelItem( item);
			else
				result.addAsKernelItem( item);
		}

		boolean hasClosureGrown;
		do {
			hasClosureGrown = false;
			for ( Item item : result.keySet()) {
				RuleElement ruleElement = item.peekNextRuleElement();
				if ( Test.isAssigned( ruleElement) 
						&& ruleElement instanceof Nonterminal) {
					Nonterminal leftRuleSideCandidate = (Nonterminal) ruleElement;
					HashSet<RuleElementSequenz> setOfRightRules = grammar.get( ruleElement);
					if ( Test.isAssigned( setOfRightRules)) {
						checkNextProduction: for ( RuleElementSequenz rightRuleSide : setOfRightRules) {
							for ( Item closureItem : result.keySet()) {
								if ( closureItem.getRightRuleSide().equals( rightRuleSide) && closureItem.getLeftRuleSide().equals( leftRuleSideCandidate)
										&& closureItem.getAnalysePosition() == 0)
									continue checkNextProduction;
							}
							result.addAsNonkernelItem( new Item( leftRuleSideCandidate, rightRuleSide));
							hasClosureGrown = true;
						}

					}
				}
			}
		} while ( !hasClosureGrown);

		return result;
	}


	/**
	 * Formal: GOTO( I,X) = CLOSURE( { [A -> aX.b] | [A -> a.Xb] \in I })
	 * 
	 * @param itemSet
	 * @return
	 */
	private static Closure gotoNextClosure( Closure fromClosure, RuleElement transitionElement, ContextFreeGrammar grammar) {
    ItemSet fromItemSet = fromClosure.getItemSet();
    ItemSet toItemSet = new ItemSet();
    for ( Item fromItem : fromItemSet) {
			if ( fromItem.peekNextRuleElement().equals( transitionElement))
				toItemSet.add( fromItem);
		}
    return calcClosureOfItemSet( toItemSet, grammar);
	}
	
	
}

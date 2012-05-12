package regextodfaconverter.directconverter.lr0parser;

import java.util.HashSet;
import java.util.List;

import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementSequenz;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;
import regextodfaconverter.directconverter.lr0parser.itemset.Item;
import regextodfaconverter.directconverter.lr0parser.itemset.ItemSet;
import utils.Test;


public class Lr0ItemAutomata {
	
	
	public static Closure calcClosureForStartItem( Item startItem, ContextFreeGrammar grammar) {
		ItemSet itemSet = new ItemSet();
		itemSet.add( startItem);
		Closure closure = calcClosureOfItemSet( itemSet, grammar);
		closure.putAsKernelItem( startItem);
		return closure;
	}
		
	public static Closure calcClosureOfItemSet( ItemSet itemSet, ContextFreeGrammar grammar) {
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
					checkNextProduction: 
						for ( RuleElementSequenz rightRuleSide : setOfRightRules) {
						  for ( Item closureItem : result.keySet()) {
							  if ( closureItem.getRightRuleSide().equals( rightRuleSide) 
							  		&& closureItem.getAnalysePosition() == 0)
								  continue checkNextProduction;
					   	}
						  result.addAsNonkernelItem( new Item( leftRuleSideCandidate, rightRuleSide));
						  hasClosureGrown = true;
						}
						
						
					}
				}
		} while ( hasClosureGrown);
		
		return result;
	}

}

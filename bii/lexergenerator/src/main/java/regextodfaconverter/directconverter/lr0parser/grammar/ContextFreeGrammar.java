package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.Sets;
import utils.Test;


public class ContextFreeGrammar extends ProductionMap {
	
	
	private Nonterminal startSymbol = null;

	public Nonterminal getStartSymbol() {
		return startSymbol;
	}
	
	@Override
	public boolean addProduction( ProductionRule productionRule) {
		if ( Test.isUnassigned( productionRule))
			return false;
		
		Nonterminal leftRuleSide = productionRule.getLeftRuleSide();
		if ( Test.isUnassigned( startSymbol))
			startSymbol = leftRuleSide;
			
		RuleElementSequenz rightRuleSide = productionRule.getRightRuleSide();
		if ( this.containsKey( leftRuleSide)) {
			this.get( leftRuleSide).add( rightRuleSide);
		} else {
			HashSet<RuleElementSequenz> ruleSet = new HashSet<RuleElementSequenz>();
      ruleSet.add( rightRuleSide);  
			this.put( leftRuleSide, ruleSet);
		}
		return true;
	}
	
	
	
	
	private Set<Terminal> getFirstSetOfProductionRule( ProductionRule rule) {
		
		Nonterminal leftRuleSide = rule.getLeftRuleSide();
		Set<Terminal> firstSet = new HashSet<Terminal>();
	
		
		for ( RuleElement ruleElement : rule) {
			if ( ruleElement instanceof EmptyString) { 
				// ProductionRule constructor ensure singularity of empty string 
				assert rule.getRightRuleSide().size() == 1;
			  firstSet.add( (Terminal) ruleElement);
			  return firstSet;
			} else if ( ruleElement instanceof Terminal) {
				  firstSet.add( (Terminal) ruleElement);
				  return firstSet;
			} else {
					HashSet<RuleElementSequenz> subRules = this.get( ruleElement);		
					for ( RuleElementSequenz subRule : subRules) {
						Set<Terminal> subFirstSet = getFirstSetOfProductionRule( new ProductionRule( (Nonterminal) ruleElement, subRule));	
						if ( ! subFirstSet.contains( new EmptyString()))
							firstSet.remove( new EmptyString());
						firstSet = Sets.unionCollections( firstSet, subFirstSet);
						return firstSet;
					}
				}
	  }
		return firstSet;  // is an empty set
	}
	
	
	public HashMap<Nonterminal,Set<Terminal>> getFirstSet() {
		HashMap<Nonterminal,Set<Terminal>> result = new HashMap<Nonterminal,Set<Terminal>>();
		for ( Nonterminal leftRuleSide : this.keySet()) {
			// reference
			HashSet<RuleElementSequenz> rules = this.get( leftRuleSide);
			// flat copy 
			HashSet<RuleElementSequenz> unprocessedRules = new HashSet<RuleElementSequenz>();
			unprocessedRules.addAll( rules);
					
			for ( RuleElementSequenz rule : rules) {
				for ( RuleElement ruleElement : rule) {
					if ( ruleElement instanceof Terminal) {
						// do: add rule
						unprocessedRules.remove( rule);
					} else {
						
					}
				}
					
			}
			//if ( )
		}
		
	}
	
	HashMap<Nonterminal,Set<Terminal>> getFollowSet() {
		// todo
		return null;
	}
	
	
	
}

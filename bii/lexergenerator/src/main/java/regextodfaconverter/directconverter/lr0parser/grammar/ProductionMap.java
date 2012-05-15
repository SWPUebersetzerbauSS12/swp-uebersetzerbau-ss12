package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import utils.Test;


public class ProductionMap extends HashMap<Nonterminal, HashSet<RuleElementSequenz>> {
	
		
	public boolean addProduction( ProductionRule productionRule) {
		if ( Test.isUnassigned( productionRule))
			return false;
		
		Nonterminal leftRuleSide = productionRule.getLeftRuleSide();
			
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
	
	public boolean addAll( ProductionSet productionSetToAdd) {
		boolean result = false;
		for ( ProductionRule productionRule : productionSetToAdd) {
			result |= addProduction( productionRule);
		}
		return result;
	}
	
	
	
}

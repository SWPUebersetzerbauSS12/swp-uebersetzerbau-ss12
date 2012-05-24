package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.HashSet;

import utils.Test;


public class ProductionSet extends HashSet<ProductionRule> {
	
	public boolean IsLeftSideUnique() {
		Nonterminal lastNonterminal = null;
		for ( ProductionRule rule : this) {
			if ( lastNonterminal == null) {
				lastNonterminal = rule.getLeftRuleSide();
			} else {
			  if ( !rule.getLeftRuleSide().equals( lastNonterminal)) {
			  	return false;
			  } else {
			    lastNonterminal = rule.getLeftRuleSide(); 	
			  }
			}
		}
		return true;
	}
	
	@Override
	public boolean contains(Object theOtherObject) {

		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof ProductionRule))
			return false;
		
		ProductionRule theOtherProductionRule = (ProductionRule) theOtherObject;
		
		for (ProductionRule rule : this) {
			if ( rule.equals(theOtherObject))
				return true;
		}
		return false;
	}

}

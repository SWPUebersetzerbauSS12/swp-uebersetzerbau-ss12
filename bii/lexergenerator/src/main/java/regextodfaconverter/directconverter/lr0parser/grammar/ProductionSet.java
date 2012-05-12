package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.HashSet;


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

}

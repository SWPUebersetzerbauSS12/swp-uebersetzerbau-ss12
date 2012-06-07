package regextodfaconverter.directconverter.syntaxtree;

import java.util.HashMap;
import java.util.List;

import regextodfaconverter.directconverter.EventHandler;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;


public class SyntaxDirectedDefinition extends HashMap<ProductionRule, SemanticRules> {


	@Override
	public boolean containsKey( Object key) {
		for ( ProductionRule rule : this.keySet()) {
			if ( rule.equals( key))
				return true;
		}
		return false;
	}


	@Override
	public SemanticRules get( Object key) {
		for ( ProductionRule rule : this.keySet()) {
			if ( rule.equals( key))
				return super.get( rule);
		}
		return null;
	}

	
}

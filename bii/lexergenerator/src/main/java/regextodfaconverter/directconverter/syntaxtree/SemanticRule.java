package regextodfaconverter.directconverter.syntaxtree;

import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;


public interface SemanticRule {
	
	void apply( AttributesMap ... attributesMaps);
	

}

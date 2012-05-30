package regextodfaconverter.directconverter.lr0parser;


import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementSequenz;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;

public class ReduceAction<Element> extends Action<Element> implements EventHandler {

	private ProductionRule reduceRule;
	
	public ReduceAction( ItemAutomata<Element> itemAutomata, ProductionRule reduceRule) {
		super( itemAutomata);
		this.reduceRule = reduceRule;
	}

	public Object handle(Object sender) throws ReduceException {
		// apply rule elements and reduce reduce the stacks by the way
		RuleElementSequenz rightReduceRuleSide = reduceRule.getRightRuleSide();
		for ( int i = rightReduceRuleSide.size(); i > 0; i--) {
		   itemAutomata.getClosureStack().pop();
		   RuleElement elementFromStack = itemAutomata.getSymbolStack().pop();
		   System.out.println( ">1 "+elementFromStack);
           RuleElement reduceRuleElement = rightReduceRuleSide.get( i-1);
           System.out.println( ">2 "+reduceRuleElement);
           
           if ( ! elementFromStack.equals( reduceRuleElement))
			   throw new ReduceException(String.format("Missing expected element %s while reduce with rule %s. Found instead %s.", reduceRuleElement, reduceRule, elementFromStack));
		}
		itemAutomata.getSymbolStack().push( reduceRule.getLeftRuleSide());
		
		return itemAutomata.getClosureStack().peek();
	}
	
	public ProductionRule getReduceRule() {
		return reduceRule;
	}

}

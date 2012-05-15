package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import utils.Test;


public class ProductionRule implements Iterable<RuleElement> {

	private Nonterminal leftRuleSide;

	private RuleElementSequenz rightRuleSide;


	public ProductionRule( Nonterminal leftRuleSide,
			RuleElementSequenz rightRuleSide) {
		super();
		this.leftRuleSide = leftRuleSide;
		if ( Test.isAssigned( rightRuleSide) 
				&& rightRuleSide.size() > 0) {
   		this.rightRuleSide = filterEmptyStrings( rightRuleSide);
		} else {
			this.rightRuleSide = (RuleElementSequenz) new ArrayList<RuleElement>();
		  this.rightRuleSide.add( new EmptyString());
		}
	}
	
	public ProductionRule( Nonterminal leftRuleSide,
			RuleElement ... rightRuleSideElements) {
		this( leftRuleSide, (RuleElementSequenz) Arrays.asList( rightRuleSideElements));
	}
	
	private RuleElementSequenz filterEmptyStrings( RuleElementSequenz elementSequenz) {
		int len = elementSequenz.size();
		for ( int i = len-1; i > 0; i--) {
			if ( elementSequenz.get(i) instanceof EmptyString)
				elementSequenz.remove( i);
		}
		return elementSequenz;
	}


	public Nonterminal getLeftRuleSide() {
		return leftRuleSide;
	}

	
	protected boolean isRightSideRuleEmpty() {
		boolean result = true;
		for ( RuleElement ruleElement : rightRuleSide) {
			result &= ( ruleElement instanceof EmptyString);
		}
		return result;
	}
	
	protected int rightSideRuleSize() {
		int result = 0;
		for ( RuleElement ruleElement : rightRuleSide) {
			result += ( ruleElement instanceof EmptyString) ? 0 : 1;
		}
		return result;
	}
	

	public RuleElementSequenz getRightRuleSide() {
		return rightRuleSide;
	}


	public Iterator<RuleElement> iterator() {
		return rightRuleSide.iterator();
	}
	
	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof ProductionRule))
			return false;
		
		
		ProductionRule theOtherProductionRule = (ProductionRule) theOtherObject;
		
		if ( !theOtherProductionRule.getLeftRuleSide().equals( this.leftRuleSide))
			return false;
		
		if ( theOtherProductionRule.getRightRuleSide().size() != this.rightRuleSide.size())
			return false;

		int length = rightRuleSide.size();
		List<RuleElement> theOtherRightRuleSide = theOtherProductionRule.getRightRuleSide();
		for( int i=0; i < length; i++) {
			if ( !theOtherRightRuleSide.get( i).equals( this.rightRuleSide.get(i)))
				return false;
		}
		
		return true;
	}
	
	

}

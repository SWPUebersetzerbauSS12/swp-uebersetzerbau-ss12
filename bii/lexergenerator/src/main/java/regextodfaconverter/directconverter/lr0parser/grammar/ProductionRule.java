package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.ArrayList;
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
		this.rightRuleSide = rightRuleSide;
	}


	public Nonterminal getLeftRuleSide() {
		return leftRuleSide;
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

package regextodfaconverter.directconverter.lr0parser.itemset;

import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementSequenz;


public class Item extends ProductionRule {

	private int analysePosition = 0;
	
	public Item( Nonterminal leftRuleSide, RuleElementSequenz rightRuleSide) {
		super( leftRuleSide, rightRuleSide);
    }
	
	public Item( Nonterminal leftRuleSide,
			RuleElement ... rightRuleSideElements) {
		super( leftRuleSide, rightRuleSideElements);
	}
	
	public Item( Nonterminal leftRuleSide, RuleElementSequenz rightRuleSide, int analysePosition) {
		super( leftRuleSide, rightRuleSide);
        this.analysePosition = analysePosition;
	}
	
	public Item( Item item, int analysePosition) {
		super( item.getLeftRuleSide(), item.getRightRuleSide());
		this.analysePosition = analysePosition;
    }
	
	public int getAnalysePosition() {
		return analysePosition;
	}
	
	
	public void setAnalysePosition( int analysePosition) {
		this.analysePosition = analysePosition;
	}
	
	
	
	public boolean IsProcessed() {
		return this.rightSideRuleSize() <= analysePosition;
	}
	
	public boolean canStepForward() {
		return this.rightSideRuleSize() > analysePosition;
	}

	
	public void stepForward() {
		if ( canStepForward())
			analysePosition++;
	}
	
	public RuleElement peekNextRuleElement() {
		if ( canStepForward())
		  return this.getRightRuleSide().get( analysePosition);
		return null;
	}
	
	public RuleElement getNextRuleElementAndStepForward() {
		if ( canStepForward())
		  return this.getRightRuleSide().get( analysePosition++);
		return null;
	}
	
	@Override
	public boolean equals( Object theOtherObject) {
		if ( !super.equals( theOtherObject))
			return false;
		
		if ( !(theOtherObject instanceof Item))
			return false;
		
		Item theOtherItem = (Item) theOtherObject;
		
		return theOtherItem.getAnalysePosition() == this.analysePosition;
	}

	public ProductionRule toProduction() {
		return new ProductionRule(getLeftRuleSide(), getRightRuleSide());
	}

	@Override
	public String toString() {
		String result = getLeftRuleSide().toString() + " -> ";
		if ( analysePosition == 0)
			result += ".";
		for( int i = 0; i < getRightRuleSide().size(); i++) {
			result += getRightRuleSide().get(i).toString();
			if ( analysePosition == i+1)
				result += ".";
		}			
		return result;
	}
	

}

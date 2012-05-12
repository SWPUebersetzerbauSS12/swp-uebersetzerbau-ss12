package regextodfaconverter.directconverter.lr0parser.grammar;

import utils.Test;


public class Terminal<Symbol> extends RuleElement {
	
	private Symbol symbol;
	
	
	public Terminal( Symbol symbol) {
		super();
		this.symbol = symbol;
	}
	
	
	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof Terminal))
			return false;
		
		Terminal theOtherTerminal = (Terminal) theOtherObject;
		
		if ( !theOtherTerminal.getSymbol().equals( this.symbol))
			return false;
		
		return true;
	}
	
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public String toString() {
		return symbol.toString();
	}

}

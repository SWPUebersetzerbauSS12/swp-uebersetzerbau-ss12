package regextodfaconverter.directconverter.lr0parser.grammar;

import utils.Test;


public class Terminal<Symbol extends Comparable<Symbol>> extends RuleElement {
	
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


	public int compareTo(RuleElement o) {
		if ( Test.isUnassigned(o))
			return 1;
		if ( o instanceof Nonterminal)
			return -1;
		if ( o instanceof Terminator)
			return -1;
		if ( o instanceof EmptyString)
			return +1;
		if ( o instanceof Terminal)
			return ((Terminal)o).getSymbol().compareTo( this.symbol);
		
		return -1;
	}

}

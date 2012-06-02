package regextodfaconverter.directconverter.lr0parser.grammar;

import utils.Test;


public class Nonterminal extends RuleElement {
	
	private String name = null;
	
	
	public Nonterminal() {
		super();
	}
	
	public Nonterminal( String name) {
		super();
		this.name = name;
	}
	

	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof Nonterminal))
			return false;
		
		Nonterminal theOtherNonterminal = (Nonterminal) theOtherObject;
		// Wenn über Namen gearbeitet wird, dann gleich, wenn Bezeichner gleich sind
		if ( Test.isAssigned( this.name) && this.name.equals( theOtherNonterminal.name))
			return true;
		
		return super.equals(theOtherNonterminal);
	}
	
	@Override
	public String toString() {
		return name != null ? name : super.toString();
	}
	
	
	public int compareTo(RuleElement o) {
		if ( Test.isUnassigned(o))
			return 1;
		if ( o instanceof Terminal)
			return 1;
		if ( o instanceof Terminator)
			return 1;
		if ( o instanceof EmptyString)
			return 1;
		if ( o instanceof Nonterminal)
			return ((Nonterminal)o).name.compareTo( this.name);
		
		return -1;
	}
	
}

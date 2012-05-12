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
		
    // Kein Vergleich auf name, da name optional. 
		// Dient der Ausgabe der toString() Methode. -> debugging
		
		return true;
	}
	
	@Override
	public String toString() {
		return name != null ? name : super.toString();
	}
	
}

package de.fuberlin.projectci.grammar;

public class TerminalSymbol extends Symbol {

	public TerminalSymbol(String value) {
		super(value);
	}
	
	@Override
	public String toString()  {
		if (getName().equals(Grammar.EMPTY_STRING))
			return "ε";
		
		else if (this.equals(Grammar.INPUT_ENDMARKER))
			return "$"; // Vielleicht noch Variable gestallten
		
		else
			return "\""+getName()+"\"";
	}
 
}
 

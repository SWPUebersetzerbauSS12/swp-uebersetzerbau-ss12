package de.fuberlin.projectci.grammar;


/**
 * Repräsentiert ein Terminalsymbol.
 *
 */
public class TerminalSymbol extends Symbol {

	/**
	 * Erstellt ein Terminalsymbol.
	 * @param value Bezeichnung des Terminalsymbols.
	 */
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
 

package de.fuberlin.projectci.grammar;

/**
 * Repräsentiert ein Nichtterminalsymbol
 *
 */
public class NonTerminalSymbol extends Symbol {

	/**
	 * Erstellt ein neues Nichtterminalsymbol.
	 * @param Die Bezeichnung des Symboles.
	 */
	public NonTerminalSymbol(String value) {
		super(value);
	}
	 
	public String toString()  {
		
			return getName();
	}
}
 

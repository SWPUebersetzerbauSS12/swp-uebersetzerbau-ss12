package de.fuberlin.projectci.grammar;

import java.util.Arrays;
import java.util.List;

import de.fuberlin.commons.util.EasyComparableObject;


/*
public class Production {
 
	private NonTerminalSymbol lhs;
	private List<Symbol> rhs;
	
	public Production(NonTerminalSymbol lhs, List<?extends Symbol> rhs) {
		super(); // TODO hier kein Super
		this.lhs = lhs;
		this.rhs = (List<Symbol>) rhs;	// TODO ist das so sinnvoll?
	}

	public Production(NonTerminalSymbol lhs, Symbol[] rhs) {
		super(); // TODO hier kein Super
		this.lhs = lhs;
		this.rhs = new ArrayList<Symbol>();
		for (int i = 0; i < rhs.length; i++) {
			Symbol aSymbol = rhs[i];
			this.rhs.add(aSymbol);
		}
	}
	
	public NonTerminalSymbol getLhs() {
		return lhs;
	}

	public List<Symbol> getRhs() {
		return rhs;
	}

	

	
	 
}

*/

public class Production extends EasyComparableObject{
	
	// Rechte Regelseite (Nichtterminalsymbol)
	private NonTerminalSymbol lhs;
	
	// Linke Regelseite (Liste von Symbolen)
	private List<Symbol> rhs;
	

	/**
	 * Erstellt eine Produktion aus einer linken und rechten Regelseite.
	 * @param lhs
	 * @param rhs Rechte Regelseite als Liste von Symbolen.
	 */
	public Production(NonTerminalSymbol lhs, List<Symbol> rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	/**
	 * Erstellt eine Produktion aus einer linken und rechten Regelseite.
	 * @param lhs
	 * @param rhs Rechte Regelseite als Array von Symbolen.
	 */
	

	public Production(NonTerminalSymbol lhs, Symbol[] rhs) {
		this.lhs = lhs;
		
		// Das Array in eine Liste umwandeln
		this.rhs = Arrays.asList(rhs);
		
	}
	
	@Override
	protected Object[] getSignificantFields() {
		return new Object[]{lhs, rhs};
	}
	
	/**
	 * Gibt die linke Regelseite zurück.
	 * @return
	 */
	public NonTerminalSymbol getLhs() {
		return lhs;
	}
	
	/**
	 * Gibt die rechte Regelseite zurück.
	 * @return
	 */
	public List<Symbol> getRhs() {
		return rhs;
	}
	

	/*
	 * Überschreibung der toString() Methode um Produktionen ausgeben zu können.
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String production = lhs.getName()+" -->";
		
		for(Symbol s : rhs) {
			production = production.concat(" "+s.toString());
		}
		
		return production;
	}
}
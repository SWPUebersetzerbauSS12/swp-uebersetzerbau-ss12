package parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Grammar {
	
	// Die Liste enthält alle Produktion in der Reihenfolge, wie sie zur Grammatik hinzugefügt worden
	private LinkedList<Production> productions = new LinkedList<Production>();
	

	// Speichern der Terminal Symbole
	private HashMap<String, TerminalSymbol> TerminalSymbols = new HashMap<String, TerminalSymbol>();
	
	
	// Speichern der Nicht Terminal Symbole
	private HashMap<String, NonTerminalSymbol> NonTerminalSymbols = new HashMap<String, NonTerminalSymbol>();
	
	public void addProduction(Production production){
		if(!productions.contains(production))
			productions.add(production);
	}
	
	
	/**
	 * Gibt für ein Nichtterminalsymbol das entsprechende Objekt zurück oder legt ein neues an.
	 * @param name Der Wert des Nichtterminalsymbols als String
	 * @return Das dazu passende Objekt
	 */
	public NonTerminalSymbol getNonTerminalSymbol(String name) {		
		// Gibt es zu diesem Token schon ein Nicht Terminalsymbol?
		NonTerminalSymbol result = NonTerminalSymbols.get(name);
		
		if(result == null) {
			result = new NonTerminalSymbol(name);
			NonTerminalSymbols.put(name, result);
		}
		
		
		return result;
		
	}
	
	/**
	 * Gibt für ein Terminalsymbol das entsprechende Objekt zurück oder legt ein neues an.
	 * @param name Der Wert des Terminalsymbols als String
	 * @return Das dazu passende Objekt
	 */
	public TerminalSymbol getTerminalSymbol(String name) {
		TerminalSymbol result = TerminalSymbols.get(name);
		
		if(result == null) {
			result = new TerminalSymbol(name);
			TerminalSymbols.put(name,result);
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		TerminalSymbol a = new TerminalSymbol("A");
		TerminalSymbol aa = new TerminalSymbol("A");
		
		HashSet<TerminalSymbol> hs = new HashSet<TerminalSymbol>();
		hs.add(a);
		
		
		System.out.println(hs.contains(aa));
		
		System.out.println(a instanceof Symbol);
	}
}

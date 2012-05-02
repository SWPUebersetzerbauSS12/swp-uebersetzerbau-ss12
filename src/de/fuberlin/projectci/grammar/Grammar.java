package de.fuberlin.projectci.grammar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Grammar {
 
	// Die Liste enthält alle Produktion in der Reihenfolge, wie sie zur Grammatik hinzugefügt worden
	private List<Production> productions = new LinkedList<Production>();
	
	// Speichern der Terminal Symbole
	private HashMap<String, TerminalSymbol> TerminalSymbols = new HashMap<String, TerminalSymbol>();
	
	// Speichern der Nicht Terminal Symbole
	private HashMap<String, NonTerminalSymbol> NonTerminalSymbols = new HashMap<String, NonTerminalSymbol>();
	
	private HashMap<NonTerminalSymbol, List<Production>> ProdList = new HashMap<NonTerminalSymbol, List<Production>>();
	
	//Startsymbol	
	NonTerminalSymbol startSymbol = null;
	
	
	
	
	public List<Production> getProductions() {
		return productions;
	}

	public Production getProductionAtIndex(int index){
		return productions.get(index);
	}
	
	void setProductions(List<Production> productions) {
		this.productions = productions;
	}
	
	public void addProduction(Production production){
		if(!productions.contains(production))
			productions.add(production);
		
		// In die Liste Nonterminal Symbol --> Porduktionen einfügen
		// Eintrag schon vorhanden?
		
		List temp = ProdList.get(production.getLhs());
		
		if (temp != null) {
			ProdList.get(production.getLhs()).add(production);
		} else {
			List<Production> p = new LinkedList<Production>();
			p.add(production);
			ProdList.put(production.getLhs(), p);
		}
		
	}
	
	/**
	 * Gibt für ein Nichtterminalsymbol das entsprechende Objekt zurück oder legt ein neues an.
	 * @param name Der Wert des Nichtterminalsymbols als String
	 * @return Das dazu passende Objekt
	 */
	public NonTerminalSymbol createNonTerminalSymbol(String name) {		
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
	public TerminalSymbol createTerminalSymbol(String name) {
		TerminalSymbol result = TerminalSymbols.get(name);
		
		if(result == null) {
			result = new TerminalSymbol(name);
			TerminalSymbols.put(name,result);
		}
		
		return result;
	}
	
	/**
	 * Legt das Startsymbol für die Grammatik fest
	 * @param startSymbol
	 */
	public void setStartSymbol(NonTerminalSymbol startSymbol) {
		this.startSymbol = startSymbol;
	}
	
	/**
	 * Gibt das Startsymbol der Grammatik wieder
	 * @return
	 */
	public NonTerminalSymbol getStartSymbol() {
		return startSymbol;
	}
	 
}
 

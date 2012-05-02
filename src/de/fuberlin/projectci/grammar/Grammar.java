package de.fuberlin.projectci.grammar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Grammar {
 
	// Die Liste enthält alle Produktion in der Reihenfolge, wie sie zur Grammatik hinzugefügt worden
	private List<Production> productions = new LinkedList<Production>();
	
	// Speichern der Terminal Symbole
	private HashMap<String, TerminalSymbol> TerminalSymbols = new HashMap<String, TerminalSymbol>();
	
	// Speichern der Nicht Terminal Symbole
	private HashMap<String, NonTerminalSymbol> NonTerminalSymbols = new HashMap<String, NonTerminalSymbol>();
	
	
	// Speichert zu jedem Nichtterminal eine Liste von Produktionen, bei denen es auf der linken Regelseite vorkommt
	private HashMap<NonTerminalSymbol, List<Production>> ProdList = new HashMap<NonTerminalSymbol, List<Production>>();
	
	//Startsymbol	
	NonTerminalSymbol startSymbol = null;
	
	//TODO Leeres Wort definieren
	public static final String EMPTY_STRING = "@" ;
	
	private static final TerminalSymbol EPSILON = new TerminalSymbol(EMPTY_STRING);
	
	
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
		
		List<Production> temp = ProdList.get(production.getLhs());
		
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
			
			result = name.equals(EMPTY_STRING) ? EPSILON : new TerminalSymbol(name);
			
			/*
			if(name.equals(EMPTY_STRING))
				result = EPSILON;
			else
				result = new TerminalSymbol(name);
				
				*/
			
			TerminalSymbols.put(name,result);
		}
		
		
		return result;
	}
	
	/**
	 * Legt das Startsymbol für die Grammatik fest
	 * @param startSymbol
	 */
	public void setStartSymbol(NonTerminalSymbol startSymbol) {
		//TODO prüfen ob das Symbol gültig ist!
		this.startSymbol = startSymbol;
	}
	
	/**
	 * Gibt das Startsymbol der Grammatik zurück
	 * @return
	 */
	public NonTerminalSymbol getStartSymbol() {
		return startSymbol;
	}
	
	/**
	 * Gibt eine Menge @see java.util.Set der Nichtterminale zurück.
	 * @return
	 */
	public Set<NonTerminalSymbol> getAllNonTerminals() {
		return ProdList.keySet();
	}
	
		
	/**
	 * Gibt eine menschenlesbare Textdarstellung der Grammatik zurück.
	 */
	@Override
	public String toString() {
		StringBuilder grammarSB = new StringBuilder();
		
		for(Production p : productions) {
			grammarSB.append(p.toString());
			grammarSB.append("\n");
		}
		
		// Das Startsymbol am Ende anzeigen
		grammarSB.append("\n");
		grammarSB.append("Startsymbol: ");
		grammarSB.append(startSymbol);
		
		return grammarSB.toString();
	}
	 
}
 

package de.fuberlin.projectci.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Grammar {
 
	// Die Liste enthält alle Produktion in der Reihenfolge, wie sie zur Grammatik hinzugefügt worden
	private List<Production> productions = new ArrayList<Production>();
	
	// Speichern der Terminal Symbole
	private Map<String, TerminalSymbol> name2Terminal = new HashMap<String, TerminalSymbol>();
	
	// Speichern der Nicht Terminal Symbole
	private Map<String, NonTerminalSymbol> name2NonTerminal = new HashMap<String, NonTerminalSymbol>();
	
	
	// Speichert zu jedem Nichtterminal eine Liste von Produktionen, bei denen es auf der linken Regelseite vorkommt
	private Map<NonTerminalSymbol, List<Production>> nonTerminal2Productions = new HashMap<NonTerminalSymbol, List<Production>>();
	
	//Startsymbol	
	NonTerminalSymbol startSymbol = null;
	
	//TODO Leeres Wort definieren
	// XXX Warum nicht Epsilon: EMPTY_STRING = "\u03B5";
	public static final String EMPTY_STRING = "@" ;
	
	public static final TerminalSymbol EPSILON = new TerminalSymbol(EMPTY_STRING);
	
	
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
		
		List<Production> temp = nonTerminal2Productions.get(production.getLhs());
		
		if (temp != null) {
			nonTerminal2Productions.get(production.getLhs()).add(production);
		} else {
			List<Production> p = new LinkedList<Production>();
			p.add(production);
			nonTerminal2Productions.put(production.getLhs(), p);
		}
		
	}
	
	/**
	 * Gibt für ein Nichtterminalsymbol das entsprechende Objekt zurück oder legt ein neues an.
	 * @param name Der Wert des Nichtterminalsymbols als String
	 * @return Das dazu passende Objekt
	 */
	public NonTerminalSymbol createNonTerminalSymbol(String name) {		
		// Gibt es zu diesem Token schon ein Nicht Terminalsymbol?
		NonTerminalSymbol result = name2NonTerminal.get(name);
		
		if(result == null) {
			result = new NonTerminalSymbol(name);
			name2NonTerminal.put(name, result);
		}
		
		
		return result;
		
	}
	
	/**
	 * Gibt für ein Terminalsymbol das entsprechende Objekt zurück oder legt ein neues an.
	 * @param name Der Wert des Terminalsymbols als String
	 * @return Das dazu passende Objekt
	 */
	public TerminalSymbol createTerminalSymbol(String name) {
		TerminalSymbol result = name2Terminal.get(name);
		
		if(result == null) {
			
			result = name.equals(EMPTY_STRING) ? EPSILON : new TerminalSymbol(name);
			
			/*
			if(name.equals(EMPTY_STRING))
				result = EPSILON;
			else
				result = new TerminalSymbol(name);
				
				*/
			
			name2Terminal.put(name,result);
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
		return nonTerminal2Productions.keySet();
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
	 
	// **************************************************************************** 
	// * Implementierung einiger grundlegender Grammatik-Algorithmen
	// ****************************************************************************

	/**
	 * Berechnet die First-Menge zu einem Symbol.
	 */
	public Set<TerminalSymbol> first(Symbol s) {
		// TODO Implementiere mich.
		return null;
	}
	 
	/**
	 * Berechnet die Follow-Menge zu einem Nicht-Terminalsymbol.
	 */
	
	public Set<TerminalSymbol> follow(NonTerminalSymbol nts) {
		// TODO Implementiere mich.
		return null;
	}
	
}
 

package de.fuberlin.projectci.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Grammar {
 
	// Die Liste enthält alle Produktion in der Reihenfolge, wie sie zur Grammatik hinzugefügt worden
	private List<Production> productions = new ArrayList<Production>();
	
	// speichert alle Symbole in der Reihenfolge ihres ersten Auftretens 
	// erleichtert die Kontrolle beim Aufbau der LR(0)-Automaten, da die Nummerierung der Zustände von der Reihenfolge der Symbole abhängt
	private List<Symbol> allSymbols= new ArrayList<Symbol>();
	
	// Speichern der Terminal Symbole
	private Map<String, TerminalSymbol> name2Terminal = new HashMap<String, TerminalSymbol>();
	
	// Speichern der Nicht Terminal Symbole
	private Map<String, NonTerminalSymbol> name2NonTerminal = new HashMap<String, NonTerminalSymbol>();
	
	
	// Speichert zu jedem Nichtterminal eine Liste von Produktionen, bei denen es auf der linken Regelseite vorkommt
	private Map<NonTerminalSymbol, List<Production>> nonTerminal2Productions = new HashMap<NonTerminalSymbol, List<Production>>();
	
	//Startsymbol	
	NonTerminalSymbol startSymbol = null;
	
	// Map für die FirstMengen
	private Map<Symbol,Set<TerminalSymbol>> firstSets = null;
	
	// Map für die Follomengen
	private Map<NonTerminalSymbol,Set<TerminalSymbol>> followSets = null;
	
	// Set für die Terminalsymbole
	private Set<TerminalSymbol> terminalSymbols = new HashSet<TerminalSymbol>();
	
	//TODO Leeres Wort definieren
	// XXX Warum nicht Epsilon: EMPTY_STRING = "\u03B5";
	public static final String EMPTY_STRING = "@" ;
	
	public static final TerminalSymbol EPSILON = new TerminalSymbol(EMPTY_STRING);
	
	/**
	 * Markiert das rechte Ende des Inputstrings. Wird für die Followmengen benötigt.
	 * Im Drachenbuch als $ notiert.
	 */
	public static final TerminalSymbol INPUT_ENDMARKER = new TerminalSymbol("EOF"); // 'eof'
	
	
	public Production getStartProduction(){		
		return getProductionsByLhs(getStartSymbol()).get(0);
	}
	
	public List<Production> getProductions() {
		return productions;
	}

	public Production getProductionAtIndex(int index){
		return productions.get(index);
	}
	
	/**
	 * Gibt alle Produktionen zurück, die das übergebene NonTerminalSymbol auf der linken Seite haben.
	 */
	public List<Production> getProductionsByLhs(NonTerminalSymbol lhs) {
		return nonTerminal2Productions.get(lhs);
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
			List<Production> p = new LinkedList<Production>();	// XXX temp benutzen?
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
		
		if (!allSymbols.contains(result)){
			allSymbols.add(result);
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
		
		if (!allSymbols.contains(result)){
			allSymbols.add(result);
		}
		terminalSymbols.add(result);
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
	 * Gibt die Namen der Nichtterminale zurück.
	 * @return Menge @see java.util.Set von Strings aller Nichtterminale.
	 */
	public Set<String> getAllNonterminalNames(){
		return name2NonTerminal.keySet();
	}
	
	/**
	 * Gibt eine Menge @see java.util.Set aller Terminale zurück
	 * @return
	 */
	public Set<TerminalSymbol> getAllTerminalSymols() {
		return terminalSymbols;
	}

	/**
	 * Liefert eine Liste aller Grammatiksymbole in der Reihenfolge ihres ersten Auftretens
	 */
	public List<Symbol> getAllSymbols() {		
		return allSymbols;
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

	
	/*
	 * Überlegung für den Algorithmus für die First Mengen:
	 * - die First Mengen werden alle aufeinmal paralell berechnet
	 * - Vielleicht zuerst eine HashMap Symbol -> Set<Terminal> füllen
	 */
	

	/**
	 * Berechnet die Firstmenge für ein Symbol s der Grammatik
	 * @param s Das Symbol (Terminal oder nicht Terminal) der Grammatik
	 * @return Die Firstmenge für das Symbol S. Gibt null zurück, wenn es Symbol nicht gibt
	 */
	public Set<TerminalSymbol> first(Symbol s) {
		// Wenn die FirstMengen noch nicht berechnet wurden, dann mach dies jetzt
		if(firstSets == null)
			firstSets = calculateFirstSets();
	
		// Die Firstmenge für das Symbol zurück geben
		return firstSets.get(s);
		
		//TODO eventuell noch bessere Fehlerbehandlung
	}
	
	
	/**
	 * Berechnet die Firstmenge für einen String von Symbolen X1X2...Xn
	 * @param Eine geordnete Liste von Symbolen, die den Eingabestring darstellt.
	 * @return Die Firstmenge zu den String
	 */
	public Set<TerminalSymbol> first(List<Symbol> symbols) {
		// Wenn die FirstMengen noch nicht berechnet wurden, dann mach dies jetzt
		if (firstSets == null)
			firstSets = calculateFirstSets();
		
		HashSet<TerminalSymbol> resultFirstSet = new HashSet<TerminalSymbol>();
		
		
		boolean containsEpsilon = false;
		// Berechne die Firstmenge für einen String X1X2...Xn
		for(Symbol x : symbols) {
			// Die Fistmenge von x ohne ε bestimmen (Kopie notwendig)
			HashSet<TerminalSymbol> firstSetOfX = new HashSet<TerminalSymbol>(firstSets.get(x));
			containsEpsilon = firstSetOfX.remove(EPSILON);
			
			// FIRST(Xi)/ε zu FIRST(X1X2...Xn) hinzufügen
			resultFirstSet.addAll(firstSetOfX);
			
			// Nur wenn FIRST(Xi) ein ε enthielt muss FIRST(Xi+1) untersucht werden
			if(!containsEpsilon)
				break;
		}
		
		// Wenn in allen Xi ein ε gefunden wurde, dann füge ε zu FIRST(X1X2...n) hinzu
		if(containsEpsilon)
			resultFirstSet.add(EPSILON);
		
		return resultFirstSet;
		
	}
	/**
	 * Berechnet parallel und erschöpfend die First-Mengen für alle Symbole der Grammatik.
	 * Implementierung des Algorithmus aus dem Drachenbuch Kapitel 4.4.2 Seite 221 (Englische Fassung)
	 * 
	 * @return Es wird ein Wörterbuch von Symbolen auf eine Menge von Terminalsymbolen zurück gegeben
	 */
	//TODO Eventuell private
	public Map<Symbol,Set<TerminalSymbol>> calculateFirstSets() {
		firstSets = new HashMap<Symbol,Set<TerminalSymbol>>();
		// 1. Für alle Terminalsymbole die Mengen erzeugen.
		// Für ein Terminal t gilt FIRST(t) = {ŧ}
	
		for(TerminalSymbol t : terminalSymbols) {
			// Neue leere Menge anlegen
			Set<TerminalSymbol> tempSet = new HashSet<TerminalSymbol>();
			
			// Terminalsymbol erzeugen
			tempSet.add(t);
			firstSets.put(t, tempSet);
		}
		
		// Für alle Nichtterminale initialisieren
		for(NonTerminalSymbol t : getAllNonTerminals()) {
			firstSets.put(t, new HashSet<TerminalSymbol>());
			}
		
		// 2. Zu allen Epsilonproduktionen wird Epsilon zur FIRST Menge der LHS hinzugefügt.
		// Über die Produktionen iterieren
		
		boolean changed = true;
		
				
		// Ich versuchs auch noch mal
		while(changed) { // erschöpfende Ausführung 
			changed = false;
			for(Production p : productions) {
				
				// 2. Regel: Gibt es eine Produktion X → ε, so füge ε zu FIRST(X) hinzu.
				if(p.getRhs().get(0).equals(EPSILON))			
					changed = changed || firstSets.get(p.getLhs()).add(EPSILON);
				
				boolean removed = false;
				// 3.Regel
				
				for(Symbol y : p.getRhs()) {
					Set<TerminalSymbol> firstY = new HashSet<TerminalSymbol>(firstSets.get(y)); // Kopie erzeugen
					
					removed = firstY.remove(EPSILON);
					changed = changed || firstSets.get(p.getLhs()).addAll(firstY); // FIRST(Yi)/ε zu FIRST(X) hinzufügen
					
					if(!removed)
						break; // Wenn kein ε vorkam, schluss

				}
				// ε Einfügen wenn alle FIRST(Yi) ε enthalten
				 if(removed)
					 changed = changed || firstSets.get(p.getLhs()).add(EPSILON);
			}
		}
		
		return firstSets;
	}
	 
	/**
	 * Berechnet die Follow-Menge zu einem Nicht-Terminalsymbol nach dem Algorithmus im Drachenbuch.
	 * Abschnitt 4.4.2 "FIRST and FOLLOW" Seite 221f (Englische, 2. Ausgabe)
	 */
	
	public Set<TerminalSymbol> follow(NonTerminalSymbol nts) {
		// ohne gesetztes Startsymbol kann der Algorithmus nicht arbeiten
		if(startSymbol == null)
			return null; // TODO Vielleicht lieber eine Exception werfen?
		
		// Ohne Firstmengen kann der Algorithmus nicht laufen
		if(firstSets == null )
			firstSets = calculateFirstSets();
		
		if(followSets == null)
			followSets = calculateFollowSets();
		// TODO Implementiere mich.
		

		return followSets.get(nts);
	}

	public Map<NonTerminalSymbol, Set<TerminalSymbol>> calculateFollowSets() {
		// ohne gesetztes Startsymbol kann der Algorithmus nicht arbeiten
		if(startSymbol == null)
			return null; // TODO Vielleicht lieber eine Exception werfen?
		
		// Die Mengen in der Map initialisieren		
		followSets = new HashMap<NonTerminalSymbol,Set<TerminalSymbol>>();		

		for(NonTerminalSymbol nt : getAllNonTerminals()) {
			followSets.put(nt, new HashSet<TerminalSymbol>());	
		}
		
		// [Algorithm] 1. Regel: Füge $ (Endmarkierung) zur FOLLOW Menge des Startsymbols ein
		followSets.get(startSymbol).add(INPUT_ENDMARKER);
		
		
		// erschöpfende Ausführung			
		boolean changed = true;
		while(changed) {
			changed = false;
			// [Algorithm] 2. Regel: für eine Produktion A → αBβ, füge alles von FIRST(β) außer ε
			// zu FOLLOW(B) hinzu
			
			//hmm wie alpha beta und b erkennen? alle präfixe durchgehen?
			// ja also die Symbole(nur Nichtterminale als "B" betrachten) der Reihe nach durchgehen und jeweils den Suffix als beta nehmen?
			
			for(Production p : productions) {
				for(int i = 0; i < p.getRhs().size(); i++) {
					Symbol B = p.getRhs().get(i);
					NonTerminalSymbol A = p.getLhs();
					
					if(B instanceof NonTerminalSymbol) { // an der Stelle i wäre jetzt B
						// alles hinter B gehört zu β 
						List<Symbol> beta = p.getRhs().subList(i+1, p.getRhs().size());
						
						// füge FIRST(β)\ε zu FOLLOW(B) hinzu
						Set<TerminalSymbol> firstOfBeta = first(beta);
						boolean betaHasEpsilon = firstOfBeta.remove(EPSILON);
						changed = changed || followSets.get(B).addAll(firstOfBeta);
						
						// [Algorithm] 3. Regel: für alle A → αB oder A → αBβ mit ε ∈ FIRST(β),
						// füge FOLLOW(A) zu FOLLOW(B) hinzu
						if(beta.isEmpty() || betaHasEpsilon){
							changed = changed || followSets.get(B).addAll(followSets.get(A));
						}
					}
				}
			}
		}

		return followSets;
	}
	
	
	
	
}
 

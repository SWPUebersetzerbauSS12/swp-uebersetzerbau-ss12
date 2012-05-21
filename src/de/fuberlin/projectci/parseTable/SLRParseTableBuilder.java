package de.fuberlin.projectci.parseTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.Production;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;

/**
 * Baut eine SLR-Parsetabelle (Action- und Goto-Tabelle) zu einer erweiterten Grammatik.
 */
public class SLRParseTableBuilder extends ParseTableBuilder {
 
	public SLRParseTableBuilder(Grammar grammar) {
		super(grammar);
	}
	 
	/**
	 *Vgl. Drachenbuch: Algorithmus 4.32
	 *
	 */
	public ParseTable buildParseTable() throws InvalidGrammarException {
		// TODO Implementiere mich
		return null;
	}
	 
	/**
	 *Vgl. Drachenbuch: Abbildung 4.32
	 *
	 */
	public Set<LR0Item> closure(Set<LR0Item> items) {
//		Algorithmus aus Drachenbuch: Abbildung 4.32
//		SetOfItems CLOSURE(I) {
//			J=I;
//			repeat
//				for ( jedes Item A → α.Bβ in J )
//					for ( jede Produktion B → γ von G)
//						if (B → .γ ist nicht in J)
//							füge B → .γ zu J hinzu
//			until keine Items mehr in einer Runde zu J hinzugefügt wurden
//			return J;
//		}
		List<LR0Item> result=new ArrayList<LR0Item>(items);
		boolean added= false;

		do{
			added=false;
			for (int i = 0; i < result.size(); i++) {
				LR0Item anItem = result.get(i);
				Symbol nextSymbol = anItem.getNextSymbol();
				if(nextSymbol == null) 
					continue;
				else if(nextSymbol instanceof TerminalSymbol)
					continue;
				List<Production> productions = getGrammar().getProductionsByLhs((NonTerminalSymbol) nextSymbol);
				for(Production aProduction: productions){
					LR0Item itemCandidate = new LR0Item(aProduction, 0);
					if(!result.contains(itemCandidate)){
						result.add(itemCandidate);
						added = true;
					}
				}
			}
		}
		while(added);
		return new HashSet<LR0Item>(result);
	}
	 	
	/**
	 * Berechnet die Hülle von Items, die unmittelbar nach dem Lesen eines gegebenen 
	 * Symbols aus einer gegebenen Item-Menge folgen.
	 * Vgl. Drachembuch: S.296/ Beispiel 4.27
	 * 
	 * @param items Item-Menge, für welche die "Folge"-Item-Menge berechnet wird.
	 * @param s oberstes Stack-Symbol
	 * @return Hülle der gefundenen "Folge"-Item-Menge
	 */
	public Set<LR0Item> gotoSet(Set<LR0Item> items, Symbol s) {
		// TODO Implementiere mich
		// Algorithmus aus Drachenbuch (eng): Abbildung 4.40
		// --> eigentlich für LR(1), sollte aber für LR(0) identisch sein
		//	SetOfItems GOTO(I,X) {
		//		J=leere Menge;
		// 		for ( jedes Item [A → α.Xβ,a] in I )
		// 			füge Item [A → αX.β,a] zu J hinzu;
		// 		return CLOSURE(J);
		//	}
		
		Set<LR0Item> J = new HashSet<LR0Item>();
		for(LR0Item item : items) {
			if(s.equals(item.getNextSymbol())){ // nach Punkt soll Symbol s folgen
				LR0Item newItem = new LR0Item(item.getProduction(), (item.getIndex()+1));
				
				// da nur aus geg. Set (items) neue Items mit verschobenem Punkt erstellt werden,
				// können keine doppelten Elemente in Set J auftreten
				J.add(newItem); 
			}
		}
		
		return closure(J);
	}
	 
	/**
	 *Vgl. Drachembuch: Abbildung 4.33
	 */
	public List<Set<LR0Item>> cannonicalCollectionOfLR0Items() {
		// TODO Implementiere mich
		return null;
	}
	 
}
 

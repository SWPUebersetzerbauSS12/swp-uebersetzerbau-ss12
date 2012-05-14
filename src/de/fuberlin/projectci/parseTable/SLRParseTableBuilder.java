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
	 *Vgl. Drachembuch: S.296/ Beispiel 4.27
	 */
	public Set<LR0Item> gotoSet(Set<LR0Item> items, Symbol s) {
		// TODO Implementiere mich
		return null;
	}
	 
	/**
	 *Vgl. Drachembuch: Abbildung 4.33
	 */
	public List<Set<LR0Item>> cannonicalCollectionOfLR0Items() {
		// TODO Implementiere mich
		return null;
	}
	 
}
 

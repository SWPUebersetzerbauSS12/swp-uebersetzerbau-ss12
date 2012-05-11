package de.fuberlin.projectci.parseTable;

import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.Production;

import java.util.Set;
import de.fuberlin.projectci.grammar.Symbol;
import java.util.List;

/**
 * Baut eine kanonische LR-Parsetabelle (Action- und Goto-Tabelle) zu einer erweiterten Grammatik.
 */
public class LRParseTableBuilder extends ParseTableBuilder {
 
	public LRParseTableBuilder(Grammar grammar) {
		super(grammar);
	}
	 
	public ParseTable buildParseTable() throws InvalidGrammarException{
		// TODO Implementiere mich
		return null;
	}
	 
	/**
	 *Vgl. Drachenbuch Abbildung 4.40
	 */
	public Set<LR1Item> closure(Set<LR1Item> items) {
		boolean added= false;
		
		do{
			added=false;
			for(LR1Item item: items){
				List<Production> productions = super.getGrammar().getProductions();
				for(Production prod: productions){
//					if (prod.getLhs().equals(nextNonTerminal)) {
//						
//					}
//					else continue;
					List<Symbol> rhs = prod.getRhs();
//					List<Symbol> terminals = getFirst(rhs.substring(item.getIndex())++item.getLookaheadSymbol())
//					for(Symbol terminal: terminals){
//						LR1Item newItem = new LR1Item(prod,0,terminal);
//						items.add(newItem);
//						added=true;
//					}
				}
				
			}
		}
		while(added==true);
		
		return items;
	}
	 
	/**
	 *Vgl. Drachenbuch Abbildung 4.40
	 */
	public Set<LR1Item> gotoSet(Set<LR1Item> items, Symbol s) {
		// TODO Implementiere mich
		return null;
	}
	 
	/**
	 *Vgl. Drachenbuch Abbildung 4.40
	 */
	public List<Set<LR1Item>> cannonicalCollectionOfLR1Items() {
		// TODO Implementiere mich
		return null;
	}
	 
}
 

package de.fuberlin.projectci.parseTable;

import java.util.List;
import java.util.Set;

import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.Symbol;

/**
 * Baut eine kanonische LR-Parsetabelle (Action- und Goto-Tabelle) zu einer erweiterten Grammatik.
 */
public abstract class LRParseTableBuilder extends ParseTableBuilder {
 
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
		return null;
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
 

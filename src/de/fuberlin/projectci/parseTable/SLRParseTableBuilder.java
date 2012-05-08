package de.fuberlin.projectci.parseTable;

import de.fuberlin.projectci.grammar.Grammar;
import java.util.Set;
import de.fuberlin.projectci.grammar.Symbol;
import java.util.List;

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
	 */
	public Set<LR0Item> closure(Set<LR0Item> items) {
		// TODO Implementiere mich
		return null;
	}
	 
	/**
	 *Vgl. Drachembuch: S.296
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
 

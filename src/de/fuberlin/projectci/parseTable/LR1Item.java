package de.fuberlin.projectci.parseTable;

import de.fuberlin.projectci.grammar.Production;
import de.fuberlin.projectci.grammar.TerminalSymbol;


/**
 * Erweitert LROItem um ein Lookahead-Symbol.
 * Drachenbuch: S. 313
 */
public class LR1Item extends LR0Item {
 
	private TerminalSymbol lookaheadSymbol;
	 
	public LR1Item(Production production, int index, TerminalSymbol lookaheadSymbol ) {
		super(production, index);
		this.lookaheadSymbol=lookaheadSymbol;
	}

	public TerminalSymbol getLookaheadSymbol() {
		return lookaheadSymbol;
	}
	
}
 

package de.fuberlin.projectci.parseTable;

import de.fuberlin.projectci.grammar.Grammar;

public abstract class ParseTableBuilder {
 
	private Grammar grammar;
	
	/**
	 * Initialisiert den ParseTableBuilder mit einer zugehörigen (erweiterten!) Grammatik.
	 * @param grammar eine erweiterte Grammatik.
	 */
	public ParseTableBuilder(Grammar grammar) {
		this.grammar=grammar;
	}
	 
	/**
	 * Fabrikmethode zum Erzeugen eines konkreten ParseTableBuilder für die übergebene Grammatik.
	 */
	public static ParseTableBuilder createParseTableBuilder(Grammar grammar){
		// Erst mal eine SLRParseTableBuilder verwenden. 
		// Kann später ggfs. gegen einen LALRParseTableBuilder ausgetauscht werden.
		return new SLRParseTableBuilder(grammar);
	}
	
	public Grammar getGrammar(){
		return grammar;
	}
	
	/**
	 * Baut die Parsetabelle (Action- und Goto-Tabelle) für die zugehörige Grammatik.
	 * @return eine Parsetabelle
	 * @throws InvalidGrammarException falls die Grammatik keine gültige (S|LA)LR-Grammatik ist.
	 */
	public abstract ParseTable buildParseTable() throws InvalidGrammarException;
}
 

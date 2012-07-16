package de.fuberlin.projectci;

import java.util.Map;
import java.util.Set;

import de.fuberlin.projectci.grammar.BNFGrammarReader;
import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;

public class Grammar_FIRST_FOLLOW_Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Grammar g = new Grammar();

		try {
			GrammarReader gReader = new BNFGrammarReader();
			g = gReader.readGrammar("./input/de/fuberlin/projectci/quellsprache_bnf.txt");
			
		} catch (BNFParsingErrorException e) {
			e.printStackTrace();
		} 
		
		System.out.println("=== Grammatik ===");
		System.out.println(g);
		System.out.println();

		System.out.println("=== FIRST ===");
		
		Map<Symbol,Set<TerminalSymbol>> firstSets  = g.calculateFirstSets();
		
		for(Symbol s :firstSets.keySet() ) {
			System.out.print(s);
			System.out.print("\t => {");
			for(TerminalSymbol t : firstSets.get(s)) {
				System.out.print(t.toString()+", ");
			}
			System.out.println("}");
		}
		
		
		System.out.println("=== FOLLOW ===");
		Map<NonTerminalSymbol, Set<TerminalSymbol>> followSets = g.calculateFollowSets();
		
		for(Symbol s :followSets.keySet() ) {
			System.out.print(s);
			System.out.print("\t => {");
			for(TerminalSymbol t : followSets.get(s)) {
				System.out.print(t.toString()+", ");
			}
			System.out.println("}");
		}

	}

}

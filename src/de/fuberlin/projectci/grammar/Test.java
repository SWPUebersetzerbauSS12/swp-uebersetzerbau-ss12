package de.fuberlin.projectci.grammar;


import java.util.Map;
import java.util.Set;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Grammar g = new Grammar();

		try {
			//g = GrammarReader.readGrammar("./doc/testFirst");
			//g = GrammarReader.readGrammar("./doc/praefixGrammatik4.28.txt");
			g = new GrammarReader().readGrammar("./doc/quellsprache_bnf.txt");
			
		} catch (BNFParsingErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println("=== Grammatik ===");
		System.out.println(g);
		System.out.println();
		
//		System.out.println("=== Grammatik erweitert ===");
//		LRParser.extendGrammar(g);
//		System.out.println(g);
//		System.out.println();

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

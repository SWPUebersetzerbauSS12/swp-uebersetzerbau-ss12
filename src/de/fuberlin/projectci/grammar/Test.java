package de.fuberlin.projectci.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Grammar g = new Grammar();
		
		try {
			g = GrammarReader.readGrammar("./doc/testFirst");
			
		} catch (BNFParsingErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		System.out.println(g);

		Map<Symbol,Set<TerminalSymbol>> firstSets  = g.computeFirstSets();
		
		for(Symbol s :firstSets.keySet() ) {
			System.out.print(s);
			System.out.print("\t => {");
			for(TerminalSymbol t : firstSets.get(s)) {
				System.out.print(t.toString()+", ");
			}
			System.out.println("}");
		}
	}

}

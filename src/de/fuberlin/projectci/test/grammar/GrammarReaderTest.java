package de.fuberlin.projectci.test.grammar;

import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;

public class GrammarReaderTest {

	/**
	 * @param args
	 * @throws BNFParsingErrorException 
	 */
	public static void main(String[] args){
		System.out.println("Ungültige Grammatik:");
		try {
			Grammar g = GrammarReader.readGrammar("./doc/beispielgrammatik1_ungueltig.txt");
			g.toString();
		} catch (BNFParsingErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		System.out.println("\nGültige Grammatik:");
		try {
			Grammar g2 = GrammarReader.readGrammar("./doc/beispielgrammatik1.txt");
			g2.toString();
		} catch (BNFParsingErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

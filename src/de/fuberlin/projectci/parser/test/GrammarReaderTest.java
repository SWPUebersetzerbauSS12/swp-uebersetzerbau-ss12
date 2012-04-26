package parser.test;

import parser.Grammar;
import parser.tools.BNFParsingErrorException;
import parser.tools.GrammarReader;

public class GrammarReaderTest {

	/**
	 * @param args
	 * @throws BNFParsingErrorException 
	 */
	public static void main(String[] args) throws BNFParsingErrorException {
		Grammar g = GrammarReader.readGrammar("./material/beispielgrammatik1_ungueltig.txt");
		g.toString();
	}

}

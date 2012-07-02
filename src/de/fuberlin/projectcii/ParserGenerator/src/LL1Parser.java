package de.fuberlin.projectcii.ParserGenerator.src;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.IParser;
import de.fuberlin.commons.parser.ISyntaxTree;


/**
 * 
 * LL1 Parser for a given TokenStream. The Parser uses the Grammar defined in
 * the Settings.ini file
 *
 */
public class LL1Parser implements IParser {
	
	private ParserGenerator pG;
	
	public LL1Parser(){
		try{
			Settings.initalize();
			pG = new ParserGenerator();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	


	/**
	 * This Methode creates a ParserTree from the parsergenerators data
	 * and a given Lexer.
	 * Decides whether parsing can be done based on the parsergenerators Parsertable
	 * @param lexer The Lexer used as input for the parser.
	 * @return ISyntaxTree The ParseTree;
	 * */
	@Override
	public ISyntaxTree parse(ILexer lexer, String grammar) {
	    boolean printSelected = true;
		ISyntaxTree parsetree = new SyntaxTree();
		
		try{
		    pG.initialize(false,grammar,printSelected);
		    //TODO wenn Grammatik LL1 wieder Abfrage nach LL1 Parsebarkeit benutzen
			TokenParser tP = new TokenParser(lexer, pG.getParseTable(),pG.getGrammar(),pG.getStartSymbol());
			parsetree = tP.parseTokenStream(printSelected);
		    /*
			if(pG.parsable_LL1(pG.getParseTable())){
			    
				TokenParser tP = new TokenParser(lexer, pG.getParseTable(),pG.getGrammar(),pG.getStartSymbol());
				parsetree = tP.parseTokenStream();
			}
			else{
			    pG.initialize(true,grammar);
			    if(pG.parsable_LL1(pG.getParseTable())){
	                TokenParser tP = new TokenParser(lexer, pG.getParseTable(),pG.getGrammar(),pG.getStartSymbol());
	                parsetree = tP.parseTokenStream();
	            }
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return parsetree;
	}
}

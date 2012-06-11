package de.fuberlin.projectcii.ParserGenerator.src;

import de.fuberlin.projectcii.ParserGenerator.src.extern.ILexer;
import de.fuberlin.projectcii.ParserGenerator.src.extern.ISyntaxTree;


public class LL1Parser {
	
	private ParserGenerator pG;
	
	public LL1Parser(){
		try{
			Settings.initalize();
			pG = new ParserGenerator();
			pG.initialize();
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
 * @author Ying Wei, Patrick Schlott
 * @param lexer The Lexer used as input for the parser.
 * @return ISyntaxTree The ParseTree;
 * */
	public ISyntaxTree getParserTree(ILexer lexer){
		ISyntaxTree parsetree = new SyntaxTree();
		try{
			if(pG.parsable_LL1(pG.getParseTable())){
				TokenParser tP = new TokenParser(lexer, pG.getParseTable(),pG.getGrammar(),pG.getStartSymbol());
				parsetree = tP.parseTokenStream();
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return parsetree;
	}
}

package de.fuberlin.projectcii.ParserGenerator.src;

import de.fuberlin.projectcii.ParserGenerator.src.extern.ILexer;


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
	
/*
 * first, check out the parsertable,
 * then decide to do the parsing and get tree ect. or not
 * @author Ying Wei
 * */

	public void getParserTree(ILexer lexer){
		try{
			if(pG.parsable_LL1(pG.getParseTable())){
				TokenParser tP = new TokenParser(lexer, pG.getParseTable(),pG.getGrammar(),pG.getStartSymbol());
				tP.parseTokenStream();
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

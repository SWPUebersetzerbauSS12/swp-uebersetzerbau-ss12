import java.io.IOException;


public class LL1Parser {
	
	private ParserGenerator pG;
	
	public LL1Parser() throws IOException{
		Settings.initalize();
		pG = new ParserGenerator();
		pG.initialize();
	}
	
/*
 * first, check out the parsertable,
 * then decide to do the parsing and get tree ect. or not
 * @author Ying Wei
 * */

	public void getSyntaxTree(){
		
		if(pG.parsable_LL1(pG.getParseTable())){
			TokenParser tP = new TokenParser(pG.getParseTable(),pG.getGrammar(),pG.getStartSymbol());
		tP.parseTokenStream();
		}
	}
}

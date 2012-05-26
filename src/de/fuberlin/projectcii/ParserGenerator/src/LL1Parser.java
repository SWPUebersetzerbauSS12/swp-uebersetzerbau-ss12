
public class LL1Parser {
	
	private ParserGenerator pG;
	
	public LL1Parser(String file){
		pG = new ParserGenerator();
		pG.createParserTable("language.txt");
	}

	public void getSyntaxTree(){
		TokenParser tP = new TokenParser();
		tP.ParseTokenStream(pG.getParseTable(),pG.getGrammar(),pG.getStartSymbol());
	}
}

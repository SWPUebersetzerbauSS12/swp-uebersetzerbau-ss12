
public class LL1Parser {
	
	private ParserGenerator pG;
	
	public LL1Parser(String file){
		Settings.initalize();
		pG = new ParserGenerator();
		pG.createParserTable("language.txt");
	}

	public void getSyntaxTree(){
		TokenParser tP = new TokenParser(pG.getParseTable(),pG.getGrammar(),pG.getStartSymbol());
		tP.parseTokenStream();
	}
}

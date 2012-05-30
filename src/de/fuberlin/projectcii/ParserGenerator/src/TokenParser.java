import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class TokenParser {

	private TestLexer lexer;
	private Map<String, HashMap<String,Vector<Integer>>> parserTable;
	private Map<String, Vector<Vector<String>>> grammar;
	private String StartSymbol;
	private String Token;
	
	public TokenParser(Map<String, HashMap<String,Vector<Integer>>> parserTable,
			 Map<String, Vector<Vector<String>>> grammar, String StartSymbol){
		lexer = new TestLexer();
		this.parserTable = parserTable;
		this.grammar = grammar;
		this.StartSymbol = StartSymbol;
	}
	
	public SyntaxTree parseTokenStream(){
		Token = lexer.getNextToken();
		SyntaxTree tree = parseToken(StartSymbol,new SyntaxTree());
		if (Token.equals(Settings.getEOF())){
			System.out.println("accepted");
		}
		tree.printTree();
		return tree;
	}
	
	private SyntaxTree parseToken(String symbol,SyntaxTree parent){
		
		SyntaxTree tree = new SyntaxTree();
		tree.setSymbol(symbol);
		tree.setParent(parent);
		
		if (symbol.equals(Token)){
			Token = lexer.getNextToken();
		}
		else if(parserTable.keySet().contains(symbol) &&
			   (parserTable.get(symbol).keySet().contains(Token))){
			String head = symbol;
			int productionNr = parserTable.get(head).get(Token).firstElement();
			Vector<String> Production = grammar.get(head).elementAt(productionNr);
			Printer.printProduction(grammar, head, productionNr);
			for (int i=0;i < Production.size() ;i++){
				if (!Production.elementAt(i).equals(Settings.getEPSILON())){
					tree.addChild(parseToken(Production.elementAt(i),tree));
				}
				else{
					SyntaxTree epsilonTree = new SyntaxTree();
					epsilonTree.setSymbol(Settings.getEPSILON());
					tree.addChild(epsilonTree);
				}
			}
		}
		else{
			System.out.println("ERROR!");
		}
	return tree;
	}
}

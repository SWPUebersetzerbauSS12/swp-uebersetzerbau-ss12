import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * A Table-navigated LL(1)-Parser. The Parser parses a TokenStream represented
 * by calls of the methode "getNextToken" based on a given grammar, Startsymbol
 * and Parsetable
 * 
 * @author Patrick Schlott
 *
 */
public class TokenParser {

	private TestLexer lexer;
	private Map<String, HashMap<String,Vector<Integer>>> parserTable;
	private Map<String, Vector<Vector<String>>> grammar;
	private String StartSymbol;
	private String Token;
	
	/**
	 * The Constructor initialising the Variables needed to parse a Tokenstream
	 * 
	 * @author Patrick Schlott
	 * @param parserTable The parsertable that is used to parse the Tokenstream
	 * @param grammar The LL(1) grammar that is used for this specific language
	 * @param StartSymbol The Starting symbol of the grammar
	 */
	public TokenParser(Map<String, HashMap<String,Vector<Integer>>> parserTable,
			 Map<String, Vector<Vector<String>>> grammar, String StartSymbol){
		lexer = new TestLexer();
		this.parserTable = parserTable;
		this.grammar = grammar;
		this.StartSymbol = StartSymbol;
	}
	
	/**
	 * 
	 * @author Patrick Schlott
	 *
	 * @return SyntaxTree The Parsetree created by parsing the Tokenstream
	 */
	public SyntaxTree parseTokenStream(){
		Token = lexer.getNextToken();
		// creates the parsetree
		SyntaxTree tree = parseToken(StartSymbol,new SyntaxTree());
		// accept if all Token have been parsed
		if (Token.equals(Settings.getEOF())){
			System.out.println("accepted");
		}
		tree.printTree();
		return tree;
	}
	
	/**
	 * 
	 * Parses the actual Tokenstream and creates a SyntaxTree from it
	 * 
	 * @author Patrick Schlott
	 *
	 * @param symbol The terminal or nonterminal that is next on the imaginary stack
	 * @param parent The Node that was reduced to the given symbol
	 * @return SyntaxTree The SyntaxTree created by paring the Tokenstream
	 */
	private SyntaxTree parseToken(String symbol,SyntaxTree parent){
		
		// initalise Node
		SyntaxTree tree = new SyntaxTree();
		tree.setSymbol(symbol);
		tree.setParent(parent);
		
		// Leaf(Terminal) reached. Get next Token to continue parsing with
		if (symbol.equals(Token)){
			Token = lexer.getNextToken();
		}
		// Check Parsertable for next Step (specifically check if symbol - Token field exists
		else if(parserTable.keySet().contains(symbol) &&
			   (parserTable.get(symbol).keySet().contains(Token))){
			String head = symbol;
			// get ProductionNr in grammar by consulting the parsetable
			int productionNr = parserTable.get(head).get(Token).firstElement();
			// get Production by Number
			Vector<String> Production = grammar.get(head).elementAt(productionNr);
			Printer.printProduction(grammar, head, productionNr);
			// reduce symbol to children acording to the selected production
			for (int i=0;i < Production.size() ;i++){
				// Create Child-Nodes recursively for all childs
				if (!Production.elementAt(i).equals(Settings.getEPSILON())){
					tree.addChild(parseToken(Production.elementAt(i),tree));
				}
				// add a epsilon childNode to the tree
				else{
					SyntaxTree epsilonTree = new SyntaxTree();
					epsilonTree.setSymbol(Settings.getEPSILON());
					tree.addChild(epsilonTree);
				}
			}
		}
		// Token doesn't fit the given grammar
		else{
			System.out.println("ERROR!");
		}
	return tree;
	}
}

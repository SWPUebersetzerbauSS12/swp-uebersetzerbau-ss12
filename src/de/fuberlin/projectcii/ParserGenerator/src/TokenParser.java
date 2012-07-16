package de.fuberlin.projectcii.ParserGenerator.src;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.commons.parser.StringSymbol;


/**
 * 
 * A Table-navigated LL(1)-Parser. The Parser parses a TokenStream represented
 * by calls of the methode "getNextToken" based on a given grammar, Startsymbol
 * and Parsetable
 *
 */
public class TokenParser {

	private ILexer lexer;
	private Map<String, HashMap<String,Vector<Integer>>> parserTable;
	private Map<String, Vector<Vector<String>>> grammar;
	private String StartSymbol;
	private IToken Token;
	private String TokenTerminal;
	
	/**
	 * The Constructor initialising the Variables needed to parse a Tokenstream
	 * 
	 * @param parserTable The parsertable that is used to parse the Tokenstream
	 * @param grammar The LL(1) grammar that is used for this specific language
	 * @param StartSymbol The Starting symbol of the grammar
	 */
	public TokenParser(ILexer lexer, Map<String, HashMap<String,Vector<Integer>>> parserTable,
			 Map<String, Vector<Vector<String>>> grammar, String StartSymbol){
		this.lexer = lexer;
		this.parserTable = parserTable;
		this.grammar = grammar;
		this.StartSymbol = StartSymbol;
	}

	/**
	 *
	 * @param printSelected 
	 * @return SyntaxTree The Parsetree created by parsing the Tokenstream
	 * @throws RuntimeException No 'EOF' too much symbols
	 */
	public SyntaxTree parseTokenStream(boolean printSelected){
		getNextToken();
		// creates the parsetree
		if (Settings.getPARSING_STEPS() && printSelected){
		    System.out.println("Parsingschritte:");
		}
		SyntaxTree tree = parseToken(StartSymbol,new SyntaxTree(),printSelected);
		if (TokenTerminal.equals(Settings.getEOF())){
			System.out.println("Tokenstream accepted");
		}else{
			throw new RuntimeException("Too Much Symbols, No 'EOF'");
		}
		if (printSelected){
		    tree.printTree();
		}
		//tree.CompressSyntaxTree();
		return tree;
	}

	/**
	 * 
	 * Parses the actual Tokenstream and creates a SyntaxTree from it
	 *
	 * @param symbol The terminal or nonterminal that is next on the imaginary stack
	 * @param parent The Node that was reduced to the given symbol
	 * @param printSelected 
	 * @return SyntaxTree The SyntaxTree created by paring the Tokenstream
	 * @throws RuntimeException if it could not be found fixed productions 
	 */
	private SyntaxTree parseToken(String symbol,SyntaxTree parent, boolean printSelected){
		
		// initalise Node
		SyntaxTree tree = new SyntaxTree();
		tree.setSymbol(new StringSymbol(symbol));
		tree.setParent(parent);
		
		// Leaf(Terminal) reached. Get next Token to continue parsing with
		if (symbol.equals(TokenTerminal)){
		    tree.setToken(Token);
			getNextToken();
		}
		// Check Parsertable for next Step (specifically check if symbol - Token field exists
		else if(parserTable.keySet().contains(symbol) &&
			   !(parserTable.get(symbol).get(TokenTerminal).isEmpty())){
			String head = symbol;
			// get ProductionNr in grammar by consulting the parsetable
			int productionNr = parserTable.get(head).get(TokenTerminal).firstElement();
			// get Production by Number
			Vector<String> Production = grammar.get(head).elementAt(productionNr);
			if (Settings.getPARSING_STEPS() && printSelected){
			    Printer.printProduction(grammar, head, productionNr);
			}
			// reduce symbol to children acording to the selected production
			for (int i=0;i < Production.size() ;i++){
				// Create Child-Nodes recursively for all childs
				if (!Production.elementAt(i).equals(Settings.getEPSILON())){
					tree.addChild(parseToken(Production.elementAt(i),tree,printSelected));
				}
				// add an epsilon childNode to the tree
//				else{
//					SyntaxTree epsilonTree = new SyntaxTree();
//					epsilonTree.setSymbol(Settings.getEPSILON());
//					tree.addChild(epsilonTree);
//				}
			}
		}
		// Token doesn't fit the given grammar
		else{
			System.out.println(symbol);
			throw new RuntimeException("Token doesen't fit the given grammar! Terminal: "+TokenTerminal+" At line: "+Token.getLineNumber());			
		}
	return tree;
	}
	
	
	/**
	 * Helping method while IToken has not the value of Symbols as they are defined in grammar
	 * 
	 */
	private void getNextToken() {
		Token = lexer.getNextToken();
		TokenTerminal = Token.getText();
	}
}

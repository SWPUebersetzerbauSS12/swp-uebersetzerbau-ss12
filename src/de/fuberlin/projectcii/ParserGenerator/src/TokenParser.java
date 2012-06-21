package de.fuberlin.projectcii.ParserGenerator.src;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.lexer.IToken;


/**
 * 
 * A Table-navigated LL(1)-Parser. The Parser parses a TokenStream represented
 * by calls of the methode "getNextToken" based on a given grammar, Startsymbol
 * and Parsetable
 *
 */
public class TokenParser {
	//ToDo: entfernen wenn IToken angepasst
	private HashMap<String,String> Terminals = new HashMap<String,String>();
	
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
		
		Terminals.put("OP_LE","<");
		Terminals.put("OP_LE","<=");
		Terminals.put("OP_EQ","==");
		Terminals.put("OP_NE","!=");
		Terminals.put("OP_GT",">");
		Terminals.put("OP_GE",">=");
		Terminals.put("OP_OR","||");
		Terminals.put("OP_AND", "&&");
		Terminals.put("OP_NOT","!");
		Terminals.put("OP_ADD","+");
		Terminals.put("OP_MINUS","-");
		Terminals.put("OP_MUL","*");
		Terminals.put("OP_DIV","/");
		Terminals.put("OP_ASSIGN","=");
		Terminals.put("OP_COMMA",",");
		Terminals.put("OP_DOT",".");
		Terminals.put("OP_SEMIC",";");
		Terminals.put("IF","if");
		Terminals.put("THEN","then");
		Terminals.put("ELSE","else");
		Terminals.put("WHILE","while");
		Terminals.put("DO","do");
		Terminals.put("BREAK","break");
		Terminals.put("RETURN","return");
		Terminals.put("PRINT","print");
		Terminals.put("DEF","def");
		Terminals.put("BASIC","basic");
		Terminals.put("RECORD","record");
		Terminals.put("ID","id");
		//Terminals.put("BOOL_LITERAL","bool");
		Terminals.put("STRING_LITERAL","string");
		Terminals.put("INT_LITERAL","num");
		Terminals.put("LPAREN","(");
		Terminals.put("RPAREN",")");
		Terminals.put("LBRACKET","[");
		Terminals.put("RBRACKET","]");
		Terminals.put("LBRACE","{");
		Terminals.put("RBRACE","}");
		Terminals.put("EOF","eof");
	}

	/**
	 *
	 * @return SyntaxTree The Parsetree created by parsing the Tokenstream
	 * @throws RuntimeException No 'EOF' too much symbols
	 */
	public SyntaxTree parseTokenStream(){
		getNextToken();
		// creates the parsetree
		SyntaxTree tree = parseToken(StartSymbol,new SyntaxTree());
		if (TokenTerminal.equals(Settings.getEOF())){
			System.out.println("accepted");
		}else{
			throw new RuntimeException("Too Much Symbols, No 'EOF'");
		}
		tree.printTree();
		//tree.CompressSyntaxTree();
		return tree;
	}

	/**
	 * 
	 * Parses the actual Tokenstream and creates a SyntaxTree from it
	 *
	 * @param symbol The terminal or nonterminal that is next on the imaginary stack
	 * @param parent The Node that was reduced to the given symbol
	 * @return SyntaxTree The SyntaxTree created by paring the Tokenstream
	 * @throws RuntimeException if it could not be found fixed productions 
	 */
	private SyntaxTree parseToken(String symbol,SyntaxTree parent){
		
		// initalise Node
		SyntaxTree tree = new SyntaxTree();
		tree.setSymbol(symbol);
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
			Printer.printProduction(grammar, head, productionNr);
			// reduce symbol to children acording to the selected production
			for (int i=0;i < Production.size() ;i++){
				// Create Child-Nodes recursively for all childs
				if (!Production.elementAt(i).equals(Settings.getEPSILON())){
					tree.addChild(parseToken(Production.elementAt(i),tree));
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
		if(Token.getType().equals("BOOL_LITERAL"))
		{			
			if((Boolean)Token.getAttribute())
				TokenTerminal = "true";
			else
				TokenTerminal = "false";
		}
		else
		{
			TokenTerminal = Terminals.get(Token.getType());			
		}		
	}
}

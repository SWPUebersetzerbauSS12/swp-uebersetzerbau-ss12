package parser;

import java.util.Stack;
import java.util.Vector;

import lexer.ILexer;
import lexer.IToken.TokenType;
import lexer.SyntaxErrorException;
import lexer.Token;
import lombok.Getter;

public class Parser {
	private ILexer lexer;
	private ParseTable table;
	private Stack<String> stack;
	private Vector<String> outputs;

	@Getter
	private ISyntaxTree syntaxTree;

	private static final String[] nonTerminals = { "program", "funcs", "func",
			"func'", "optparams", "params", "params'", "block", "decls",
			"decl", "type", "type'", "stmts", "stmt", "stmt'", "stmt''", "loc",
			"loc'", "loc''", "assign", "assign'", "bool", "bool'", "join",
			"join'", "equality", "equality'", "rel", "rel'", "expr", "expr'",
			"term", "term'", "unary", "factor", "factor'", "optargs", "args",
			"args'", "basic" };
	private static final String[] terminals = { "DEF", "ID", "LPAREN",
			"RPAREN", "OP_SEMIC", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET",
			"INT", "IF", "WHILE", "DO", "BREAK", "RETURN", "PRINT", "ELSE",
			"OP_ASSIGN", "OP_OR", "OP_AND", "OP_EQ", "OP_NE", "OP_LT", "OP_LE",
			"OP_GE", "OP_GT", "OP_ADD", "OP_MINUS", "OP_MUL", "OP_DIV",
			"OP_NOT", "REAL", "STRING", "OP_COMMA", "EOF", "OP_DOT" };

	private static final String EPSILON = "ε";

	public Parser(ILexer lexer) {
		this.lexer = lexer;

		table = new ParseTable(nonTerminals, terminals);
		try {
			fillParseTable();
		} catch (ParserException e) {
			e.printStackTrace();
		}

		stack = new Stack<String>();
		outputs = new Vector<String>();
		syntaxTree = new NonTerminal("program");
	}

	private void initStack() {
		stack.clear();
		stack.push("$");
		stack.push("program");
	}

	private boolean isTerminal(String s) {
		for (int i = 0; i < terminals.length; i++) {
			if (terminals[i].equals(s))
				return true;
		}

		return false;
	}

	public void parse() throws ParserException {

		if (table.isAmbigous()) {
			throw new ParserException(
					"Parsing table is ambigous! Won't start syntax analysis");
		}

		initStack();
		String peek;
		Token token = null;
		try {
			token = lexer.getNextToken();
		} catch (SyntaxErrorException e) {
			e.printStackTrace();
		}

		do {
			peek = stack.peek();
			if (isTerminal(peek) || peek.equals("$")) {
				if (peek.equals(token.getType().toString())
						|| (peek.equals("$") && token.getType() == TokenType.EOF)) {

					stack.pop();
					try {
						token = lexer.getNextToken();
					} catch (SyntaxErrorException e) {
						e.printStackTrace();
					}
				} else {
					throw new ParserException("Wrong token " + token
							+ " in input");
				}

			} else /** stack symbol is non-terminal */
			{
				String prod;
				if ((prod = table.getEntry(peek, token.getType().toString())) != null) {
					stack.pop();

					String[] tmp = prod.split("::=");
					if (tmp.length == 2) {
						String[] prods = tmp[1].split(" ");
						for (int i = prods.length - 1; i >= 0; i--) {
							if (!prods[i].trim().equals("")
									&& !prods[i].equals(EPSILON)) {
								stack.push(prods[i]);
							}
						}
						outputs.add(prod);
					} else if (prod.trim().equals("")) {
						throw new ParserException(
								" Syntax error: No Rule in parsing table (Stack: "
										+ peek + ", token: " + token + ")");
					} else {
						throw new ParserException(
								"Wrong structur in parsing table! Productions should "
										+ "be of the form: X ::= Y1 Y2 ... Yk! Problem with: "
										+ prod + "(Stack: " + peek
										+ ", token: " + token + ")");
					}
				} else {
					throw new ParserException(
							" Syntax error: No Rule in parsing table (Stack: "
									+ peek + ", token: " + token + ")");
				}
			}
		} while (!stack.peek().equals("$"));

		createSyntaxTree();
	}

	/**
	 * Call to create the corresponding parse tree from the previous parse call.
	 */
	private void createSyntaxTree() {
		for (String t : outputs) {

			String[] tmp = t.split("::=");
			String[] tmp2 = tmp[1].split(" ");

			ISyntaxTree node = getNextOccurrence(tmp[0].trim());
			for (int i = 0; i < tmp2.length; i++) {
				if (!tmp2[i].equals("")) {
					ISyntaxTree newNode;
					if (isAllUpper(tmp2[i]) || tmp2[i].equals(EPSILON)) {
						newNode = new Terminal(tmp2[i]);
					} else {
						newNode = new NonTerminal(tmp2[i]);
					}
					node.addTree(newNode);
				}
			}
		}

		printParseTree(syntaxTree,0);
	}

	/**
	 * Helper function to distinguish between terminals and non-terminals.
	 * 
	 * @param s
	 * @return
	 */
	private boolean isAllUpper(String s) {
		for (char c : s.toCharArray()) {
			if (Character.isLetter(c) && Character.isLowerCase(c)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Using Depth-First search to find the node.
	 * 
	 * @param name
	 * @return the next occurrence of the given node, if it has no children yet.
	 */
	private ISyntaxTree getNextOccurrence(String name) {

		if (syntaxTree.getName().equals(name)
				&& syntaxTree.getChildrenCount() == 0) {
			return syntaxTree;
		}

		Stack<ISyntaxTree> stack = new Stack<ISyntaxTree>();
		stack.push(syntaxTree);

		while (!stack.isEmpty()) {
			ISyntaxTree tmp = stack.pop();
			if (tmp.getName().equals(name) && tmp.getChildrenCount() == 0) {
				return tmp;
			} else // push all children onto the stack backwards
			{
				int count = tmp.getChildrenCount();
				for (int i = count - 1; i >= 0; i--) {
					stack.push(tmp.getChild(i));
				}
			}
		}
		return null;
	}

	/**
	 * Prints the generated parse tree turned by 90 degree clockwise.
	 * Read direction is still from the top to the bottom.
	 * 
	 * @param tree
	 * @param depth
	 */
	private void printParseTree(ISyntaxTree tree, int depth) {
		
		for(int i = 0; i <= depth; i++ ){
			System.out.print("\t");
		}
		System.out.print(tree.getName());
		System.out.println("");
		int count = tree.getChildrenCount();
		if (count != 0) {
			for (int i = 0; i < count; i++) {
				printParseTree(tree.getChild(i), depth +1);
			}
		}
	}

	/**
	 * Cells should be filled by Productions of the form: X ::= Y1 Y2 ... Yk
	 * Treats basic as { INT, REAL, STRING }!
	 * 
	 * TODO: true, false, record missing
	 * 
	 * @throws ParserException
	 */
	private void fillParseTable() throws ParserException {
		// program
		table.setEntry("program", "DEF", "program ::= funcs");

		// funcs
		table.setEntry("funcs", "DEF", "funcs ::=  func funcs");
		table.setEntry("funcs", "EOF", "funcs ::= ε");

		// func'
		table.setEntry("func", "DEF",
				"func ::=  DEF type ID LPAREN optparams RPAREN func'");
		table.setEntry("func'", "OP_SEMIC", "func' ::= OP_SEMIC");
		table.setEntry("func'", "LBRACE", "func' ::= block");

		// optparams
		table.setEntry("optparams", "RPAREN", "optparams ::= ε");
		table.setEntry("optparams", "INT", "optparams ::= params");
		table.setEntry("optparams", "REAL", "optparams ::= params");
		table.setEntry("optparams", "STRING", "optparams ::= params");

		// params
		table.setEntry("params", "INT", "params ::= type ID params'");
		table.setEntry("params", "REAL", "params ::= type ID params'");
		table.setEntry("params", "STRING", "params ::= type ID params'");

		// params'
		table.setEntry("params'", "RPAREN", "params' ::= ε");
		table.setEntry("params'", "OP_COMMA", "params' ::= OP_COMMA params");

		// block
		table.setEntry("block", "LBRACE", "block ::= LBRACE decls stmts RBRACE");

		// decls
		table.setEntry("decls", "ID", "decls ::= ε");
		table.setEntry("decls", "LBRACE", "decls ::= ε");
		table.setEntry("decls", "RBRACE", "decls ::= ε");
		table.setEntry("decls", "IF", "decls ::= ε");
		table.setEntry("decls", "WHILE", "decls ::= ε");
		table.setEntry("decls", "DO", "decls ::= ε");
		table.setEntry("decls", "BREAK", "decls ::= ε");
		table.setEntry("decls", "RETURN", "decls ::= ε");
		table.setEntry("decls", "PRINT", "decls ::= ε");
		table.setEntry("decls", "INT", "decls ::=  decl decls");
		table.setEntry("decls", "REAL", "decls ::=  decl decls");
		table.setEntry("decls", "STRING", "decls ::=  decl decls");

		// decl
		table.setEntry("decl", "INT", "decl ::=  type ID OP_SEMIC");
		table.setEntry("decl", "REAL", "decl ::=  type ID OP_SEMIC");
		table.setEntry("decl", "STRING", "decl ::=  type ID OP_SEMIC");

		// type
		table.setEntry("type", "INT", "type ::=  basic type'");
		table.setEntry("type", "REAL", "type ::=  basic type'");
		table.setEntry("type", "STRING", "type ::=  basic type'");

		// type'
		table.setEntry("type'", "ID", "type' ::= ε");
		table.setEntry("type'", "LBRACKET",
				"type' ::= LBRACKET INT RBRACKET type' ");

		// stmts
		table.setEntry("stmts", "ID", "stmts ::= stmt stmts");
		table.setEntry("stmts", "LBRACE", "stmts ::= stmt stmts");
		table.setEntry("stmts", "IF", "stmts ::= stmt stmts");
		table.setEntry("stmts", "WHILE", "stmts ::= stmt stmts");
		table.setEntry("stmts", "DO", "stmts ::= stmt stmts");
		table.setEntry("stmts", "BREAK", "stmts ::= stmt stmts");
		table.setEntry("stmts", "RETURN", "stmts ::= stmt stmts");
		table.setEntry("stmts", "PRINT", "stmts ::= stmt stmts");
		table.setEntry("stmts", "RBRACE", "stmts ::= ε");

		// stmt
		table.setEntry("stmt", "LBRACE", "stmt ::= block");
		table.setEntry("stmt", "IF",
				"stmt ::= IF LPAREN assign RPAREN stmt stmt'");
		table.setEntry("stmt", "WHILE",
				"stmt ::= WHILE LPAREN assign RPAREN stmt ");
		table.setEntry("stmt", "DO",
				"stmt ::= DO stmt WHILE LPAREN assign RPAREN OP_SEMIC ");
		table.setEntry("stmt", "BREAK", "stmt ::= BREAK OP_SEMIC ");
		table.setEntry("stmt", "RETURN", "stmt ::= RETURN stmt''");
		table.setEntry("stmt", "PRINT", "stmt ::= PRINT loc OP_SEMIC ");

		// stmt'
		table.setEntry("stmt'", "ID", "stmt' ::= ε");
		table.setEntry("stmt'", "LBRACE", "stmt' ::= ε");
		table.setEntry("stmt'", "IF", "stmt' ::= ε");
		table.setEntry("stmt'", "WHILE", "stmt' ::= ε");
		table.setEntry("stmt'", "DO", "stmt' ::= ε");
		table.setEntry("stmt'", "BREAK", "stmt' ::= ε");
		table.setEntry("stmt'", "RETURN", "stmt' ::= ε");
		table.setEntry("stmt'", "PRINT", "stmt' ::= ε");
		table.setEntry("stmt'", "ELSE", "stmt' ::= ELSE stmt");

		// stmt''
		table.setEntry("stmt''", "ID", "stmt'' ::= loc OP_SEMIC");
		table.setEntry("stmt''", "OP_SEMIC", "stmt'' ::= OP_SEMIC");

		// loc
		table.setEntry("loc", "ID", "loc ::=   ID loc'' ");

		// loc'
		table.setEntry("loc'", "LBRACKET", "loc' ::= LBRACKET assign RBRACKET");
		table.setEntry("loc'", "OP_DOT", "loc' ::= OP_DOT ID");

		// loc''
		table.setEntry("loc''", "LBRACKET", "loc'' ::= loc' loc'' ");
		table.setEntry("loc''", "OP_DOT", "loc'' ::= loc' loc'' ");

		// assign
		table.setEntry("assign", "ID", "assign ::= bool assign'");
		table.setEntry("assign", "LPAREN", "assign ::= bool assign'");
		table.setEntry("assign", "INT", "assign ::= bool assign'");
		table.setEntry("assign", "OP_MINUS", "assign ::= bool assign'");
		table.setEntry("assign", "OP_NOT", "assign ::= bool assign'");
		table.setEntry("assign", "REAL", "assign ::= bool assign'");
		table.setEntry("assign", "STRING", "assign ::= bool assign'");

		// assign'
		table.setEntry("assign'", "OP_ASSIGN",
				"assign' ::= OP_ASSIGN assign assign'");
		table.setEntry("assign'", "RPAREN", "assign' ::= ε ");
		table.setEntry("assign'", "RBRACKET", "assign' ::= ε ");
		table.setEntry("assign'", "OP_COMMA", "assign' ::= ε ");
		// table.setEntry("assign'", "OP_ASSIGN", "assign' ::= ε ");
		// TODO: This is right but still leads to errors

		// bool
		table.setEntry("bool", "ID", "bool ::=  join bool'");
		table.setEntry("bool", "LPAREN", "bool ::=  join bool'");
		table.setEntry("bool", "INT", "bool ::=  join bool'");
		table.setEntry("bool", "OP_MINUS", "bool ::=  join bool'");
		table.setEntry("bool", "OP_NOT", "bool ::=  join bool'");
		table.setEntry("bool", "REAL", "bool ::=  join bool'");
		table.setEntry("bool", "STRING", "bool ::=  join bool'");

		// bool'
		table.setEntry("bool'", "RPAREN", "bool' ::= ε ");
		table.setEntry("bool'", "RBRACKET", "bool' ::= ε ");
		table.setEntry("bool'", "OP_ASSIGN", "bool' ::= ε ");
		table.setEntry("bool'", "OP_COMMA", "bool' ::= ε ");
		table.setEntry("bool'", "OP_OR", "bool' ::= OP_OR join bool'");

		// join
		table.setEntry("join", "ID", "join ::= equality join'");
		table.setEntry("join", "LPAREN", "join ::= equality join'");
		table.setEntry("join", "INT", "join ::= equality join'");
		table.setEntry("join", "OP_MINUS", "join ::= equality join'");
		table.setEntry("join", "OP_NOT", "join ::= equality join'");
		table.setEntry("join", "REAL", "join ::= equality join'");
		table.setEntry("join", "STRING", "join ::= equality join'");

		// join'
		table.setEntry("join'", "RPAREN", "join' ::= ε ");
		table.setEntry("join'", "RBRACKET", "join' ::= ε ");
		table.setEntry("join'", "OP_ASSIGN", "join' ::= ε ");
		table.setEntry("join'", "OP_COMMA", "join' ::= ε ");
		table.setEntry("join'", "OP_OR", "join' ::= ε ");

		// equality
		table.setEntry("equality", "ID", "equality ::= rel equality'");
		table.setEntry("equality", "LPAREN", "equality ::= rel equality'");
		table.setEntry("equality", "INT", "equality ::= rel equality'");
		table.setEntry("equality", "OP_MINUS", "equality ::= rel equality'");
		table.setEntry("equality", "OP_NOT", "equality ::= rel equality'");
		table.setEntry("equality", "REAL", "equality ::= rel equality'");
		table.setEntry("equality", "STRING", "equality ::= rel equality'");

		// equality'
		table.setEntry("equality'", "RPAREN", "equality' ::= ε ");
		table.setEntry("equality'", "RBRACKET", "equality' ::= ε ");
		table.setEntry("equality'", "OP_ASSIGN", "equality' ::= ε ");
		table.setEntry("equality'", "OP_COMMA", "equality' ::= ε ");
		table.setEntry("equality'", "OP_OR", "equality' ::= ε ");
		table.setEntry("equality'", "OP_AND", "equality' ::= ε ");
		table.setEntry("equality'", "OP_EQ",
				"equality' ::= OP_EQ rel equality'");
		table.setEntry("equality'", "OP_NE",
				"equality' ::= OP_NE rel equality'");

		// rel
		table.setEntry("rel", "ID", "rel ::= expr  rel'");
		table.setEntry("rel", "LPAREN", "rel ::= expr  rel'");
		table.setEntry("rel", "INT", "rel ::= expr  rel'");
		table.setEntry("rel", "OP_MINUS", "rel ::= expr  rel'");
		table.setEntry("rel", "OP_NOT", "rel ::= expr  rel'");
		table.setEntry("rel", "REAL", "rel ::= expr  rel'");
		table.setEntry("rel", "STRING", "rel ::= expr  rel'");

		// rel'
		table.setEntry("rel'", "RPAREN", "rel' ::= ε ");
		table.setEntry("rel'", "RBRACKET", "rel' ::= ε ");
		table.setEntry("rel'", "OP_ASSIGN", "rel' ::= ε ");
		table.setEntry("rel'", "OP_COMMA", "rel' ::= ε ");
		table.setEntry("rel'", "OP_OR", "rel' ::= ε ");
		table.setEntry("rel'", "OP_AND", "rel' ::= ε ");
		table.setEntry("rel'", "OP_EQ", "rel' ::= ε ");
		table.setEntry("rel'", "OP_NE", "rel' ::= ε ");
		table.setEntry("rel'", "OP_LT", "rel' ::= OP_LT expr");
		table.setEntry("rel'", "OP_LE", "rel' ::= OP_LE expr");
		table.setEntry("rel'", "OP_GE", "rel' ::= OP_GE expr");
		table.setEntry("rel'", "OP_GT", "rel' ::= OP_GT expr");

		// expr
		table.setEntry("expr", "ID", "expr ::= term expr'");
		table.setEntry("expr", "LPAREN", "expr ::= term expr'");
		table.setEntry("expr", "INT", "expr ::= term expr'");
		table.setEntry("expr", "OP_MINUS", "expr ::= term expr'");
		table.setEntry("expr", "OP_NOT", "expr ::= term expr'");
		table.setEntry("expr", "REAL", "expr ::= term expr'");
		table.setEntry("expr", "STRING", "expr ::= term expr'");

		// expr'
		table.setEntry("expr'", "OP_LT", "expr' ::= ε ");
		table.setEntry("expr'", "OP_LE", "expr' ::= ε ");
		table.setEntry("expr'", "OP_GE", "expr' ::= ε ");
		table.setEntry("expr'", "OP_GT", "expr' ::= ε ");
		table.setEntry("expr'", "OP_ADD", "expr' ::= OP_ADD term expr'");
		table.setEntry("expr'", "OP_MINUS", "expr' ::= OP_MINUS term expr'");

		// term
		table.setEntry("term", "ID", "expr ::= unary term'");
		table.setEntry("term", "LPAREN", "expr ::= unary term'");
		table.setEntry("term", "INT", "expr ::= unary term'");
		table.setEntry("term", "OP_MINUS", "expr ::= unary term'");
		table.setEntry("term", "OP_NOT", "expr ::= unary term'");
		table.setEntry("term", "REAL", "expr ::= unary term'");
		table.setEntry("term", "STRING", "expr ::= unary term'");

		// term'
		table.setEntry("term'", "OP_LT", "term' ::= ε ");
		table.setEntry("term'", "OP_LE", "term' ::= ε ");
		table.setEntry("term'", "OP_GE", "term' ::= ε ");
		table.setEntry("term'", "OP_GT", "term' ::= ε ");
		table.setEntry("term'", "OP_ADD", "term' ::= ε ");
		table.setEntry("term'", "OP_MINUS", "term' ::= ε ");
		table.setEntry("term'", "OP_MUL", "term' ::= OP_MUL unary term'");
		table.setEntry("term'", "OP_DIV", "term' ::= OP_DIV unary term'");

		// unary
		table.setEntry("unary", "ID", "unary ::= factor");
		table.setEntry("unary", "LPAREN", "unary ::= factor");
		table.setEntry("unary", "INT", "unary ::= factor");
		table.setEntry("unary", "REAL", "unary ::= factor");
		table.setEntry("unary", "STRING", "unary ::= factor");
		table.setEntry("unary", "OP_MINUS", "unary ::= OP_MINUS unary");
		table.setEntry("unary", "OP_NOT", "unary ::= OP_NOT unary");

		// factor
		table.setEntry("factor", "ID", "factor ::= loc factor'");
		table.setEntry("factor", "LPAREN", "factor ::= LPAREN assign RPAREN");
		table.setEntry("factor", "INT", "factor ::= INT");
		table.setEntry("factor", "REAL", "factor ::= REAL");
		table.setEntry("factor", "STRING", "factor ::= STRING");

		// factor'
		table.setEntry("factor'", "LPAREN",
				"factor' ::= LPAREN optargs RPAREN ");
		table.setEntry("factor'", "OP_LT", "factor' ::= ε ");
		table.setEntry("factor'", "OP_LE", "factor' ::= ε ");
		table.setEntry("factor'", "OP_GE", "factor' ::= ε ");
		table.setEntry("factor'", "OP_GT", "factor' ::= ε ");
		table.setEntry("factor'", "OP_ADD", "factor' ::= ε ");
		table.setEntry("factor'", "OP_MINUS", "factor' ::= ε ");
		table.setEntry("factor'", "OP_MUL", "factor' ::= ε ");
		table.setEntry("factor'", "OP_DIV", "factor' ::= ε ");

		// optargs
		table.setEntry("optargs", "ID", "optargs ::= args");
		table.setEntry("optargs", "RPAREN", "optargs ::= ε");

		// args
		table.setEntry("args", "ID", "args ::= assign args'");
		table.setEntry("args", "LPAREN", "args ::= assign args'");
		table.setEntry("args", "INT", "args ::= assign args'");
		table.setEntry("args", "OP_MINUS", "args ::= assign args'");
		table.setEntry("args", "OP_NOT", "args ::= assign args'");
		table.setEntry("args", "REAL", "args ::= assign args'");
		table.setEntry("args", "STRING", "args ::= assign args'");

		// args'
		table.setEntry("args'", "RPAREN", "agrs' ::= ε");
		table.setEntry("args'", "OP_COMMA", "args' ::= OP_COMMA args");

		// this is just for testing !!!
		// type
		table.setEntry("basic", "INT", "basic ::=  INT");
		table.setEntry("basic", "REAL", "basic ::= REAL");
		table.setEntry("basic", "STRING", "basic ::=  STRING");
	}

}

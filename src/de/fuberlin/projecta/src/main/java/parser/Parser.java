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
	@Getter private ISyntaxTree syntaxTree;

	private static final String[] nonTerminals = { "program", "funcs", "func",
			"optparams", "params", "block", "decls", "decl", "type", "stmts",
			"assign", "bool", "join", "equality", "rel", "expr", "term",
			"unary", "factor", "funccall", "args", "stmt", "loc" };
	private static final String[] terminals = { "def", "id", "(", ")", ";",
			",", "{", "}", "[", "]", "basic", "record", "if", "else", "while",
			"do", "break", "return", "print", ".", "=", "||", "&&", "==", "!=",
			"<", "<=", ">=", ">", "+", "-", "*", "/", "!", "num", "real",
			"true", "false", "string" };

	/**
	 * Cells should be filled by Productions of the form: X ::= Y1 Y2 ... Yk
	 */
	private void fillParseTable() {
		// TODO: we can do this by hand...
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

	public Parser(ILexer lexer) {
		this.lexer = lexer;

		table = new ParseTable(nonTerminals, terminals);
		fillParseTable();

		stack = new Stack<String>();
		outputs = new Vector<String>();
	}

	public void parse() throws ParserException {

		if (table.isAmbigous()) {
			throw new ParserException(
					"Parsing table is ambigous! Won't start syntax analysis");
		}

		initStack();
		String X;
		Token a = null;
		try {
			a = lexer.getNextToken();
		} catch (SyntaxErrorException e) {
			e.printStackTrace();
		}

		do {
			X = stack.peek();
			if (isTerminal(X) || X.equals("$")) {
				if (a.getAttribute() != null) {
					if (X.equals(a.getAttribute())
							|| X.equals(a.getType().toString().toLowerCase())
							|| X.equals(getStringFromType(a.getType())) 
							|| X.equals("$") && a == null) /** null is returned if input ended  */ {
						stack.pop();
						try {
							a = lexer.getNextToken();
						} catch (SyntaxErrorException e) {
							e.printStackTrace();
						}
					} else {
						throw new ParserException("Wrong token "
								+ a + " in input");
					}
				}
			} else /** stack symbol is non-terminal  */{
				String prod;
				if ((prod = table.getEntry(X, a.getAttribute())) != null){
					// my heart skips skips..
				} else if ((prod = table.getEntry(X, a.getType().toString().toLowerCase())) != null){
					// skips skips..
				} else if ((prod = table.getEntry(X, getStringFromType(a.getType()))) != null){
					// skips skips a beat
				}
				
				if (prod != null) {
					stack.pop();
					String[] tmp = prod.split("::=");
					if (tmp.length == 2) {
						String[] prods = tmp[1].split(" ");
						for (int i = prods.length - 1; i >= 0; i--) {
							stack.push(prods[i]);
						}
						outputs.add(prod);
					} else {
						throw new ParserException(
								"Wrong structur in parsing table! Productions should be of the form: X ::= Y1 Y2 ... Yk");
					}
				} else {
					throw new ParserException(" Syntax error: No Rule in parsing table ");
				}
			}
		} while (!stack.peek().equals("$"));
		
		createSyntaxTree();
	}

	private void createSyntaxTree(){
		
	}
	
	/**
	 * Helper function to re-extract string from token.
	 * 
	 * @param type
	 *            the token type
	 * @return the corresponding sign in the input
	 */
	private String getStringFromType(TokenType type) {
		switch (type) {
		case ASSIGN:
			return "=";
		case BRL:
			return "(";
		case BRR:
			return ")";
		case SBRL:
			return "[";
		case SBRR:
			return "]";
		case CBRL:
			return "{";
		case CBRR:
			return "}";
		case COMMA:
			return ",";
		case SEMIC:
			return ";";
		default:
			return "";
		}
	}
}

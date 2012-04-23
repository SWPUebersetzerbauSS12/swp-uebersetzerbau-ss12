package parser;

import java.util.Stack;
import java.util.Vector;

import ast.ISyntaxTree;
import ast.NonTerminal;

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
		syntaxTree = new NonTerminal("program");
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
				if (token.getAttribute() != null) {
					if (peek.equals(token.getAttribute())
							|| peek.equals(token.getType().toString()
									.toLowerCase())
							|| peek.equals(getStringFromType(token.getType()))
							|| peek.equals("$") && token == null) /**
					 * null is
					 * returned if input ended
					 */
					{
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
				}
			} else /** stack symbol is non-terminal */
			{
				String prod;
				if ((prod = table.getEntry(peek, token.getAttribute())) != null) {
					// my heart skips skips..
				} else if ((prod = table.getEntry(peek, token.getType()
						.toString().toLowerCase())) != null) {
					// skips skips..
				} else if ((prod = table.getEntry(peek,
						getStringFromType(token.getType()))) != null) {
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
					throw new ParserException(
							" Syntax error: No Rule in parsing table ");
				}
			}
		} while (!stack.peek().equals("$"));

		createSyntaxTree();
	}

	private void createSyntaxTree() {
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
		case OP_ASSIGN:
			return "=";
		case LPAREN:
			return "(";
		case RPAREN:
			return ")";
		case LBRACKET:
			return "[";
		case RBRACKET:
			return "]";
		case LBRACE:
			return "{";
		case RBRACE:
			return "}";
		case OP_COMMA:
			return ",";
		case OP_SEMIC:
			return ";";
		default:
			return "";
		}
	}
}

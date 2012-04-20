package parser;

import java.util.Stack;

import lexer.Lexer;
import lexer.SyntaxErrorException;
import lexer.Token;

public class Parser {
	private Lexer lexer;
	private ParseTable table;
	private Stack<String> stack;

	private static final String[] nonTerminals = { "program", "funcs", "func",
			"optparams", "params", "block", "decls", "decl", "type", "stmts",
			"assign", "bool", "join", "equality", "rel", "expr", "term",
			"unary", "factor", "funccall", "args", "stmt", "loc" };
	private static final String[] terminals = { "def", "id", "(", ")", ";",
			",", "{", "}", "[", "]", "basic", "record", "if", "else", "while",
			"do", "break", "return", "print", ".", "=", "||", "&&", "==", "!=",
			"<", "<=", ">=", ">", "+", "-", "*", "/", "!", "num", "real",
			"true", "false", "string" };

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

	public Parser(Lexer lexer) {
		this.lexer = lexer;

		table = new ParseTable(nonTerminals, terminals);
		fillParseTable();

		stack = new Stack<String>();
	}

	public void parse() {
		initStack();
		String X;
		Token a;
		try {
			a = lexer.getNextToken();
		} catch (SyntaxErrorException e) {
			e.printStackTrace();
		}

		do {
			X = stack.peek();
			if (isTerminal(X) || X.equals("$")) {
				if (X.equals(a.getAttribute())) {
					stack.pop();
					a = lexer.getNextToken();
				} else {
					throw new SyntaxErrorException("Wrong terminal "
							+ a.getAttribute() + " in input");
				}
			} else {
				String prod = table.getEntry(X, a.getAttribute());
				if(prod != null){
					stack.pop();
				}
			}
		} while (!stack.peek().equals("$"));
	}
}

package parser;

import lexer.Lexer;
import lexer.SyntaxErrorException;
import lexer.Token;

public class Parser {
	private Lexer lexer;
	private ParseTable table;
	
	private static final String[] nonTerminals = { "program", "funcs", "func", "optparams",
			"params", "block", "decls", "decl", "type", "stmts", "assign",
			"bool", "join", "equality", "rel", "expr", "term", "unary",
			"factor", "funccall", "args", "stmt", "loc" };
	private static final String[] terminals = { "def", "id", "(", ")", ";", ",", "{", "}", "[",
			"]", "basic", "record", "if", "else", "while", "do", "break",
			"return", "print", ".", "=", "||", "&&", "==", "!=", "<", "<=",
			">=", ">", "+", "-", "*", "/", "!", "num", "real", "true",
			"false", "string" };

	public Parser(Lexer lexer) {
		this.lexer = lexer;
		table = new ParseTable(nonTerminals, terminals);
	}

	public void parse() {
		Token tok;
		try {
			while ((tok = lexer.getNextToken()) != null) {

			}
		} catch (SyntaxErrorException e) {
			e.printStackTrace();
		}
	}
}

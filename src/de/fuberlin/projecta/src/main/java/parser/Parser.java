package parser;

import lexer.Lexer;
import lexer.Token;

public class Parser {
	private Lexer lexer;
	
	public Parser(Lexer lexer){
		this.lexer = lexer;
	}
	
	public void parse(Lexer lexer){
		Token tok;
		while((tok = lexer.nextToken()) != null){
			
		}
	}
}

package tokenmatcher;


public class Token {
	
	public TokenType type;
	public String lexem;

	
	public Token( TokenType type, String lexem) {
		super();
		this.type = type;
		this.lexem = lexem;
	}
	
}

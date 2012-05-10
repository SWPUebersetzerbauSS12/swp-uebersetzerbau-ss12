package parser.nodes;

import lexer.IToken.TokenType;
import lombok.Getter;
import semantic.analysis.SymbolTableStack;

public class type extends Tree {

	@Getter
	private TokenType type;

	public type(String name) {
		super(name);
	}

	@Override
	public void run(SymbolTableStack tables) {
		Object value = getAttribute(DefaultAttribute.TokenValue.name());
		this.type = (TokenType) value;
	}
}

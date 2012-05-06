package parser.nodes;

import lexer.IToken.TokenType;
import semantic.analysis.SymbolTableStack;

public class ConstantTree extends expr {
	
	TokenType type;
	
	public ConstantTree(TokenType type) {
		super("constant");
		
		this.type = type;
	}

	@Override
	public void run(SymbolTableStack tables) {
	}

}

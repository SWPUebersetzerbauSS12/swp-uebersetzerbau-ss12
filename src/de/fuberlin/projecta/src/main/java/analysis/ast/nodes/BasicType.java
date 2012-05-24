package analysis.ast.nodes;

import analysis.SymbolTableStack;
import lexer.TokenType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BasicType extends Type {
		
	TokenType type;
	
	public void run(SymbolTableStack tables) {

	}
}

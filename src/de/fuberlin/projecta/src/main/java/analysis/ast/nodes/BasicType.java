package analysis.ast.nodes;

import lexer.TokenType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import analysis.SymbolTableStack;

@AllArgsConstructor
public class BasicType extends Type {
		
	@Getter
	TokenType type;
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}
}

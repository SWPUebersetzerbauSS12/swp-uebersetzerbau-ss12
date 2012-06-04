package analysis.ast.nodes;

import lexer.BasicTokenType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import analysis.SymbolTableStack;

@AllArgsConstructor
public class BasicType extends Type {
		
	@Getter
	BasicTokenType type;
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}
}

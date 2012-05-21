package semantic.analysis.AstNodes;

import lexer.IToken.TokenType;
import lombok.AllArgsConstructor;
import parser.Tree;
import semantic.analysis.SymbolTableStack;

@AllArgsConstructor
public class BasicType extends Tree {
		
	TokenType type;
	
	public void run(SymbolTableStack tables) {

	}
}

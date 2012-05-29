package analysis.ast.nodes;

import lexer.TokenType;
import lombok.Getter;
import parser.Tree;
import analysis.SymbolTableStack;


public class UnaryOp extends Tree {
	
	@Getter
	TokenType op;
	
	public UnaryOp(TokenType op){
		this.op = op;
	}
	
	public void run(SymbolTableStack tables) {

	}
}

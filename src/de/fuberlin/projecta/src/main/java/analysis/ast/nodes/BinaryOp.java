package analysis.ast.nodes;

import lexer.TokenType;
import lombok.Getter;
import parser.Tree;
import analysis.SymbolTableStack;


public class BinaryOp extends Tree {
	
	@Getter
	TokenType op;
	
	public BinaryOp(TokenType op){
		this.op = op;
	}
	
	public void run(SymbolTableStack tables) {

	}
}

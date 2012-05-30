package analysis.ast.nodes;

import lexer.TokenType;
import lombok.Getter;
import analysis.SymbolTableStack;


public class UnaryOp extends AbstractSyntaxTree {
	
	@Getter
	TokenType op;
	
	public UnaryOp(TokenType op){
		this.op = op;
	}
	
	public void run(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		// TODO Auto-generated method stub
		return false;
	}
}

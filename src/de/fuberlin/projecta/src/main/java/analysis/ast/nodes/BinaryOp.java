package analysis.ast.nodes;

import lexer.TokenType;
import lombok.Getter;
import analysis.SemanticException;
import analysis.SymbolTableStack;


public class BinaryOp extends AbstractSyntaxTree {
	
	@Getter
	TokenType op;
	
	public BinaryOp(TokenType op){
		this.op = op;
	}
	
	public void run(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		switch (this.getOp()) {
		case OP_ASSIGN:
			if(this.getChild(0) instanceof Id)
				return true;
			break;
		case OP_DIV:
			if (this.getChild(1) instanceof IntLiteral){
				int v = ((IntLiteral) this.getChild(1)).getValue();
				if (v == 0)
					throw new SemanticException("Division by Zero!");
				else return true;
			}
			if (this.getChild(1) instanceof RealLiteral){
				double v = ((RealLiteral) this.getChild(1)).getValue();
				if (v == 0.0)
					throw new SemanticException("Division by Zero!");
				else return true;
			}
		}

		return false;
	}
}

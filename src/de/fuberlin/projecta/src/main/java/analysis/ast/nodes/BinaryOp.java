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
	
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		switch (this.getOp()) {
		// TODO: think if you can find other cases, where semantics can be wrong/ambiguous
		case OP_ASSIGN:
			if(!(this.getChild(0) instanceof Id)){
				return false;
			}
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
		for(int i = 0; i < this.getChildrenCount(); i++){
			if(!((AbstractSyntaxTree)this.getChild(i)).checkSemantics()){
				return false;
			}
		}
		return true;
	}

	@Override
	public String genCode() {
		// TODO Auto-generated method stub
		return null;
	}
}

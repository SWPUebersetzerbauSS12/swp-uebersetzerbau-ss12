package analysis.ast.nodes;

import java.util.List;

import parser.ISyntaxTree;

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
		//TODO: check for type safety
		switch (this.getOp()) {
		case OP_ASSIGN:
			if(this.getChild(0) instanceof Id){
				List<ISyntaxTree> children = this.getChildren();
				for(ISyntaxTree child : children){
					if(!((AbstractSyntaxTree)child).checkSemantics()){
						return false;
					}
				}
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

		return true;
	}
}

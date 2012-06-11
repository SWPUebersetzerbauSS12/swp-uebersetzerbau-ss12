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
	
	public void buildSymbolTable(SymbolTableStack tables) {
		//Is this correct? first child is type, second id?
		tables.top().insertEntry((Id) getChild(1), (Type) getChild(0));
	}

	@Override
	public boolean checkSemantics() {
		return true;
	}
}

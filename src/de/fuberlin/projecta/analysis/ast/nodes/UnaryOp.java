package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableStack;

public class UnaryOp extends Type {
	
	TokenType op;
	
	public UnaryOp(TokenType op){
		this.op = op;
	}
	
	public void buildSymbolTable(SymbolTableStack tables) {
		tables.top().insertEntry((Id) getChild(1), (Type) getChild(0));
	}

	@Override
	public String genCode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public TokenType getOp() {
		return this.op;
	}

	@Override
	public void checkTypes() {
		String type = ((Type) this.getChild(0)).toTypeString();
		switch (this.op) {
		case OP_NOT:
			if (!(type.equals(TYPE_BOOL_STRING)))
				throw new SemanticException("Invalid operand to NOT: " + type);
		case OP_MINUS:
			if (!type.equals(TYPE_INT_STRING) || !type.equals(TYPE_REAL_STRING))
				throw new SemanticException("Invalid operand to MINUS: " + type);
		}
	}
	
	@Override
	public String toTypeString(){
		return ((Type) this.getChild(0)).toTypeString();
	}
}

package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.SymbolTableStack;


public class UnaryOp extends Type {
	
	TokenType op;
	
	public UnaryOp(TokenType op){
		this.op = op;
	}
	
	public void buildSymbolTable(SymbolTableStack tables) {
		//Is this correct? first child is type, second id?
		//Yep, this should be correct xD
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
	public boolean checkTypes() {
		switch (this.op) {
		case OP_NOT:
			return ((Type) this.getChild(0)).toTypeString().equals(
					TYPE_BOOL_STRING);
		case OP_MINUS:
			return ((Type) this.getChild(0)).toTypeString().equals(
					TYPE_INT_STRING)
					|| ((Type) this.getChild(0)).toTypeString().equals(
							TYPE_REAL_STRING);
		}
		return false;
	}
	
	@Override
	public String toTypeString(){
		return ((Type) this.getChild(0)).toTypeString();
	}
}

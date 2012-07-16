package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.SemanticException;

public class UnaryOp extends Expression {
	
	TokenType op;
	
	public UnaryOp(TokenType op){
		this.op = op;
	}

	public Expression getExpression() {
		return (Expression)this.getChild(0);
	}

	public TokenType getOp() {
		return this.op;
	}

	@Override
	public void checkTypes() {
		String type = getExpression().toTypeString();
		switch (this.op) {
		case OP_NOT:
			if (!(type.equals(Type.TYPE_BOOL_STRING)))
				throw new SemanticException("Invalid operand to NOT: " + type);
		case OP_MINUS:
			if (!type.equals(Type.TYPE_INT_STRING) || !type.equals(Type.TYPE_REAL_STRING))
				throw new SemanticException("Invalid operand to MINUS: " + type);
		}
	}
	
	@Override
	public String toTypeString(){
		return getExpression().toTypeString();
	}
}

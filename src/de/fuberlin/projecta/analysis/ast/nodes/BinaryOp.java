package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.projecta.lexer.TokenType;

public class BinaryOp extends AbstractSyntaxTree {

package analysis.ast.nodes;

import lexer.BasicTokenType;
import lexer.TokenType;
import analysis.EntryType;
import analysis.SemanticException;
import analysis.SymbolTable;
import analysis.SymbolTableStack;

public class BinaryOp extends AbstractSyntaxTree {

	TokenType op;

	public BinaryOp(TokenType op) {
		this.op = op;
	}

	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		switch (this.getOp()) {
		// TODO: think if you can find other cases, where semantics can be
		// wrong/ambiguous
		case OP_ASSIGN:
			if (!(this.getChild(0) instanceof Id)) {
				return false;
			}
			break;
		case OP_DIV:
			if (this.getChild(1) instanceof IntLiteral) {
				int v = ((IntLiteral) this.getChild(1)).getValue();
				if (v == 0)
					throw new SemanticException("Division by Zero!");
				else
					return true;
			}
			if (this.getChild(1) instanceof RealLiteral) {
				double v = ((RealLiteral) this.getChild(1)).getValue();
				if (v == 0.0)
					throw new SemanticException("Division by Zero!");
				else
					return true;
			}
		}
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (!((AbstractSyntaxTree) this.getChild(i)).checkSemantics()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String genCode() {
		String ret = "";
		Id a = null,b = null;
		if(getChild(0) instanceof Id && getChild(1) instanceof Id){
			a = ((Id) getChild(0));
			b = ((Id) getChild(1));
		}
		
		switch (op) {
		case OP_LT:
			ret = "fcmp olt " + checkType(a, b) + " %" + a.getRegister() + ", %"
					+ b.getRegister();
			break;
		case OP_LE:
			ret = "fcmp ole " + checkType(a, b) + " %" + a.getRegister() + ", %"
					+ b.getRegister();
			break;
		}
		return ret;
	}

	/**
	 * Searches the symbolTables up to the point where both id's are found and
	 * gives the highest type possible. E.g. for int and real it is double, for
	 * int and int it is i32, for int and string it is i8*.
	 * 
	 * @param a
	 *            the first id
	 * @param b
	 *            the second id
	 * @return the highest possible basicTokenType of both id's
	 */
	private String checkType(Id a, Id b) {
		String ret = "";
		SymbolTable t = table;
		EntryType eA = null, eB = null;
		if (t == null)
			t = getHigherTable();
		while (t != null) {
			eA = t.lookup(a.getValue());
			eB = t.lookup(b.getValue());
			if (eA != null && eB != null)
				break;
			t = getHigherTable();
		}

		if (eA != null && eB != null) {
			Type tA = eA.getType();
			Type tB = eB.getType();
			if(tA.equals(tB)){
				ret = tA.genCode();
			}
		} else {
			throw new SemanticException("Error! Id's:" + a.getValue() + ", "
					+ b.getValue() + " not found in symbolTables!");
		}

		return ret;
	}

	public TokenType getOp() {
		return op;
	}
}

package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.projecta.analysis.BasicTokenType;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableStack;

public class Declaration extends AbstractSyntaxTree {

	@Override
	public void buildSymbolTable(SymbolTableStack tables) {
		tables.top().insertEntry((Id) getChild(1), (Type) getChild(0));
	}

	@Override
	public void checkSemantics() {
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (this.getChild(i) instanceof BasicType
					&& ((BasicType) this.getChild(i)).getTokenType() == BasicTokenType.VOID) {
				throw new SemanticException("Variable cannot be from type void");
			}
			((AbstractSyntaxTree) this.getChild(i)).checkSemantics();
		}
	}

	@Override
	public String genCode() {
		String ret = "";
		ret += "%" + ((Id) getChild(1)).genCode() + " = alloca "
				+ ((Type) getChild(0)).genCode();
		return ret;
	}

	public Type getType() {
		return (Type)getChild(0);
	}

	public Id getId() {
		return (Id)getChild(1);
	}
}

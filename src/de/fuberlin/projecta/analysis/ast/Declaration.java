package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.projecta.analysis.TypeErrorException;

/**
 * Declaration consists of two child nodes
 * 
 * First child: Type node
 * Second child: Id node
 */
public class Declaration extends AbstractSyntaxTree {

	@Override
	public void buildSymbolTable(SymbolTableStack tables) {
		tables.top().insertEntry((Id) getChild(1), (Type) getChild(0));
	}

	@Override
	public void checkTypes() {
		if (getType().toTypeString().equals(BasicType.TYPE_VOID_STRING)) {
			throw new TypeErrorException("Variable cannot be from type void");
		}

		for (int i = 0; i < getChildrenCount(); ++i)
			((AbstractSyntaxTree)getChild(i)).checkTypes();
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

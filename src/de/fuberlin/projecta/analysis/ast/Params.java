package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.projecta.analysis.BasicTokenType;
import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableStack;

public class Params extends AbstractSyntaxTree {

	@Override
	public void buildSymbolTable(SymbolTableStack tables) {
		for (int i = 0; i < getChildrenCount(); i += 2) {
			Type type = (Type) getChild(i);
			Id id = (Id) getChild(i + 1);
			EntryType entry = new EntryType(id, type);
			tables.top().insertEntry(entry);
			if(type instanceof Record){
				type.buildSymbolTable(tables);
			}
		}
	}

	@Override
	public void checkSemantics() {
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (this.getChild(i) instanceof BasicType
					&& ((BasicType) this.getChild(i)).getTokenType() == BasicTokenType.VOID) {
				throw new SemanticException("Parameter must have a type. \"void\" is not allowed!", this);
			}
			((AbstractSyntaxTree) this.getChild(i)).checkSemantics();
		}
	}

	@Override
	public String genCode() {
		String ret = "";
		boolean atLeastOne = false;
		for (int i = 0; i < getChildrenCount(); i += 2) {
			atLeastOne = true;
			ret += ((Type) getChild(i)).genCode();
			// hack for arrays...
			if(getChild(i) instanceof Array)
				ret+="* byval";
			ret +=  " %" + ((Id) getChild(i + 1)).genCode() + ", ";
		}
		if (atLeastOne) {
			// strip trailing comma
			ret = ret.substring(0, ret.length() - 2);
		}
		return ret;
	}
}

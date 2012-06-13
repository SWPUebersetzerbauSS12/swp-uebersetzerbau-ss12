package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.projecta.lexer.BasicTokenType;

public class Declaration extends AbstractSyntaxTree {
	@Override
	public void buildSymbolTable(SymbolTableStack tables) {
		tables.top().insertEntry((Id) getChild(1), (Type) getChild(0));
	}

	@Override
	public boolean checkSemantics() {
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (this.getChild(i) instanceof BasicType
					&& ((BasicType) this.getChild(i)).getType() == BasicTokenType.VOID) {
				return false;
			}
			if (!((AbstractSyntaxTree) this.getChild(i)).checkSemantics()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String genCode() {
		String ret = "";
		ret += "%" + ((Id) getChild(1)).genCode() + " = alloca "
				+ ((Type) getChild(0)).genCode();
		return ret;
	}
}

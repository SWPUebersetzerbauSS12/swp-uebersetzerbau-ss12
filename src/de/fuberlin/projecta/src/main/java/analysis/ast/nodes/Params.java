package analysis.ast.nodes;

import lexer.BasicTokenType;
import analysis.EntryType;
import analysis.SymbolTableStack;

public class Params extends AbstractSyntaxTree {
	@Override
	public void buildSymbolTable(SymbolTableStack tables) {
		for (int i = 0; i < getChildrenCount(); i += 2) {
			Type type = (Type) getChild(i);
			Id id = (Id) getChild(i + 1);
			EntryType entry = new EntryType(id, type);
			tables.top().insertEntry(entry);
		}
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
		boolean atLeastOne = false;
		for (int i = 0; i < getChildrenCount(); i += 2) {
			atLeastOne = true;
			ret += ((Type) getChild(i)).genCode() + " %"
					+ ((Id) getChild(i + 1)).genCode() + ", ";
		}
		if (atLeastOne) {
			// strip trailing comma
			ret = ret.substring(0, ret.length() - 2);
		}
		return ret;
	}
}

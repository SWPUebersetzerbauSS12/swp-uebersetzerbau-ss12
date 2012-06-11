package analysis.ast.nodes;

import analysis.EntryType;
import analysis.SymbolTable;
import analysis.SymbolTableStack;

public class FuncDef extends AbstractSyntaxTree {

	@Override
	public boolean checkSemantics() {
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (!((AbstractSyntaxTree) this.getChild(i)).checkSemantics()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void buildSymbolTable(SymbolTableStack stack) {
		Type type = (Type) getChild(0);
		Id id = (Id) getChild(1);

		stack.push();
		// these are parameters
		((AbstractSyntaxTree) getChild(2)).buildSymbolTable(stack);

		SymbolTable tmp = stack.pop();
		EntryType entry = new EntryType(id, type, tmp.getEntries());
		stack.top().insertEntry(entry);

		// TODO: musn't the parameters be also stored in the block
		// symbolTable???
		if (this.getChildrenCount() == 4)
			((AbstractSyntaxTree) getChild(3)).buildSymbolTable(stack); // this
																		// is
																		// the
																		// block,
																		// it
																		// can
		// handle everything itself

	}

	@Override
	public String genCode() {
		String ret = "define " + ((Type) getChild(0)).genCode() + " @"
				+ ((Id) getChild(1)).genCode() + "("
				+ ((Params) getChild(2)).genCode() + ") nounwind uwtable { "
				+ ((Block) getChild(3)).genCode() + " }";
		return ret;
	}
}

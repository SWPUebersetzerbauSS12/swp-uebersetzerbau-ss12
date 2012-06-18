package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.analysis.SymbolTableStack;

public class Return extends Statement {
	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		String ret = "";
		if (getChildrenCount() == 0)
			return "ret void";

		if ((AbstractSyntaxTree) getChild(0) instanceof Id) {
			EntryType eA = null;
			SymbolTableHelper helper = new SymbolTableHelper();
			eA = helper.lookup(((Id) getChild(0)).getValue(), this);
			Block block = getHighestBlock();
			int reg = block.getNewRegister();
			ret = "%" + reg + " = load " + eA.getType().genCode() + "* %"
					+ ((AbstractSyntaxTree) getChild(0)).genCode() + "\n";
			ret += "ret " + eA.getType().genCode() + " %" + reg;
		}
		return ret;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}

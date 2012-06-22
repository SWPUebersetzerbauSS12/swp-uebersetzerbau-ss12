package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SymbolTableHelper;

public class Return extends Statement {

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		Block block = getHighestBlock();
		String ret = "";
		if (getChildrenCount() == 0)
			return "ret void";

		if ((AbstractSyntaxTree) getChild(0) instanceof Id) {
			EntryType eA = null;
			SymbolTableHelper helper = new SymbolTableHelper();
			eA = helper.lookup(((Id) getChild(0)).getValue(), this);
			int reg = block.getNewRegister();
			ret = "%" + reg + " = load " + eA.getType().genCode() + "* %"
					+ ((AbstractSyntaxTree) getChild(0)).genCode() + "\n";
			ret += "ret " + eA.getType().genCode() + " %" + reg;
		} else {
			ret += "ret " + ((AbstractSyntaxTree) getChild(0)).genCode();
		}
		if(ret != ""){
			ret += "\n; <label>:" + block.getNewRegister();
		}
		return ret;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}

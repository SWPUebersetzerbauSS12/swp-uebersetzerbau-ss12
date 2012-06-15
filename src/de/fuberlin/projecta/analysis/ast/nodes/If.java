package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;

public class If extends Statement {
	private Block block;

	public void buildSymbolTable(SymbolTableStack tables) {

	}

	@Override
	public boolean checkSemantics() {
		// semantics of if-statement is unambiguous
		// only need to check children
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
		block = getHighestBlock();
		if (block != null) {
			int[] regs = new int[3];
			for (int i = 0; i < 3; i++) {
				regs[i] = block.getNewRegister();
			}
			ret = "%" +  regs[0] + " = "
					+ ((AbstractSyntaxTree) getChild(0)).genCode() + "\n";
			ret += "br i1 %" + regs[0] + ", label %" + regs[1] + ", label %"
					+ regs[2] + "\n";
			ret += "; <label>:" + regs[1] + "\n";
			ret += ((Statement) getChild(1)).genCode() + "\n";
			ret += "; <label>:" + regs[2] + "\n";
		}

		return ret;
	}

}

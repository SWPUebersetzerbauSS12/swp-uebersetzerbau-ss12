package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.codegen.LLVM;

public class Return extends Statement {

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		Block block = getHighestBlock();
		String ret = "";
		if (getChildrenCount() == 0) {
			return "ret void";
		} else if (getChild(0) instanceof Literal) {
			ret += "ret " + ((Type) getChild(0)).genCode() + "\n";
		} else if (getChild(0) instanceof Type) {
			Type t = (Type) getChild(0);
			ret += LLVM.loadType(t);
			ret += "ret " + t.fromTypeStringToLLVMType() + " %"
					+ LLVM.getMem(t) + "\n";
		}
		if (!ret.equals("")) {
			ret += "\n; <label>:" + block.getNewVar();
		}
		return ret;
	}

	@Override
	public boolean checkTypes() {
		String funcType = ((Type) getParentFunction().getChild(0))
				.toTypeString();
		if (this.getChildrenCount() == 0) {
			// return type must be void!
			return funcType.equals(Type.TYPE_VOID_STRING);
		} else {
			return funcType.equals(((Type) getChild(0)).toTypeString());
		}
	}

	private FuncDef getParentFunction() {
		ISyntaxTree func = this;
		do {
			func = func.getParent();
		} while (!(func instanceof FuncDef));
		return (FuncDef) func;
	}
}

package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.codegen.LLVM;

public class Return extends Statement {

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
	public void checkTypes() {
		String funcType = ((Type) getParentFunction().getChild(0))
				.toTypeString();

		if (this.getChildrenCount() == 0) {
			// return type must be void!
			if (!funcType.equals(Type.TYPE_VOID_STRING))
				throw new SemanticException("Missing return value in non-void function");
		} else {
			String returnType = ((Type) getChild(0)).toTypeString();
			if (!funcType.equals(returnType))
				throw new SemanticException("Incompatible arguments: Function declared with return type " + funcType + " but returned " + returnType);
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

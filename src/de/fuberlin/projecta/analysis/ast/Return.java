package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.codegen.LLVM;

public class Return extends Statement {

	public Expression getArgument() {
		if (getChildrenCount() == 0)
			return null;
		return (Expression)getChild(0);
	}

	@Override
	public String genCode() {
		Block block = getHighestBlock();
		String ret = "";
		Expression argument = getArgument();
		if (argument == null) {
			return "ret void";
		} else if (argument instanceof Literal) {
			ret += "ret " + argument.genCode() + "\n";
		} else {
			ret += LLVM.loadType(argument);
			ret += "ret " + argument.fromTypeStringToLLVMType() + " %"
					+ LLVM.getMem(argument) + "\n";
		}
		if (!ret.equals("")) {
			ret += "\n; <label>:" + block.getNewVar();
		}
		return ret;
	}

	@Override
	public void checkTypes() {
		String funcType = getParentFunction().toTypeString();
		if (this.getChildrenCount() == 0) {
			// return type must be void!
			if (!funcType.equals(BasicType.TYPE_VOID_STRING))
				throw new SemanticException("Missing return value in non-void function", null);
		} else {
			String returnType = getArgument().toTypeString();
			if (!funcType.equals(returnType))
				throw new SemanticException("Incompatible arguments: Function declared with return type " + funcType + " but returned " + returnType, null);
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

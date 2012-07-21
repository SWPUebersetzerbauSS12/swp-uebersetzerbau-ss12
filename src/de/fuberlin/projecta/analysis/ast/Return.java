package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.codegen.LLVM;

public class Return extends Statement {

	public Expression getArgument() {
		if (getChildrenCount() == 0)
			return null;
		return (Expression) getChild(0);
	}

	@Override
	public String genCode() {
		String ret = "";

		Expression argument = getArgument();

		if (argument == null) {
			getHighestBlock().getNewVar();
			return "br label %return\n";
			
		} else {
			ret += LLVM.loadType(argument);
			ret += "store " + getParentFunction().fromTypeStringToLLVMType()
					+ " %" + LLVM.getMem(argument) + ", "
					+ getParentFunction().fromTypeStringToLLVMType() + "* %1\n"
					+ "br label %return\n";
			getHighestBlock().getNewVar();
		}
		return ret;
	}

	@Override
	public void checkTypes() {
		String funcType = getParentFunction().toTypeString();
		if (this.getChildrenCount() == 0) {
			// return type must be void!
			if (!funcType.equals(BasicType.TYPE_VOID_STRING))
				throw new SemanticException("Missing return value in non-void function", this);
		} else {
			String returnType = getArgument().toTypeString();
			if (!funcType.equals(returnType))
				throw new SemanticException(
						"Incompatible arguments: Function declared with return type "
								+ funcType + " but returned " + returnType, this);
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

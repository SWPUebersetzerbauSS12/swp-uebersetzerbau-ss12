package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.codegen.LLVM;

/**
 * This class represents one function call.
 * 
 * It has one or two children. The first is id, which represents the functions name,
 * the second is a node of type Args (if existing) and contains all arguments. 
 */
public class FuncCall extends Expression {

	public Id getId() {
		return (Id)getChild(0);
	}

	/**
	 * For this to work properly all parameters MUST be loaded before!
	 */
	@Override
	public String genCode() {
		String ret = "";
		EntryType func = null;
		if (getChildrenCount() > 1) {

			// List<EntryType> parameters = new ArrayList<EntryType>();
			// I simply can't reach the type node of the parameters !!! So
			// there's no equal entryType !!!

			// TODO: this is currently not working if multiple instances with
			// this id exist in the symbolTable!!!
			func = SymbolTableHelper
					.lookup(getId().getValue(), this);
		} else {
			func = SymbolTableHelper
					.lookup(getId().getValue(), this);
		}
		if (func != null) {
			ret = "call " + func.getType().genCode();
			if (!func.getParams().isEmpty()) {
				int counter = 0;
				ret += " (";
				for (ISyntaxTree child : getChild(1).getChildren()) {
					if (!(child instanceof Literal)) {

						Type node = null;

						if (child instanceof Id) {
							counter++;
							ret += ((Id) child).getType().genCode() + ", ";
						} else if (child instanceof BasicType) {
							counter++;
							node = (Type) child;
							ret += node.genCode() + ", ";
						}
					}
				}
				if (counter > 0) {
					ret = ret.substring(0, ret.length() - 2);
					ret += ")*";
				} else {
					ret = ret.substring(0, ret.length() - 1);
				}
			}

			ret += " @" + func.getId() + "(";
			boolean tmp = false;
			if (getChildrenCount() > 1)
				for (ISyntaxTree child : getChild(1).getChildren()) {
					tmp = true;
					Expression node = (Expression) child;
					ret += node.fromTypeStringToLLVMType() + " %"
							+ LLVM.getMem(node) + ", ";
				}
			if (tmp)
				ret = ret.substring(0, ret.length() - 2);
			ret += ")";

			// implicit var incrementation
			if (!searchUpAssign()
					&& func.getType().toTypeString().equals("void")) {
				getHighestBlock().getNewVar();
			}
		}
		return ret;
	}

	public boolean searchUpAssign() {
		BinaryOp bOp = null;
		if (getParent() != null) {
			ISyntaxTree parent = getParent();
			while (parent != null) {
				if (parent instanceof BinaryOp) {
					bOp = (BinaryOp) parent;
					if (bOp.getOp() == TokenType.OP_ASSIGN)
						return true;
				}
				parent = parent.getParent();
			}
		}
		return false;
	}

	@Override
	public String toTypeString() {
		return SymbolTableHelper.lookup(getId().getValue(), this)
				.getType().toTypeString();
	}

}

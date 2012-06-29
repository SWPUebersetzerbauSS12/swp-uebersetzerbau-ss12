package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SymbolTableHelper;

/**
 * This class represents one function call. It has one or two children. The
 * first is id, which represents the functions name, the second is a node of
 * type Args (if existing) and contains all arguments.
 * 
 * @author micha
 * 
 */
public class FuncCall extends Type {

	@Override
	public boolean checkSemantics() {
		return true;
	}

	@Override
	public String genCode() {
		String ret = "";
		EntryType func = null;
		if (getChildrenCount() > 1) {

		} else {
			func = SymbolTableHelper
					.lookup(((Id) getChild(0)).getValue(), this);
		}
		if (func != null) {
			ret = "call " + func.getType().genCode();
//			ret += " (";
//			for (EntryType param : func.getParams()) {
//				tmp = true;
//				ret += param.getType().genCode() + "*, ";
//			}
//			if (tmp)
//				ret = ret.substring(0, ret.length() - 2);
//			ret += ")*" 
			ret += " @" + func.getId() + "(";
			boolean tmp = false;
			for (EntryType param : func.getParams()) {
				tmp = true;
				ret += param.getType().genCode() + "* %" + param.getId() + ", ";
			}
			if (tmp)
				ret = ret.substring(0, ret.length() - 2);
			ret += ")";

			// implicit var incrementation
			if (!searchUpAssign()
					&& !func.getType().toTypeString().equals("void")) {
				getHighestBlock().getNewMemory();
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
                return SymbolTableHelper.lookup(((Id) getChild(0)).getValue(), this)
                                .getType().toTypeString();
        }

}

package de.fuberlin.projecta.analysis.ast.nodes;

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
public class FuncCall extends AbstractSyntaxTree {

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
		}
		return ret;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}

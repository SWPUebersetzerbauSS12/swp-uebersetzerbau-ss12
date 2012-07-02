package de.fuberlin.projecta.codegen;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.analysis.ast.nodes.AbstractSyntaxTree;
import de.fuberlin.projecta.analysis.ast.nodes.Args;
import de.fuberlin.projecta.analysis.ast.nodes.Block;
import de.fuberlin.projecta.analysis.ast.nodes.FuncDef;
import de.fuberlin.projecta.analysis.ast.nodes.Id;
import de.fuberlin.projecta.analysis.ast.nodes.Statement;
import de.fuberlin.projecta.analysis.ast.nodes.Type;

public class LLVM {

	/**
	 * This class should hold all the methods to generate LLVM code
	 */

	public static String genBranch(Statement current,
			AbstractSyntaxTree block1, AbstractSyntaxTree block2, boolean not,
			boolean loop) {
		String ret = "";
		Block block = current.getHighestBlock();
		if (block != null) {
			String s1 = "", s2 = "";
			int varDecision, labelTrue, labelFalse, labelBehind;

			varDecision = block.getCurrentRegister();
			labelTrue = block.getNewVar();
			if (block1 != null)
				s1 = block1.genCode();
			labelFalse = block.getNewVar();
			if (block2 != null)
				s2 = block2.genCode();
			labelBehind = block.getNewVar();
			current.setEndLabel(labelBehind);

			if (!not) {
				ret += "br i1 %" + varDecision + ", label %" + labelTrue
						+ ", label %" + labelFalse + "\n\n";
			} else {
				ret += "br i1 %" + varDecision + ", label %" + labelFalse
						+ ", label %" + labelTrue + "\n\n";
			}

			ret += "; <label> %" + labelTrue + "\n";
			ret += s1 + "\n";
			if (!loop)
				ret += "br label %" + labelBehind + "\n\n";
			else
				ret += "br label %" + current.getBeginLabel() + "\n\n";
			ret += "; <label> %" + labelFalse + "\n";
			ret += s2 + "\n";
			ret += "br label %" + labelBehind + "\n\n";
			ret += "; <label> %" + labelBehind + "\n";
		}
		return ret;
	}

	public static String loadVar(Id id) {
		String ret = "";
		if (id != null && !isInParams(id) && id.getVar() == 0) {
			int memory = id.getHighestBlock().getNewVar();
			id.setValMemory(memory);
			ret += "%"
					+ memory
					+ " = load "
					+ SymbolTableHelper.lookup(id.getValue(), id).getType()
							.genCode() + "* %" + id.getValue() + "\n";
		}
		return ret;
	}

	public static String loadParams(Args args) {
		String ret = "";
		if (args != null) {
			for (ISyntaxTree child : args.getChildren()) {
				if (child instanceof Id)
					if (!isInParams((Id) child))
						ret += loadVar((Id) child);
			}
		}
		return ret;
	}

	public static boolean isInParams(Id id) {
		FuncDef fDef = searchUpFuncDef(id);

		if (fDef != null) {
			if (fDef.getChild(2).getChildrenCount() > 0) {
				for (int i = 0; i < fDef.getChild(2).getChildrenCount(); i += 2) {
					Type typeO = (Type) fDef.getChild(2).getChild(i);
					Id idO = (Id) fDef.getChild(2).getChild(i + 1);
					if (idO.getValue().equals(id.getValue())
							&& typeO.equals(id.getType()))
						return true;
				}
			}
		}

		return false;
	}

	private static FuncDef searchUpFuncDef(AbstractSyntaxTree node) {
		if (node.getParent() != null) {
			ISyntaxTree parent = node.getParent();
			while (parent != null) {
				if (parent instanceof FuncDef) {
					return (FuncDef) parent;
				}
				parent = parent.getParent();
			}
		}
		return null;
	}
	
	public static String store(){
		String out = "";
		
		return out;
	}
	
	public static String getMem(Id id) {
		String ret = "";
		if(LLVM.isInParams(id)){
			ret = id.getValue();
		} else {
			ret = "" + id.getVar();
		}
		return ret;
	}
}

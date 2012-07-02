package de.fuberlin.projecta.codegen;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.analysis.ast.nodes.AbstractSyntaxTree;
import de.fuberlin.projecta.analysis.ast.nodes.Args;
import de.fuberlin.projecta.analysis.ast.nodes.Block;
import de.fuberlin.projecta.analysis.ast.nodes.Id;
import de.fuberlin.projecta.analysis.ast.nodes.Statement;

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
			labelTrue = block.getNewMemory();
			if (block1 != null)
				s1 = block1.genCode();
			labelFalse = block.getNewMemory();
			if (block2 != null)
				s2 = block2.genCode();
			labelBehind = block.getNewMemory();
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
		if (id.getValMemory() == 0) {
			int memory = id.getHighestBlock().getNewMemory();
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
		for (ISyntaxTree child : args.getChildren()) {
			if (child instanceof Id)
				ret += loadVar((Id) child);
		}
		return ret;
	}
}

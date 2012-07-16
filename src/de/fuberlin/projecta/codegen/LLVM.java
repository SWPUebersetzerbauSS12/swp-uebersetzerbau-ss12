package de.fuberlin.projecta.codegen;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.analysis.ast.nodes.AbstractSyntaxTree;
import de.fuberlin.projecta.analysis.ast.nodes.Args;
import de.fuberlin.projecta.analysis.ast.nodes.Block;
import de.fuberlin.projecta.analysis.ast.nodes.Declaration;
import de.fuberlin.projecta.analysis.ast.nodes.Expression;
import de.fuberlin.projecta.analysis.ast.nodes.FuncCall;
import de.fuberlin.projecta.analysis.ast.nodes.FuncDef;
import de.fuberlin.projecta.analysis.ast.nodes.Id;
import de.fuberlin.projecta.analysis.ast.nodes.Literal;
import de.fuberlin.projecta.analysis.ast.nodes.Record;
import de.fuberlin.projecta.analysis.ast.nodes.RecordVarCall;
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
			ret += "; <label>:" + labelTrue + "\n";
			ret += s1 + "\n";
			if (!loop)
				ret += "br label %" + labelBehind + "\n\n";
			else
				ret += "br label %" + current.getBeginLabel() + "\n\n";
			ret += "; <label>:" + labelFalse + "\n";
			ret += s2 + "\n";
			ret += "br label %" + labelBehind + "\n\n";
			ret += "; <label>:" + labelBehind + "\n";
		}
		return ret;
	}

	private static String loadVar(Id id) {
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

	private static String loadParams(Args args) {
		String ret = "";
		if (args != null) {
			for (ISyntaxTree child : args.getChildren()) {
				Expression expr = (Expression) child;
				ret += loadType(expr);
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

	public static String getMem(Expression expr) {
		String ret = "";
		if (expr instanceof Id) {
			Id id = (Id) expr;
			if (LLVM.isInParams(id)) {
				ret = id.getValue();
			} else {
				ret = "" + id.getVar();
			}
		} else {
			ret = "" + expr.getVar();
		}

		return ret;
	}

	public static int findNumberOfRecordVar(Record rec, String recordVar) {
		for (int i = 0; i < rec.getChildrenCount(); i++) {
			Declaration decl = (Declaration) rec.getChild(i);
			if (((Id) decl.getChild(1)).getValue().equals(recordVar)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * The idea is to load the value into a register, even if it is a literal or
	 * a funcCall in order to statically take the valMemory after calling this
	 * function
	 * 
	 * @param expr
	 *            the node to load the value into a new memory
	 * @return the code for loading it
	 */
	public static String loadType(Expression expr) {
		String ret = "";

		// If getVar is 0 it must be loaded, otherwise it is already loaded
		if (expr != null && expr.getVar() == 0) {
			Block block = expr.getHighestBlock();
			if (expr instanceof Literal) {
				int n = block.getNewVar(), m = block.getNewVar();
				String t = expr.genCode().split(" ")[0];
				String v = expr.genCode().split(" ")[1];
				ret += "%" + n + " = alloca " + t + "\n";
				ret += "store " + t + " " + v + ", " + t + "* %" + n + "\n";
				ret += "%" + m + " = load " + t + "* %" + n + "\n";
				expr.setValMemory(m);
			} else if (expr instanceof Id) {
				ret += loadVar((Id) expr);
			} else if (expr instanceof FuncCall) {
				if (expr.getChildrenCount() > 1) {
					ret += LLVM.loadParams((Args) expr.getChild(1));
				}
				int n = block.getNewVar();
				ret += "%" + n + " = " + expr.genCode() + "\n";
				expr.setValMemory(n);
			} else if (expr instanceof RecordVarCall) {
				int n = block.getNewVar();
				RecordVarCall recVarCall = (RecordVarCall) expr;
				Record rec = (Record) SymbolTableHelper.lookup(
						recVarCall.getRecordId().getValue(), expr).getType();
				Id recName = recVarCall.getRecordId();
				ret += "%"
						+ n
						+ " = getelementptr inbounds %struct."
						+ recName.getValue()
						+ "* %" + recName.getValue() + ", i32 0, i32 "
						+ findNumberOfRecordVar(rec, recVarCall
								.getVarId().getValue()) + "\n";
				expr.setValMemory(n);
			} else {
				// TODO: is this already calling setValMemory always?
				ret += expr.genCode();
			}
		}

		return ret;
	}
}

package de.fuberlin.projecta.codegen;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.analysis.TypeErrorException;
import de.fuberlin.projecta.analysis.ast.AbstractSyntaxTree;
import de.fuberlin.projecta.analysis.ast.Args;
import de.fuberlin.projecta.analysis.ast.Array;
import de.fuberlin.projecta.analysis.ast.ArrayCall;
import de.fuberlin.projecta.analysis.ast.Block;
import de.fuberlin.projecta.analysis.ast.Declaration;
import de.fuberlin.projecta.analysis.ast.Expression;
import de.fuberlin.projecta.analysis.ast.FuncCall;
import de.fuberlin.projecta.analysis.ast.FuncDef;
import de.fuberlin.projecta.analysis.ast.Id;
import de.fuberlin.projecta.analysis.ast.IntLiteral;
import de.fuberlin.projecta.analysis.ast.Literal;
import de.fuberlin.projecta.analysis.ast.Record;
import de.fuberlin.projecta.analysis.ast.RecordVarCall;
import de.fuberlin.projecta.analysis.ast.Statement;
import de.fuberlin.projecta.analysis.ast.Type;

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

	/**
	 * Gets number
	 * 
	 * 
	 * @param rec
	 * @param recordVar
	 * @return
	 */
	public static int findNumberOfRecordVar(Record rec, String recordVar) {
		for (int i = 0; i < rec.getChildrenCount(); i++) {
			Declaration decl = (Declaration) rec.getChild(i);
			if (((Id) decl.getChild(1)).getValue().equals(recordVar)) {
				return i;
			}
		}
		throw new TypeErrorException("Entry " + recordVar
				+ " not found in record "
				+ ((Id) rec.getParent().getChild(1)).getValue());
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
			} else if(expr instanceof ArrayCall){
				ArrayCall array = (ArrayCall) expr;
				ret += getArrayCallPointer(array);
				int pointer = array.getVar();
				Id id = array.getVarId();
				Type t = SymbolTableHelper.lookup(id.getValue(), array).getType();
				int val = block.getNewVar();
				ret += "%"+val + " = load "+ ((Array)t).getBasicType().fromTypeStringToLLVMType() + "* %"+pointer;
				expr.setValMemory(val);
				ret += "\n";
				
			} else if (expr instanceof RecordVarCall) {
				int n = -1;
				RecordVarCall recVarCall = (RecordVarCall) expr;
				Record rec = (Record) SymbolTableHelper.lookup(
						recVarCall.getRecordId().getValue(), expr).getType();
				if (recVarCall.getChild(0) instanceof RecordVarCall) {
					String currentMemory = recVarCall.getRecordId().getValue();
					// start by searching child 1 of innermost recordVarCall
					Id currentSearchNode = null;
					// this is used for searching the correct node in the
					// record, starting in the innermost node
					RecordVarCall currentRecVarCall = (RecordVarCall) recVarCall
							.getRecordId().getParent();
					Type retType = null;
					do {
						n = block.getNewVar();
						// move up in RecordVar-Tree
						currentSearchNode = (Id) currentRecVarCall.getChild(1);

						int index = findNumberOfRecordVar(rec,
								currentSearchNode.getValue());
						retType = ((Type) rec.getChild(index).getChild(0));
						ret += "%" + n + " = getelementptr inbounds %struct."
								+ ((Id) rec.getParent().getChild(1)).getValue()
								+ "* %" + currentMemory + ", i32 0, i32 "
								+ index + "\n";
						currentMemory = "" + n;

						if ((currentRecVarCall.getParent() instanceof RecordVarCall)
								&& rec.getChildrenCount() > index
								&& rec.getChild(index).getChildrenCount() > 0) {
							if (rec.getChild(index).getChild(0) instanceof Record)
								rec = (Record) rec.getChild(index).getChild(0);
						} else {

							break;
						}
						currentRecVarCall = (RecordVarCall) currentRecVarCall
								.getParent();
					} while (rec instanceof Record);

					// before returning, load the actual value into new memory
					n = block.getNewVar();
					ret += "%" + n + " = load "
							+ retType.fromTypeStringToLLVMType() + "* %"
							+ (n - 1) + "\n";

					// save the memory in the outermost recordVarCall node
					recVarCall.setValMemory(n);
					return ret;
				} else {
					int index = findNumberOfRecordVar(rec, recVarCall
							.getVarId().getValue());
					n = block.getNewVar();
					recVarCall.setValMemory(n);
					ret += "%" + n + " = getelementptr inbounds %struct."
							+ ((Id) rec.getParent().getChild(1)).getValue()
							+ "* %" + recVarCall.getRecordId().getValue()
							+ ", i32 0, i32 " + index + "\n";
					n = block.getNewVar();
					Type retType = ((Type) rec.getChild(index).getChild(0));
					ret += "%" + n + " = load "
							+ retType.fromTypeStringToLLVMType() + "* %"
							+ (n - 1) + "\n";
					expr.setValMemory(n);
				}
			} else {
				// TODO: is this already calling setValMemory always?
				ret += expr.genCode();
			}
		}

		return ret;
	}
	
	public static String getArrayCallPointer(ArrayCall array){
		String ret = "";
		int num = ((IntLiteral)array.getChild(0)).getValue();
		String index = ", i32 0, i32 " + num;
		ArrayCall tmp = array;
		// collect all array references
		while(tmp.getChild(1) instanceof ArrayCall){
			tmp = (ArrayCall)tmp.getChild(1);
			index += ", i32 " + ((IntLiteral)tmp.getChild(0)).getValue();
		}
		//last ArrayCall contains the id of the array! (weird, but we deal with it)
		Id id = (Id) tmp.getChild(1);
		Type t = SymbolTableHelper.lookup(id.getValue(), array).getType();
		int pointer = array.getHighestBlock().getNewVar();
		ret += "%"+ pointer + " = getelementptr inbounds "+ ((Array)t).genCode() +"* %"+ id.getValue();
		ret += index + "\n";
		array.setValMemory(pointer);
		return ret;
	}

	public static String getRecordVarCallPointer(RecordVarCall expr) {
		String ret = "";
		Block block = expr.getHighestBlock();
		int n = -1;
		RecordVarCall recVarCall = (RecordVarCall) expr;
		Record rec = (Record) SymbolTableHelper.lookup(
				recVarCall.getRecordId().getValue(), expr).getType();
		if (recVarCall.getChild(0) instanceof RecordVarCall) {
			String currentMemory = recVarCall.getRecordId().getValue();
			// start by searching child 1 of innermost recordVarCall
			Id currentSearchNode = null;
			// this is used for searching the correct node in the
			// record, starting in the innermost node
			RecordVarCall currentRecVarCall = (RecordVarCall) recVarCall
					.getRecordId().getParent();

			do {
				n = block.getNewVar();
				// move up in RecordVar-Tree
				currentSearchNode = (Id) currentRecVarCall.getChild(1);

				int index = findNumberOfRecordVar(rec,
						currentSearchNode.getValue());

				ret += "%" + n + " = getelementptr inbounds %struct."
						+ ((Id) rec.getParent().getChild(1)).getValue() + "* %"
						+ currentMemory + ", i32 0, i32 " + index + "\n";
				currentMemory = "" + n;

				if ((currentRecVarCall.getParent() instanceof RecordVarCall)
						&& rec.getChildrenCount() > index
						&& rec.getChild(index).getChildrenCount() > 0) {
					if (rec.getChild(index).getChild(0) instanceof Record)
						rec = (Record) rec.getChild(index).getChild(0);
				} else {
					break;
				}
				currentRecVarCall = (RecordVarCall) currentRecVarCall
						.getParent();
			} while (rec instanceof Record);
			// save the memory in the outermost recordVarCall node
			recVarCall.setValMemory(n);
			return ret;
		} else {
			int index = findNumberOfRecordVar(rec, recVarCall.getVarId()
					.getValue());
			n = block.getNewVar();
			recVarCall.setValMemory(n);
			ret += "%" + n + " = getelementptr inbounds %struct."
					+ ((Id) rec.getParent().getChild(1)).getValue() + "* %"
					+ recVarCall.getRecordId().getValue() + ", i32 0, i32 "
					+ index + "\n";
		}
		return ret;
	}
}

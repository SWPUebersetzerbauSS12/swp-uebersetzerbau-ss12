package de.fuberlin.projecta.analysis.ast;

import java.util.ArrayList;
import java.util.Arrays;

import de.fuberlin.projecta.analysis.BasicTokenType;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.codegen.LLVM;

public class Print extends Statement {

	@Override
	/*
	 * we use the puts function to print to screen %forprinting = load i8**
	 * %str3 call i32 (i8*)* @puts(i8* %forprinting)
	 */
	public String genCode() {
		String out = "";
		Block block = getHighestBlock();
		Type idType = null;
		if (getChild(0) instanceof RecordVarCall) {
			idType = SymbolTableHelper
					.lookupRecordVarCall((RecordVarCall) getChild(0));
			idType = (Type) idType.getParent().getChild(0);
		} else if (getChild(0) instanceof ArrayCall) {
			ArrayCall tmp = (ArrayCall) getChild(0);
			while (tmp.getChild(1) instanceof ArrayCall) {
				tmp = (ArrayCall) tmp.getChild(1);
			}
			if (tmp.getChild(1) instanceof Id) {
				String arrayId = ((Id) tmp.getChild(1)).getValue();
				idType = SymbolTableHelper.lookup(arrayId, this).getType();
				idType = ((Array) idType).getBasicType();
			}
		} else {
			idType = SymbolTableHelper.lookup(((Id) getChild(0)).getValue(),
					this).getType();
		}
		if (idType instanceof BasicType) {

			if (((BasicType) idType).getTokenType() == BasicTokenType.STRING) {

				if (LLVM.isInParams((Id) getChild(0))) {
					out += "%" + getHighestBlock().getNewVar() + " = "
							+ "call i32 (i8*)* @puts(i8* %"
							+ ((Id) getChild(0)).getValue() + ")";
				} else {
					int reg = block.getNewVar();
					out += "%" + reg + " = load i8** %"
							+ ((Id) getChild(0)).getValue() + "\n";
					out += "%" + getHighestBlock().getNewVar() + " = "
							+ "call i32 (i8*)* @puts(i8* %" + reg + ")";
				}

			} else {
				String format = "";
				if (((BasicType) idType).getTokenType() == BasicTokenType.INT) {
					format = "%d";
					out += "";
				} else if (((BasicType) idType).getTokenType() == BasicTokenType.REAL) {
					format = "%f";
				} else if (((BasicType) idType).getTokenType() == BasicTokenType.BOOL) {
					format = "%d";
				}

				// format string
				int tempReg = block.getNewVar();
				out += "%" + tempReg + " = alloca [4 x i8]\n";
				out += "store [4 x i8] c\"" + format
						+ "\\0A\\00\", [4 x i8]* %" + tempReg + "\n";
				int tempReg2 = block.getNewVar();
				out += "%" + tempReg2 + " = getelementptr [4 x i8]* %"
						+ tempReg + ", i8 0, i8 0 \n";
				// now we print
				int valReg = 0;
				out += LLVM.loadType((Expression) getChild(0));
				valReg = block.getCurrentRegister();
				out += "call i32 (i8*, ...)* @printf(i8* %" + tempReg2 + ", "
						+ idType.genCode() + " %" + valReg + ")";
				// implicit return of printf increments var counter!
				block.getNewVar();
			}
		}
		return out;
	}

	@Override
	public void checkTypes() {
		String[] b = { BasicType.TYPE_BOOL_STRING,
				BasicType.TYPE_STRING_STRING, BasicType.TYPE_INT_STRING,
				BasicType.TYPE_REAL_STRING };
		String argumentType = ((Expression) getChild(0)).toTypeString();
		ArrayList<String> validTypes = new ArrayList<String>(Arrays.asList(b));
		if (!validTypes.contains(argumentType))
			throw new SemanticException(
					"Invalid argument to print-function of type "
							+ argumentType, this);
	}
}

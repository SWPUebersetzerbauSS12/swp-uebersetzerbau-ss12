package de.fuberlin.projecta.analysis.ast.nodes;

import java.util.ArrayList;
import java.util.Arrays;

import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.lexer.BasicTokenType;

public class Print extends Statement {

	@Override
	/*
	 * we use the puts function to print to screen 
	 * %forprinting = load i8**
	 * %str3 tail call i32 (i8*)* @puts(i8* %forprinting)
	 */
	public String genCode() {
		String out = "";
		Block block = getHighestBlock();
		EntryType id = SymbolTableHelper.lookup(((Id) getChild(0)).getValue(),
				this);
		if (id.getType() instanceof BasicType) {
			if (((BasicType) id.getType()).getTokenType() == BasicTokenType.STRING) {
				int reg = block.getNewVar();
				out += "%" + reg + " = load i8** %"
						+ ((Id) getChild(0)).getValue() + "\n";
				out += "%" + getHighestBlock().getNewVar() + " = "
						+ "tail call i32 (i8*)* @puts(i8* %" + reg + ")";
			} else {
				String format = "";
				if (((BasicType) id.getType()).getTokenType() == BasicTokenType.INT) {
					format = "%d";
					out += "";
				} else if (((BasicType) id.getType()).getTokenType() == BasicTokenType.REAL) {
					format = "%f";
				} else if (((BasicType) id.getType()).getTokenType() == BasicTokenType.BOOL) {
					format = "%d";
				}
				int tempReg = block.getNewVar();
				int tempReg2 = block.getNewVar();
				int valReg = block.getNewVar();
				//format string
				out += "%" + tempReg + " = alloca [4 x i8]\n";
				out += "store [4 x i8] c\"" + format
						+ "\\0A\\00\", [4 x i8]* %" + tempReg
						+ "\n";
				out += "%" + tempReg2 + " = getelementptr [4 x i8]* %" + tempReg + ", i8 0, i8 0 \n";
				//now we print
				out += "%"+valReg+ " = load "+id.getType().genCode() +"* %"+ ((Id)getChild(0)).getValue()+"\n";
				out += "call i32 (i8*, ...)* @printf(i8* %"+tempReg2+", "+ id.getType().genCode() + " %" +valReg+")";
				// implicit return of printf increments var counter!
				block.getNewVar();
			}
		}
		return out;
	}

	@Override
	public boolean checkTypes() {
		String[] b = {
				Type.TYPE_BOOL_STRING,
				Type.TYPE_STRING_STRING,
				Type.TYPE_INT_STRING,
				Type.TYPE_REAL_STRING
		};
		ArrayList<String> validTypes = new ArrayList<String>(Arrays.asList(b));
		return validTypes.contains(((Type)getChild(0)).toTypeString());
	}
}

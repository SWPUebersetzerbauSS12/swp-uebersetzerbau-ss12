package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.lexer.BasicTokenType;

public class Print extends Statement {

	@Override
	public boolean checkSemantics() {
		return true;
	}

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
				int reg = block.getNewMemory();
				out += "%" + reg + " = load i8** %"
						+ ((Id) getChild(0)).getValue() + "\n";
				out += "%" + getHighestBlock().getNewMemory() + " = "
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
				int tempReg = block.getNewMemory();
				int tempReg2 = block.getNewMemory();
				int valReg = block.getNewMemory();
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
				block.getNewMemory();
			}
		}
		return out;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}

package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projectci.extern.IToken.TokenType;

public class Print extends Statement {

	@Override
	public boolean checkSemantics() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	/*
	 * we use the puts function to print to screen %forprinting = load i8**
	 * %str3 tail call i32 (i8*)* @puts(i8* %forprinting)
	 */
	public String genCode() {
		String out = "";
		SymbolTableHelper helper = new SymbolTableHelper();
		EntryType id = helper.lookup(((Id) getChild(0)).getValue(), this);
		/* Please check if this Id is for a string. How do we do this? */
		//if (id.getType() == TokenType.STRING_LITERAL) {
			int reg = getHighestBlock().getNewRegister();
			out += "%" + reg + " = load i8** %" + ((Id) getChild(0)).getValue()
					+ "\n";
			out += "%" + getHighestBlock().getNewRegister() + " = "
					+ "tail call i32 (i8*)* @puts(i8* %" + reg + ")";
		//}
		return out;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}

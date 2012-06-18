package de.fuberlin.projecta.analysis.ast.nodes;


public class Print extends Statement {

	@Override
	public boolean checkSemantics() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	/*
	 * we use the puts function to print to screen
	 */
	public String genCode() {
		String out = "%" + getHighestBlock().getNewRegister() + " = ";
		out += "tail call i32 (i8*)* @puts(i8* %"
				+ ((Id) getChild(0)).getValue() + ")\t;me likes to print";
		return out;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}

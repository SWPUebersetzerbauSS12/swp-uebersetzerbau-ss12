package de.fuberlin.projecta.analysis.ast;

/**
 * Basic class for all expression-like nodes
 * 
 * Expressions have a type and return a value
 *
 */
public abstract class Expression extends AbstractSyntaxTree {

	private int valMemory;

	public String genCode() {
		return "";
	}

	public String toTypeString() {
		return "";
	}

	/**
	 * genCode must be called before this is set.
	 * 
	 * @return the memory address, in which the node's value is stored
	 */
	public int getVar() {
		return valMemory;
	}

	public void setValMemory(int valMemory) {
		this.valMemory = valMemory;
	}

	public String fromTypeStringToLLVMType() {
		String type = "";
		if (this.toTypeString().equals(BasicType.TYPE_INT_STRING))
			type = "i32";
		else if (this.toTypeString().equals(BasicType.TYPE_REAL_STRING))
			type = "double";
		else if (this.toTypeString().equals(BasicType.TYPE_BOOL_STRING))
			type = "i1";
		else if (this.toTypeString().equals(BasicType.TYPE_STRING_STRING))
			type = "i8*";
		return type;
	}
}

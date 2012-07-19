package de.fuberlin.projecta.analysis.ast;


/**
 * Generic array-based node
 * 
 * First child: Number of entries (dimension)
 * Second child: Type
 */
public class Array extends Type {
	
	public int getDimension() {
		return ((IntLiteral)this.getChild(0)).getValue();
	}

	public Type getBasicType(){
		Array tmp = this;
		while(tmp.getChild(1) instanceof Array)
			tmp = (Array)tmp.getChild(1);
		return (Type)tmp.getChild(1);
	}

	public Type getType() {
		return (Type)this.getChild(1);
	}

	@Override
	public String toTypeString(){
		final int dimension = getDimension();
		return "array(" + dimension + "," + getType().toTypeString() + ")";
	}
	
	@Override
	public String genCode(){
		Type t = getType();
		String ret =  "[" + getDimension() + " x "+ t.genCode() +"]";
		return ret;
	}
}

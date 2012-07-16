package de.fuberlin.projecta.analysis.ast.nodes;


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

	public Type getType() {
		return (Type)this.getChild(1);
	}

	@Override
	public String toTypeString(){
		final int dimension = getDimension();
		return "array(" + dimension + "," + getType().toTypeString() + ")";
	}
}

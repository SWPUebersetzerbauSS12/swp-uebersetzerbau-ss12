package de.fuberlin.projecta.analysis.ast.nodes;


/**
 * first child num
 * second child type' 
 * 
 * @author sh4ke
 */
public class Array extends Type {
	
	@Override
	public String toTypeString(){
		int dimension = ((IntLiteral)this.getChild(0)).getValue();
		Type basicType = (Type) this.getChild(1);
		return "array(" + dimension + "," + basicType.toTypeString() + ")";
	}
}

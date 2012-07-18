package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableHelper;


/**
 * first child num
 * second child Type
 * 
 */
public class ArrayCall extends Type {
	
	public Id getArrayId(){
		ISyntaxTree child = getChild(1);
		while (child instanceof ArrayCall) {
			child = child.getChild(1);
		}
		return (Id) child;
	}

	@Override
	public void checkSemantics() {
		ISyntaxTree child = this;
		do {
			child = child.getChild(1);
		} while (!(child instanceof Id));
		Id id = (Id) child;
		Type type = SymbolTableHelper.lookup(id.getValue(), this).getType();
		// we now have a the type node and the call node
		// we have to look if there both have the same depth
		// if so, the array call is complete
		// if not, the array call is obviously incomplete and therefore the
		// array call is semantically wrong!
		ISyntaxTree self = this;
		ISyntaxTree def = type;
		do{
			self = self.getChild(1);
			def = def.getChild(1);
		} while(!(self instanceof Id || !(def instanceof Array)));
		
		// if both have the same depth self will be an instance of Id and def
		// won't be an instance of array. Otherwise there must be something
		// wrong woth the call
		if (!(self instanceof Id)) {
			if(!(def instanceof Array)){
				throw new SemanticException("More dimensions in array call, than in definition!");
			}
		} else {
			if (def instanceof Array){
				throw new SemanticException("Only full array calls are allowed!");
			}
		}
	}
	
	@Override
	public String toTypeString(){
		
		ISyntaxTree child = this;
		do{
			child = child.getChild(1);
		} while(!(child instanceof Id));
		Id id = (Id) child;
		Type type = SymbolTableHelper.lookup(id.getValue(), this).getType();
		// we already know that the array call is calling a non array type
		// because of semantic check
		do{
			type = (Type) type.getChild(1);
		} while(!(type instanceof BasicType));
		return type.toTypeString();
	}
	
	public Id getVarId() {
		// collect all array references
		ArrayCall tmp = this;
		while(tmp.getChild(1) instanceof ArrayCall)
			tmp = (ArrayCall)tmp.getChild(1);
		return (Id)tmp.getChild(1);
	}
	
	@Override
	public String genCode(){
		//in LLVM class
		return "";
	}
}

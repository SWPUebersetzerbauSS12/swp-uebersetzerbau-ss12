package de.fuberlin.projecta.analysis.ast;

import de.fuberlin.projecta.analysis.BasicTokenType;

public class BasicType extends Type {

	public static final String TYPE_BOOL_STRING = "bool";
	public static final String TYPE_INT_STRING = "int";
	public static final String TYPE_STRING_STRING = "string";
	public static final String TYPE_VOID_STRING = "void";
	public static final String TYPE_REAL_STRING = "real";

	BasicTokenType type;

	public BasicType(BasicTokenType t){
		this.type = t;
	}
	
	@Override
	public String genCode() {
		String ret = "";
		switch (type) {
		case INT:
			ret += "i32";
			break;
		case REAL:
			ret += "double";
			break;
		case STRING:
			ret += "i8*";
			break;
		case BOOL:
			ret += "i1";
			break;
		case VOID:
			ret += "void";
			break;
		}
		return ret;
	}

	public BasicTokenType getTokenType() {
		return type;
	}
	
	@Override
	public String toTypeString() {
		switch (this.type) {
		case BOOL:
			return BasicType.TYPE_BOOL_STRING;
		case REAL:
			return BasicType.TYPE_REAL_STRING;
		case INT:
			return BasicType.TYPE_INT_STRING;
		case STRING:
			return BasicType.TYPE_STRING_STRING;
		case VOID:
			return BasicType.TYPE_VOID_STRING;
		default:
			return null;
		}
	}

}

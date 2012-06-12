package analysis.ast.nodes;

import lexer.BasicTokenType;
import analysis.SymbolTableStack;

public class BasicType extends Type {

	BasicTokenType type;

	public BasicType(BasicTokenType t){
		this.type = t;
	}
	
	public void buildSymbolTable(SymbolTableStack tables) {

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
			ret += "i8";
			break;
		}
		return ret;
	}

	public BasicTokenType getType() {
		return type;
	}

}

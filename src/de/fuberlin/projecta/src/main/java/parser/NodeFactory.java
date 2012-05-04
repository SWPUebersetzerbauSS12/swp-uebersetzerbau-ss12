package parser;

import parser.nodes.*;

public class NodeFactory {
	
	public ISyntaxTree createNode(String name){
		//nonTerminals
		if(name.equals("program"))
			return new program("program");
		if(name.equals("funcs"))
			return new funcs("funcs");
		if(name.equals("func"))
			return new func("func");
		if(name.equals("func'"))
			return new func2("func'");
		if(name.equals("optparams"))
			return new optparams("optparams");
		if(name.equals("params"))
			return new params("params");
		if(name.equals("params'"))
			return new params2("params'");
		if(name.equals("block"))
			return new block("block");
		if(name.equals("decls"))
			return new decls("decls");
		if(name.equals("decl"))
			return new decl("decl");
		if(name.equals("type"))
			return new type("type");
		if(name.equals("type'"))
			return new type2("type'");
		if(name.equals("stmts"))
			return new stmts("stmts");
		if(name.equals("stmt"))
			return new stmt("stmt");
		if(name.equals("stmt'"))
			return new stmt2("stmt'");
		if(name.equals("stmt''"))
			return new stmt3("stmt''");
		if(name.equals("loc"))
			return new loc("loc");
		if(name.equals("loc'"))
			return new loc2("loc'");
		if(name.equals("loc''"))
			return new loc3("loc''");
		if(name.equals("assign"))
			return new assign("assign");
		if(name.equals("assign'"))
			return new assign2("assign'");
		if(name.equals("bool"))
			return new bool("bool");
		if(name.equals("bool'"))
			return new bool2("bool'");
		if(name.equals("join"))
			return new join("join");
		if(name.equals("join'"))
			return new join2("join'");
		if(name.equals("equality"))
			return new equality("equality");
		if(name.equals("equality'"))
			return new equality2("equality'");
		if(name.equals("rel"))
			return new rel("rel");
		if(name.equals("rel'"))
			return new rel2("rel'");
		if(name.equals("expr"))
			return new expr("expr");
		if(name.equals("expr'"))
			return new expr2("expr'");
		if(name.equals("term"))
			return new term("term");
		if(name.equals("term'"))
			return new term2("term'");
		if(name.equals("unary"))
			return new unary("unary");
		if(name.equals("factor"))
			return new factor("factor");
		if(name.equals("factor'"))
			return new factor2("factor'");
		if(name.equals("optargs"))
			return new optargs("optargs");
		if(name.equals("args"))
			return new args("args");
		if(name.equals("args'"))
			return new args2("args'");
		if(name.equals("basic"))
			return new basic("basic");		
		
		//terminals
		if(name.equals("OP_LT"))
			return new OP_LT("OP_LT");
		if(name.equals("OP_LE"))
			return new OP_LE("OP_LE");
		if(name.equals("OP_EQ"))
			return new OP_EQ("OP_EQ");
		if(name.equals("OP_NE"))
			return new OP_NE("OP_NE");
		if(name.equals("OP_GT"))
			return new OP_GT("OP_GT");
		if(name.equals("OP_GE"))
			return new OP_GE("OP_GE");
		if(name.equals("OP_OR"))
			return new OP_OR("OP_OR");
		if(name.equals("OP_AND"))
			return new OP_AND("OP_AND");
		if(name.equals("OP_NOT"))
			return new OP_NOT("OP_NOT");
		if(name.equals("OP_ADD"))
			return new OP_ADD("OP_ADD");
		if(name.equals("OP_MINUS"))
			return new OP_MINUS("OP_MINUS");
		if(name.equals("OP_MUL"))
			return new OP_MUL("OP_MUL");
		if(name.equals("OP_DIV"))
			return new OP_DIV("OP_DIV");
		if(name.equals("OP_ASSIGN"))
			return new OP_ASSIGN("OP_ASSIGN");
		if(name.equals("OP_COMMA"))
			return new OP_COMMA("OP_COMMA");
		if(name.equals("OP_DOT"))
			return new OP_DOT("OP_DOT");
		if(name.equals("OP_SEMIC"))
			return new OP_SEMIC("OP_SEMIC");
		if(name.equals("IF"))
			return new IF("IF");
		if(name.equals("THEN"))
			return new THEN("THEN");
		if(name.equals("ELSE"))
			return new ELSE("ELSE");
		if(name.equals("WHILE"))
			return new WHILE("WHILE");
		if(name.equals("DO"))
			return new DO("DO");
		if(name.equals("BREAK"))
			return new BREAK("BREAK");
		if(name.equals("RETURN"))
			return new RETURN("RETURN");
		if(name.equals("PRINT"))
			return new PRINT("PRINT");
		if(name.equals("DEF"))
			return new DEF("DEF");
		if(name.equals("RECORD"))
			return new RECORD("RECORD");
		if(name.equals("ID"))
			return new ID("ID");
		if(name.equals("BOOL_TYPE"))
			return new BOOL_TYPE("BOOL_TYPE");
		if(name.equals("STRING_TYPE"))
			return new STRING_TYPE("STRING_TYPE");
		if(name.equals("INT_TYPE"))
			return new INT_TYPE("INT_TYPE");
		if(name.equals("REAL_TYPE"))
			return new REAL_TYPE("REAL_TYPE");
		if(name.equals("BOOL_LITERAL"))
			return new BOOL_LITERAL("BOOL_LITERAL");	
		if(name.equals("STRING_LITERAL"))
			return new STRING_LITERAL("STRING_LITERAL");
		if(name.equals("INT_LITERAL"))
			return new INT_LITERAL("INT_LITERAL");
		if(name.equals("REAL_LITERAL"))
			return new REAL_LITERAL("REAL_LITERAL");
		if(name.equals("LPAREN"))
			return new LPAREN("LPAREN");
		if(name.equals("RPAREN"))
			return new RPAREN("RPAREN");
		if(name.equals("LBRACKET"))
			return new LBRACKET("LBRACKET");
		if(name.equals("RBRACKET"))
			return new RBRACKET("RBRACKET");
		if(name.equals("LBRACE"))
			return new LBRACE("LBRACE");
		if(name.equals("RBRACE"))
			return new RBRACE("RBRACE");
		if(name.equals("EOF"))
			return new EOF("EOF");	
		if(name.equals("ε"))
			return new EPSILON("ε");
		
		return null;
	}
}

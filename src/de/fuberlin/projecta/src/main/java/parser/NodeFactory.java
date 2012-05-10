package parser;

import lexer.IToken.TokenType;
import parser.nodes.ConstantTree;
import parser.nodes.ID;
import parser.nodes.NoOpTree;
import parser.nodes.args;
import parser.nodes.args2;
import parser.nodes.assign;
import parser.nodes.assign2;
import parser.nodes.basic;
import parser.nodes.block;
import parser.nodes.bool;
import parser.nodes.bool2;
import parser.nodes.decl;
import parser.nodes.decls;
import parser.nodes.equality;
import parser.nodes.equality2;
import parser.nodes.expr;
import parser.nodes.expr2;
import parser.nodes.factor;
import parser.nodes.factor2;
import parser.nodes.func;
import parser.nodes.func2;
import parser.nodes.funcs;
import parser.nodes.join;
import parser.nodes.join2;
import parser.nodes.loc;
import parser.nodes.loc2;
import parser.nodes.loc3;
import parser.nodes.optargs;
import parser.nodes.optparams;
import parser.nodes.params;
import parser.nodes.params2;
import parser.nodes.program;
import parser.nodes.rel;
import parser.nodes.rel2;
import parser.nodes.stmt;
import parser.nodes.stmt2;
import parser.nodes.stmt3;
import parser.nodes.stmts;
import parser.nodes.term;
import parser.nodes.term2;
import parser.nodes.type;
import parser.nodes.type2;
import parser.nodes.unary;

public class NodeFactory {
	
	public ISyntaxTree createNode(Symbol symbol){
		if (symbol.isNonTerminal())
			return createNode(symbol.asNonTerminal());
		else if (symbol.isTerminal())
			return createNode(symbol.asTerminal());
		return null;
	}

	public ISyntaxTree createNode(NonTerminal symbol) {
		if(symbol.equals(NonTerminal.program))
			return new program("program");
		if(symbol.equals(NonTerminal.funcs))
			return new funcs("funcs");
		if(symbol.equals(NonTerminal.func))
			return new func("func");
		if(symbol.equals(NonTerminal.func_))
			return new func2("func_");
		if(symbol.equals(NonTerminal.optparams))
			return new optparams("optparams");
		if(symbol.equals(NonTerminal.params))
			return new params("params");
		if(symbol.equals(NonTerminal.params_))
			return new params2("params_");
		if(symbol.equals(NonTerminal.block))
			return new block("block");
		if(symbol.equals(NonTerminal.decls))
			return new decls("decls");
		if(symbol.equals(NonTerminal.decl))
			return new decl("decl");
		if(symbol.equals(NonTerminal.type))
			return new type("type");
		if(symbol.equals(NonTerminal.type_))
			return new type2("type_");
		if(symbol.equals(NonTerminal.stmts))
			return new stmts("stmts");
		if(symbol.equals(NonTerminal.stmt))
			return new stmt("stmt");
		if(symbol.equals(NonTerminal.stmt_))
			return new stmt2("stmt_");
		if(symbol.equals(NonTerminal.stmt__))
			return new stmt3("stmt__");
		if(symbol.equals(NonTerminal.loc))
			return new loc("loc");
		if(symbol.equals(NonTerminal.loc_))
			return new loc2("loc_");
		if(symbol.equals(NonTerminal.loc__))
			return new loc3("loc__");
		if(symbol.equals(NonTerminal.assign))
			return new assign("assign");
		if(symbol.equals(NonTerminal.assign_))
			return new assign2("assign_");
		if(symbol.equals(NonTerminal.bool))
			return new bool("bool");
		if(symbol.equals(NonTerminal.bool_))
			return new bool2("bool_");
		if(symbol.equals(NonTerminal.join))
			return new join("join");
		if(symbol.equals(NonTerminal.join_))
			return new join2("join_");
		if(symbol.equals(NonTerminal.equality))
			return new equality("equality");
		if(symbol.equals(NonTerminal.equality_))
			return new equality2("equality_");
		if(symbol.equals(NonTerminal.rel))
			return new rel("rel");
		if(symbol.equals(NonTerminal.rel_))
			return new rel2("rel_");
		if(symbol.equals(NonTerminal.expr))
			return new expr("expr");
		if(symbol.equals(NonTerminal.expr_))
			return new expr2("expr_");
		if(symbol.equals(NonTerminal.term))
			return new term("term");
		if(symbol.equals(NonTerminal.term_))
			return new term2("term_");
		if(symbol.equals(NonTerminal.unary))
			return new unary("unary");
		if(symbol.equals(NonTerminal.factor))
			return new factor("factor");
		if(symbol.equals(NonTerminal.factor_))
			return new factor2("factor_");
		if(symbol.equals(NonTerminal.optargs))
			return new optargs("optargs");
		if(symbol.equals(NonTerminal.args))
			return new args("args");
		if(symbol.equals(NonTerminal.args_))
			return new args2("args_");
		if(symbol.equals(NonTerminal.basic))
			return new basic("basic");

		assert(false);
		return null;
	}

	public ISyntaxTree createNode(TokenType type) {
		switch(type) {
		case BOOL_LITERAL:
		case INT_LITERAL:
		case REAL_LITERAL:
		case STRING_LITERAL:
			return new ConstantTree(type);
		case ID:
			return new ID("id");
		}
		return new NoOpTree("noop");

		/*
		//terminals
		if(name.equals(NonTerminal.OP_LT))
			return new OP_LT("OP_LT");
		if(name.equals(NonTerminal.OP_LE))
			return new OP_LE("OP_LE");
		if(name.equals(NonTerminal.OP_EQ))
			return new OP_EQ("OP_EQ");
		if(name.equals(NonTerminal.OP_NE))
			return new OP_NE("OP_NE");
		if(name.equals(NonTerminal.OP_GT))
			return new OP_GT("OP_GT");
		if(name.equals(NonTerminal.OP_GE))
			return new OP_GE("OP_GE");
		if(name.equals(NonTerminal.OP_OR))
			return new OP_OR("OP_OR");
		if(name.equals(NonTerminal.OP_AND))
			return new OP_AND("OP_AND");
		if(name.equals(NonTerminal.OP_NOT))
			return new OP_NOT("OP_NOT");
		if(name.equals(NonTerminal.OP_ADD))
			return new OP_ADD("OP_ADD");
		if(name.equals(NonTerminal.OP_MINUS))
			return new OP_MINUS("OP_MINUS");
		if(name.equals(NonTerminal.OP_MUL))
			return new OP_MUL("OP_MUL");
		if(name.equals(NonTerminal.OP_DIV))
			return new OP_DIV("OP_DIV");
		if(name.equals(NonTerminal.OP_ASSIGN))
			return new OP_ASSIGN("OP_ASSIGN");
		if(name.equals(NonTerminal.OP_COMMA))
			return new OP_COMMA("OP_COMMA");
		if(name.equals(NonTerminal.OP_DOT))
			return new OP_DOT("OP_DOT");
		if(name.equals(NonTerminal.OP_SEMIC))
			return new OP_SEMIC("OP_SEMIC");
		if(name.equals(NonTerminal.IF))
			return new IF("IF");
		if(name.equals(NonTerminal.THEN))
			return new THEN("THEN");
		if(name.equals(NonTerminal.ELSE))
			return new ELSE("ELSE");
		if(name.equals(NonTerminal.WHILE))
			return new WHILE("WHILE");
		if(name.equals(NonTerminal.DO))
			return new DO("DO");
		if(name.equals(NonTerminal.BREAK))
			return new BREAK("BREAK");
		if(name.equals(NonTerminal.RETURN))
			return new RETURN("RETURN");
		if(name.equals(NonTerminal.PRINT))
			return new PRINT("PRINT");
		if(name.equals(NonTerminal.DEF))
			return new DEF("DEF");
		if(name.equals(NonTerminal.RECORD))
			return new RECORD("RECORD");
		if(name.equals(NonTerminal.ID))
			return new ID("ID");
		if(name.equals(NonTerminal.BOOL_TYPE))
			return new BOOL_TYPE("BOOL_TYPE");
		if(name.equals(NonTerminal.STRING_TYPE))
			return new STRING_TYPE("STRING_TYPE");
		if(name.equals(NonTerminal.INT_TYPE))
			return new INT_TYPE("INT_TYPE");
		if(name.equals(NonTerminal.REAL_TYPE))
			return new REAL_TYPE("REAL_TYPE");
		if(name.equals(NonTerminal.BOOL_LITERAL))
			return new BOOL_LITERAL("BOOL_LITERAL");	
		if(name.equals(NonTerminal.STRING_LITERAL))
			return new STRING_LITERAL("STRING_LITERAL");
		if(name.equals(NonTerminal.INT_LITERAL))
			return new INT_LITERAL("INT_LITERAL");
		if(name.equals(NonTerminal.REAL_LITERAL))
			return new REAL_LITERAL("REAL_LITERAL");
		if(name.equals(NonTerminal.LPAREN))
			return new LPAREN("LPAREN");
		if(name.equals(NonTerminal.RPAREN))
			return new RPAREN("RPAREN");
		if(name.equals(NonTerminal.LBRACKET))
			return new LBRACKET("LBRACKET");
		if(name.equals(NonTerminal.RBRACKET))
			return new RBRACKET("RBRACKET");
		if(name.equals(NonTerminal.LBRACE))
			return new LBRACE("LBRACE");
		if(name.equals(NonTerminal.RBRACE))
			return new RBRACE("RBRACE");
		if(name.equals(NonTerminal.EOF))
			return new EOF("EOF");	
		if(name.equals(NonTerminal.ε))
			return new EPSILON("ε");

		return null;
		*/
	}
}

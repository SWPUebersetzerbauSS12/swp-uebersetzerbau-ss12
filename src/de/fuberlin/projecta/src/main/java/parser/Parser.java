package parser;

import java.util.Stack;
import java.util.Vector;

import lexer.ILexer;
import lexer.IToken;
import lexer.IToken.TokenType;
import lexer.SyntaxErrorException;
import lombok.Getter;

public class Parser {

	private ILexer lexer;
	private ParseTable table;
	private Stack<Symbol> stack;
	private Vector<String> outputs;
	private NodeFactory nodeFactory;

	@Getter
	private ISyntaxTree syntaxTree;		

	public Parser(ILexer lexer) {
		this.lexer = lexer;

		table = new ParseTable(NonTerminal.values(), TokenType.values());
		try {
			fillParseTable();
		} catch (ParserException e) {
			e.printStackTrace();
		}
		
		nodeFactory = new NodeFactory();

		stack = new Stack<Symbol>();
		outputs = new Vector<String>();
		syntaxTree = nodeFactory.createNode(new Symbol(NonTerminal.program));
		assert(syntaxTree != null);
	}

	private void initStack() {
		stack.clear();
		stack.push(new Symbol(TokenType.EOF));
		stack.push(new Symbol(NonTerminal.program));
	}

	public void parse() throws ParserException {
		if (table.isAmbigous()) {
			throw new ParserException(
					"Parsing table is ambigous! Won't start syntax analysis");
		}

		initStack();
		
		IToken token = null;
		try {
			token = lexer.getNextToken();
		} catch (SyntaxErrorException e) {
			e.printStackTrace();
		}

		do {
			Symbol peek = stack.peek();
			if (peek.isTerminal()) {
				TokenType terminal = peek.asTerminal();
				if (terminal == token.getType()) {
					stack.pop();
					try {
						token = lexer.getNextToken();
					} catch (SyntaxErrorException e) {
						e.printStackTrace();
					}
				} else {
					throw new ParserException("Wrong token " + token
							+ " in input");
				}
			} else /** stack symbol is non-terminal */ {
				System.out.println(peek.asNonTerminal());
				System.out.println(token.getType());
				String prod = table.getEntry(peek.asNonTerminal(), token.getType());
				stack.pop();

				String[] tmp = prod.split("::=");
				if (tmp.length == 2) {
					String[] prods = tmp[1].split(" ");
					for (int i = prods.length - 1; i >= 0; i--) {
						if (!prods[i].trim().isEmpty()) {
							Symbol symbol = new Symbol(prods[i]);
							if (symbol.isNonTerminal() && symbol.asNonTerminal() == NonTerminal.EPSILON)
								continue;
							
							stack.push(symbol);
						}
					}
					outputs.add(prod);
				} else if (prod.trim().equals("")) {
					throw new ParserException(
							"Syntax error: No rule in parsing table (Stack: "
									+ peek + ", token: " + token + ")");
				} else {
					throw new ParserException(
							"Wrong structur in parsing table! Productions should "
									+ "be of the form: X ::= Y1 Y2 ... Yk! Problem with: "
									+ prod + "(Stack: " + peek + ", token: "
									+ token + ")");
				}
			}
		} while (!(stack.peek().isTerminal() && stack.peek().asTerminal() == TokenType.EOF));
		//assert(stack.size() == 0);

		createSyntaxTree();
	}

	/**
	 * Call to create the corresponding parse tree from the previous parse call.
	 * 
	 * @throws ParserException
	 */
	private void createSyntaxTree() throws ParserException {
		for (String t : outputs) {

			String[] tmp = t.split("::=");
			String[] tmp2 = tmp[1].split(" ");

			ISyntaxTree node = getNextOccurrence(tmp[0].trim());

			for (int i = 0; i < tmp2.length; i++) {
				if (!tmp2[i].equals("")) {
					ISyntaxTree newNode;
					newNode = nodeFactory.createNode(new Symbol(tmp2[i]));

					if (node == null) {
						throw new ParserException(
								"node is null! Can't append new nodes to null! Tried to append: "
										+ newNode.getName() + " into "
										+ tmp[0].trim());
					}
					
					if(newNode == null){
						throw new ParserException("Can't add null! " + tmp2[i]);
					}

					if (newNode != null && node != null) {
						node.addTree(newNode);
					}
				}
			}
		}
	}
	
	/**
	 * Using Depth-First search to find the node.
	 * 
	 * @param name
	 * @return the next occurrence of the given node, if it has no children yet.
	 */
	private ISyntaxTree getNextOccurrence(String name) {
		assert(syntaxTree != null);

		if (syntaxTree.getName().equals(name)
				&& syntaxTree.getChildrenCount() == 0) {
			return syntaxTree;
		}

		Stack<ISyntaxTree> stack = new Stack<ISyntaxTree>();
		stack.push(syntaxTree);

		while (!stack.isEmpty()) {
			ISyntaxTree tmp = stack.pop();
			if (tmp.getName().equals(name) && tmp.getChildrenCount() == 0) {
				return tmp;
			} else // push all children onto the stack backwards
			{
				int count = tmp.getChildrenCount();
				for (int i = count - 1; i >= 0; i--) {
					stack.push(tmp.getChild(i));
				}
			}
		}
		return null;
	}

	/**
	 * Prints the generated parse tree turned by 90 degree clockwise. Read
	 * direction is still from the top to the bottom.
	 */
	public void printParseTree() {
		printParseTree(syntaxTree, 0);
	}

	private static void printParseTree(ISyntaxTree tree, int depth) {

		for (int i = 0; i <= depth; i++) {
			System.out.print("\t");
		}
		System.out.print(tree.getName());
		System.out.println("");
		int count = tree.getChildrenCount();
		if (count != 0) {
			for (int i = 0; i < count; i++) {
				printParseTree(tree.getChild(i), depth + 1);
			}
		}
	}

	/**
	 * Cells should be filled by Productions of the form: X ::= Y1 Y2 ... Yk
	 * Treats basic as { INT_TYPE, REAL_TYPE, STRING_TYPE, BOOL_TYPE }!
	 * 
	 * @throws ParserException
	 */
	private void fillParseTable() throws ParserException {
		// program
		table.setEntry(NonTerminal.program, TokenType.DEF, "program ::= funcs");

		// funcs
		table.setEntry(NonTerminal.funcs, TokenType.DEF, "funcs ::=  func funcs");
		table.setEntry(NonTerminal.funcs, TokenType.EOF, "funcs ::= EPSILON");

		// func_
		table.setEntry(NonTerminal.func, TokenType.DEF,
				"func ::=  DEF type ID LPAREN optparams RPAREN func_");
		table.setEntry(NonTerminal.func_, TokenType.OP_SEMIC, "func_ ::= OP_SEMIC");
		table.setEntry(NonTerminal.func_, TokenType.LBRACE, "func_ ::= block");

		// optparams
		table.setEntry(NonTerminal.optparams, TokenType.RPAREN, "optparams ::= EPSILON");
		table.setEntry(NonTerminal.optparams, TokenType.INT_TYPE, "optparams ::= params");
		table.setEntry(NonTerminal.optparams, TokenType.REAL_TYPE, "optparams ::= params");
		table.setEntry(NonTerminal.optparams, TokenType.STRING_TYPE,
				"optparams ::= params");
		table.setEntry(NonTerminal.optparams, TokenType.BOOL_TYPE, "optparams ::= params");
		table.setEntry(NonTerminal.optparams, TokenType.RECORD, "optparams ::= params");

		// params
		table.setEntry(NonTerminal.params, TokenType.INT_TYPE,
				"params ::= type ID params_");
		table.setEntry(NonTerminal.params, TokenType.REAL_TYPE,
				"params ::= type ID params_");
		table.setEntry(NonTerminal.params, TokenType.STRING_TYPE,
				"params ::= type ID params_");
		table.setEntry(NonTerminal.params, TokenType.BOOL_TYPE,
				"params ::= type ID params_");
		table.setEntry(NonTerminal.params, TokenType.RECORD, "params ::= type ID params_");

		// params_
		table.setEntry(NonTerminal.params_, TokenType.RPAREN, "params_ ::= EPSILON");
		table.setEntry(NonTerminal.params_, TokenType.OP_COMMA,
				"params_ ::= OP_COMMA params");

		// block
		table.setEntry(NonTerminal.block, TokenType.LBRACE,
				"block ::= LBRACE decls stmts RBRACE");

		// decls
		table.setEntry(NonTerminal.decls, TokenType.OP_NOT, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.OP_MINUS, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.LPAREN, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.ID, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.IF, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.WHILE, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.DO, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.BREAK, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.RETURN, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.PRINT, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.LBRACE, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.RBRACE, "decls ::= EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.INT_LITERAL, "decls ::=  EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.REAL_LITERAL, "decls ::=  EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.STRING_LITERAL, "decls ::=  EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.BOOL_LITERAL, "decls ::=  EPSILON");
		table.setEntry(NonTerminal.decls, TokenType.INT_TYPE, "decls ::=  decl decls");
		table.setEntry(NonTerminal.decls, TokenType.REAL_TYPE, "decls ::=  decl decls");
		table.setEntry(NonTerminal.decls, TokenType.STRING_TYPE, "decls ::=  decl decls");
		table.setEntry(NonTerminal.decls, TokenType.BOOL_TYPE, "decls ::=  decl decls");
		table.setEntry(NonTerminal.decls, TokenType.RECORD, "decls ::=  decl decls");

		// decl
		table.setEntry(NonTerminal.decl, TokenType.INT_TYPE, "decl ::=  type ID OP_SEMIC");
		table.setEntry(NonTerminal.decl, TokenType.REAL_TYPE,
				"decl ::=  type ID OP_SEMIC");
		table.setEntry(NonTerminal.decl, TokenType.STRING_TYPE,
				"decl ::=  type ID OP_SEMIC");
		table.setEntry(NonTerminal.decl, TokenType.BOOL_TYPE,
				"decl ::=  type ID OP_SEMIC");
		table.setEntry(NonTerminal.decl, TokenType.RECORD, "decl ::=  type ID OP_SEMIC");

		// type
		table.setEntry(NonTerminal.type, TokenType.INT_TYPE, "type ::=  basic type_");
		table.setEntry(NonTerminal.type, TokenType.REAL_TYPE, "type ::=  basic type_");
		table.setEntry(NonTerminal.type, TokenType.STRING_TYPE, "type ::=  basic type_");
		table.setEntry(NonTerminal.type, TokenType.BOOL_TYPE, "type ::=  basic type_");
		table.setEntry(NonTerminal.type, TokenType.RECORD,
				"type ::= RECORD LBRACE decls RBRACE type_");

		// type_
		table.setEntry(NonTerminal.type_, TokenType.ID, "type_ ::= EPSILON");
		table.setEntry(NonTerminal.type_, TokenType.LBRACKET,
				"type_ ::= LBRACKET INT_LITERAL RBRACKET type_ ");

		// stmts
		table.setEntry(NonTerminal.stmts, TokenType.ID, "stmts ::= stmt stmts");

		table.setEntry(NonTerminal.stmts, TokenType.IF, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.WHILE, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.DO, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.BREAK, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.RETURN, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.PRINT, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.LBRACE, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.OP_NOT, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.OP_MINUS, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.LPAREN, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.INT_LITERAL, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.REAL_LITERAL, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.BOOL_LITERAL, "stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.STRING_LITERAL,
				"stmts ::= stmt stmts");
		table.setEntry(NonTerminal.stmts, TokenType.RBRACE, "stmts ::= EPSILON");

		// stmt
		table.setEntry(NonTerminal.stmt, TokenType.ID, "stmt ::= assign OP_SEMIC");
		table.setEntry(NonTerminal.stmt, TokenType.OP_NOT, "stmt ::= assign OP_SEMIC");
		table.setEntry(NonTerminal.stmt, TokenType.OP_MINUS, "stmt ::= assign OP_SEMIC");
		table.setEntry(NonTerminal.stmt, TokenType.LPAREN, "stmt ::= assign OP_SEMIC");
		table.setEntry(NonTerminal.stmt, TokenType.INT_LITERAL,
				"stmt ::= assign OP_SEMIC");
		table.setEntry(NonTerminal.stmt, TokenType.REAL_LITERAL,
				"stmt ::= assign OP_SEMIC");
		table.setEntry(NonTerminal.stmt, TokenType.BOOL_LITERAL,
				"stmt ::= assign OP_SEMIC");
		table.setEntry(NonTerminal.stmt, TokenType.STRING_LITERAL,
				"stmt ::= assign OP_SEMIC");

		table.setEntry(NonTerminal.stmt, TokenType.LBRACE, "stmt ::= block");
		table.setEntry(NonTerminal.stmt, TokenType.IF,
				"stmt ::= IF LPAREN assign RPAREN stmt stmt_");
		table.setEntry(NonTerminal.stmt, TokenType.WHILE,
				"stmt ::= WHILE LPAREN assign RPAREN stmt ");
		table.setEntry(NonTerminal.stmt, TokenType.DO,
				"stmt ::= DO stmt WHILE LPAREN assign RPAREN OP_SEMIC ");
		table.setEntry(NonTerminal.stmt, TokenType.BREAK, "stmt ::= BREAK OP_SEMIC ");
		table.setEntry(NonTerminal.stmt, TokenType.RETURN, "stmt ::= RETURN stmt__");
		table.setEntry(NonTerminal.stmt, TokenType.PRINT, "stmt ::= PRINT loc OP_SEMIC ");

		// stmt_
		table.setEntry(NonTerminal.stmt_, TokenType.ELSE, "stmt_ ::= ELSE stmt");

		table.setEntry(NonTerminal.stmt_, TokenType.ID, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.IF, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.WHILE, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.DO, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.BREAK, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.RETURN, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.PRINT, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.LBRACE, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.RBRACE, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.OP_NOT, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.OP_MINUS, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.LPAREN, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.INT_LITERAL, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.REAL_LITERAL, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.STRING_LITERAL, "stmt_ ::= EPSILON");
		table.setEntry(NonTerminal.stmt_, TokenType.BOOL_LITERAL, "stmt_ ::= EPSILON");
		// TODO: missing table.setEntry(NonTerminal.stmt_, TokenType.ELSE, "stmt_ ::= EPSILON");

		// stmt__
		table.setEntry(NonTerminal.stmt__, TokenType.ID, "stmt__ ::= loc OP_SEMIC");
		table.setEntry(NonTerminal.stmt__, TokenType.OP_SEMIC, "stmt__ ::= OP_SEMIC");

		// loc
		table.setEntry(NonTerminal.loc, TokenType.ID, "loc ::=   ID loc__ ");

		// loc_
		table.setEntry(NonTerminal.loc_, TokenType.LBRACKET,
				"loc_ ::= LBRACKET assign RBRACKET");
		table.setEntry(NonTerminal.loc_, TokenType.OP_DOT, "loc_ ::= OP_DOT ID");

		// loc__
		table.setEntry(NonTerminal.loc__, TokenType.LBRACKET, "loc__ ::= loc' loc__ ");
		table.setEntry(NonTerminal.loc__, TokenType.OP_DOT, "loc__ ::= loc' loc__ ");

		table.setEntry(NonTerminal.loc__, TokenType.LPAREN, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_SEMIC, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_LT, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_LE, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_GE, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_GT, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_ADD, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_MINUS, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_MUL, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_DIV, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_EQ, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_NE, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_AND, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_OR, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.RBRACKET, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.RPAREN, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_ASSIGN, "loc__ ::= EPSILON");
		table.setEntry(NonTerminal.loc__, TokenType.OP_COMMA, "loc__ ::= EPSILON");

		// assign
		table.setEntry(NonTerminal.assign, TokenType.ID, "assign ::= bool assign_");
		table.setEntry(NonTerminal.assign, TokenType.LPAREN, "assign ::= bool assign_");
		table.setEntry(NonTerminal.assign, TokenType.OP_MINUS, "assign ::= bool assign_");
		table.setEntry(NonTerminal.assign, TokenType.OP_NOT, "assign ::= bool assign_");
		table.setEntry(NonTerminal.assign, TokenType.INT_LITERAL,
				"assign ::= bool assign_");
		table.setEntry(NonTerminal.assign, TokenType.REAL_LITERAL,
				"assign ::= bool assign_");
		table.setEntry(NonTerminal.assign, TokenType.STRING_LITERAL,
				"assign ::= bool assign_");
		table.setEntry(NonTerminal.assign, TokenType.BOOL_LITERAL,
				"assign ::= bool assign_");

		// assign'
		table.setEntry(NonTerminal.assign_, TokenType.OP_ASSIGN,
				"assign_ ::= OP_ASSIGN assign assign_");
		table.setEntry(NonTerminal.assign_, TokenType.RPAREN, "assign_ ::= EPSILON ");
		table.setEntry(NonTerminal.assign_, TokenType.RBRACKET, "assign_ ::= EPSILON ");
		table.setEntry(NonTerminal.assign_, TokenType.OP_COMMA, "assign_ ::= EPSILON ");
		table.setEntry(NonTerminal.assign_, TokenType.OP_SEMIC, "assign_ ::= EPSILON ");
		// TODO: missing table.setEntry(NonTerminal.assign_, TokenType.OP_ASSIGN,
		// "assign_ ::= EPSILON ");

		// bool
		table.setEntry(NonTerminal.bool, TokenType.ID, "bool ::=  join bool_");
		table.setEntry(NonTerminal.bool, TokenType.LPAREN, "bool ::=  join bool_");
		table.setEntry(NonTerminal.bool, TokenType.INT_LITERAL, "bool ::=  join bool_");
		table.setEntry(NonTerminal.bool, TokenType.OP_MINUS, "bool ::=  join bool_");
		table.setEntry(NonTerminal.bool, TokenType.OP_NOT, "bool ::=  join bool_");
		table.setEntry(NonTerminal.bool, TokenType.REAL_LITERAL, "bool ::=  join bool_");
		table.setEntry(NonTerminal.bool, TokenType.STRING_LITERAL, "bool ::=  join bool_");
		table.setEntry(NonTerminal.bool, TokenType.BOOL_LITERAL, "bool ::=  join bool_");

		// bool_
		table.setEntry(NonTerminal.bool_, TokenType.RPAREN, "bool_ ::= EPSILON ");
		table.setEntry(NonTerminal.bool_, TokenType.RBRACKET, "bool_ ::= EPSILON ");
		table.setEntry(NonTerminal.bool_, TokenType.OP_ASSIGN, "bool_ ::= EPSILON ");
		table.setEntry(NonTerminal.bool_, TokenType.OP_COMMA, "bool_ ::= EPSILON ");
		table.setEntry(NonTerminal.bool_, TokenType.OP_SEMIC, "bool_ ::= EPSILON ");
		table.setEntry(NonTerminal.bool_, TokenType.OP_OR, "bool_ ::= OP_OR join bool_");

		// join
		table.setEntry(NonTerminal.join, TokenType.ID, "join ::= equality join_");
		table.setEntry(NonTerminal.join, TokenType.LPAREN, "join ::= equality join_");
		table.setEntry(NonTerminal.join, TokenType.INT_LITERAL, "join ::= equality join_");
		table.setEntry(NonTerminal.join, TokenType.OP_MINUS, "join ::= equality join_");
		table.setEntry(NonTerminal.join, TokenType.OP_NOT, "join ::= equality join_");
		table.setEntry(NonTerminal.join, TokenType.REAL_LITERAL,
				"join ::= equality join_");
		table.setEntry(NonTerminal.join, TokenType.STRING_LITERAL,
				"join ::= equality join_");
		table.setEntry(NonTerminal.join, TokenType.BOOL_LITERAL,
				"join ::= equality join_");

		// join_
		table.setEntry(NonTerminal.join_, TokenType.RPAREN, "join_ ::= EPSILON ");
		table.setEntry(NonTerminal.join_, TokenType.RBRACKET, "join_ ::= EPSILON ");
		table.setEntry(NonTerminal.join_, TokenType.OP_ASSIGN, "join_ ::= EPSILON ");
		table.setEntry(NonTerminal.join_, TokenType.OP_COMMA, "join_ ::= EPSILON ");
		table.setEntry(NonTerminal.join_, TokenType.OP_OR, "join_ ::= EPSILON ");
		table.setEntry(NonTerminal.join_, TokenType.OP_SEMIC, "join_ ::= EPSILON ");
		table.setEntry(NonTerminal.join_, TokenType.OP_AND,
				"join_ ::= OP_AND equality join_");

		// equality
		table.setEntry(NonTerminal.equality, TokenType.ID, "equality ::= rel equality_");
		table.setEntry(NonTerminal.equality, TokenType.LPAREN,
				"equality ::= rel equality_");
		table.setEntry(NonTerminal.equality, TokenType.INT_LITERAL,
				"equality ::= rel equality_");
		table.setEntry(NonTerminal.equality, TokenType.OP_MINUS,
				"equality ::= rel equality_");
		table.setEntry(NonTerminal.equality, TokenType.OP_NOT,
				"equality ::= rel equality_");
		table.setEntry(NonTerminal.equality, TokenType.REAL_LITERAL,
				"equality ::= rel equality_");
		table.setEntry(NonTerminal.equality, TokenType.STRING_LITERAL,
				"equality ::= rel equality_");
		table.setEntry(NonTerminal.equality, TokenType.BOOL_LITERAL,
				"equality ::= rel equality_");

		// equality_
		table.setEntry(NonTerminal.equality_, TokenType.RPAREN, "equality_ ::= EPSILON ");
		table.setEntry(NonTerminal.equality_, TokenType.RBRACKET, "equality_ ::= EPSILON ");
		table.setEntry(NonTerminal.equality_, TokenType.OP_ASSIGN, "equality_ ::= EPSILON ");
		table.setEntry(NonTerminal.equality_, TokenType.OP_COMMA, "equality_ ::= EPSILON ");
		table.setEntry(NonTerminal.equality_, TokenType.OP_OR, "equality_ ::= EPSILON ");
		table.setEntry(NonTerminal.equality_, TokenType.OP_AND, "equality_ ::= EPSILON ");
		table.setEntry(NonTerminal.equality_, TokenType.OP_SEMIC, "equality_ ::= EPSILON ");
		table.setEntry(NonTerminal.equality_, TokenType.OP_EQ,
				"equality_ ::= OP_EQ rel equality_");
		table.setEntry(NonTerminal.equality_, TokenType.OP_NE,
				"equality_ ::= OP_NE rel equality_");

		// rel
		table.setEntry(NonTerminal.rel, TokenType.ID, "rel ::= expr  rel_");
		table.setEntry(NonTerminal.rel, TokenType.LPAREN, "rel ::= expr  rel_");
		table.setEntry(NonTerminal.rel, TokenType.INT_LITERAL, "rel ::= expr  rel_");
		table.setEntry(NonTerminal.rel, TokenType.OP_MINUS, "rel ::= expr  rel_");
		table.setEntry(NonTerminal.rel, TokenType.OP_NOT, "rel ::= expr  rel_");
		table.setEntry(NonTerminal.rel, TokenType.REAL_LITERAL, "rel ::= expr  rel_");
		table.setEntry(NonTerminal.rel, TokenType.STRING_LITERAL, "rel ::= expr  rel_");
		table.setEntry(NonTerminal.rel, TokenType.BOOL_LITERAL, "rel ::= expr  rel_");

		// rel_
		table.setEntry(NonTerminal.rel_, TokenType.RPAREN, "rel_ ::= EPSILON ");
		table.setEntry(NonTerminal.rel_, TokenType.RBRACKET, "rel_ ::= EPSILON ");
		table.setEntry(NonTerminal.rel_, TokenType.OP_ASSIGN, "rel_ ::= EPSILON ");
		table.setEntry(NonTerminal.rel_, TokenType.OP_COMMA, "rel_ ::= EPSILON ");
		table.setEntry(NonTerminal.rel_, TokenType.OP_SEMIC, "rel_ ::= EPSILON ");
		table.setEntry(NonTerminal.rel_, TokenType.OP_OR, "rel_ ::= EPSILON ");
		table.setEntry(NonTerminal.rel_, TokenType.OP_AND, "rel_ ::= EPSILON ");
		table.setEntry(NonTerminal.rel_, TokenType.OP_EQ, "rel_ ::= EPSILON ");
		table.setEntry(NonTerminal.rel_, TokenType.OP_NE, "rel_ ::= EPSILON ");
		table.setEntry(NonTerminal.rel_, TokenType.OP_LT, "rel_ ::= OP_LT expr");
		table.setEntry(NonTerminal.rel_, TokenType.OP_LE, "rel_ ::= OP_LE expr");
		table.setEntry(NonTerminal.rel_, TokenType.OP_GE, "rel_ ::= OP_GE expr");
		table.setEntry(NonTerminal.rel_, TokenType.OP_GT, "rel_ ::= OP_GT expr");

		// expr
		table.setEntry(NonTerminal.expr, TokenType.ID, "expr ::= term expr_");
		table.setEntry(NonTerminal.expr, TokenType.LPAREN, "expr ::= term expr_");
		table.setEntry(NonTerminal.expr, TokenType.INT_LITERAL, "expr ::= term expr_");
		table.setEntry(NonTerminal.expr, TokenType.OP_MINUS, "expr ::= term expr_");
		table.setEntry(NonTerminal.expr, TokenType.OP_NOT, "expr ::= term expr_");
		table.setEntry(NonTerminal.expr, TokenType.REAL_LITERAL, "expr ::= term expr_");
		table.setEntry(NonTerminal.expr, TokenType.STRING_LITERAL, "expr ::= term expr_");
		table.setEntry(NonTerminal.expr, TokenType.BOOL_LITERAL, "expr ::= term expr_");

		// expr_
		table.setEntry(NonTerminal.expr_, TokenType.OP_ADD, "expr_ ::= OP_ADD term expr_");
		table.setEntry(NonTerminal.expr_, TokenType.OP_MINUS,
				"expr_ ::= OP_MINUS term expr_");
		table.setEntry(NonTerminal.expr_, TokenType.OP_LT, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_LE, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_GE, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_GT, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_EQ, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_NE, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_AND, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_OR, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.RBRACKET, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.RPAREN, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_SEMIC, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_ASSIGN, "expr_ ::= EPSILON ");
		table.setEntry(NonTerminal.expr_, TokenType.OP_COMMA, "expr_ ::= EPSILON ");

		// term
		table.setEntry(NonTerminal.term, TokenType.ID, "term ::= unary term_");
		table.setEntry(NonTerminal.term, TokenType.LPAREN, "term ::= unary term_");
		table.setEntry(NonTerminal.term, TokenType.INT_LITERAL, "term ::= unary term_");
		table.setEntry(NonTerminal.term, TokenType.OP_MINUS, "term ::= unary term_");
		table.setEntry(NonTerminal.term, TokenType.OP_NOT, "term ::= unary term_");
		table.setEntry(NonTerminal.term, TokenType.REAL_LITERAL, "term ::= unary term_");
		table.setEntry(NonTerminal.term, TokenType.STRING_LITERAL, "term ::= unary term_");
		table.setEntry(NonTerminal.term, TokenType.BOOL_LITERAL, "term ::= unary term_");

		// term_
		table.setEntry(NonTerminal.term_, TokenType.OP_MUL,
				"term_ ::= OP_MUL unary term_");
		table.setEntry(NonTerminal.term_, TokenType.OP_DIV,
				"term_ ::= OP_DIV unary term_");
		table.setEntry(NonTerminal.term_, TokenType.OP_LT, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_LE, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_GE, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_GT, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_ADD, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_MINUS, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_EQ, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_NE, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_AND, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_OR, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.RBRACKET, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.RPAREN, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_SEMIC, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_ASSIGN, "term_ ::= EPSILON ");
		table.setEntry(NonTerminal.term_, TokenType.OP_COMMA, "term_ ::= EPSILON ");

		// unary
		table.setEntry(NonTerminal.unary, TokenType.ID, "unary ::= factor");
		table.setEntry(NonTerminal.unary, TokenType.LPAREN, "unary ::= factor");
		table.setEntry(NonTerminal.unary, TokenType.INT_LITERAL, "unary ::= factor");
		table.setEntry(NonTerminal.unary, TokenType.REAL_LITERAL, "unary ::= factor");
		table.setEntry(NonTerminal.unary, TokenType.STRING_LITERAL, "unary ::= factor");
		table.setEntry(NonTerminal.unary, TokenType.BOOL_LITERAL, "unary ::= factor");
		table.setEntry(NonTerminal.unary, TokenType.OP_MINUS, "unary ::= OP_MINUS unary");
		table.setEntry(NonTerminal.unary, TokenType.OP_NOT, "unary ::= OP_NOT unary");

		// factor
		table.setEntry(NonTerminal.factor, TokenType.ID, "factor ::= loc factor_");
		table.setEntry(NonTerminal.factor, TokenType.LPAREN,
				"factor ::= LPAREN assign RPAREN");
		table.setEntry(NonTerminal.factor, TokenType.INT_LITERAL,
				"factor ::= INT_LITERAL");
		table.setEntry(NonTerminal.factor, TokenType.REAL_LITERAL,
				"factor ::= REAL_LITERAL");
		table.setEntry(NonTerminal.factor, TokenType.STRING_LITERAL,
				"factor ::= STRING_LITERAL");
		table.setEntry(NonTerminal.factor, TokenType.BOOL_LITERAL,
				"factor ::= BOOL_LITERAL");

		// factor_
		table.setEntry(NonTerminal.factor_, TokenType.LPAREN,
				"factor_ ::= LPAREN optargs RPAREN ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_LT, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_LE, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_GE, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_GT, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_ADD, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_MINUS, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_MUL, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_DIV, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_EQ, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_NE, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_AND, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_OR, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.RBRACKET, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.RPAREN, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_SEMIC, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_ASSIGN, "factor_ ::= EPSILON ");
		table.setEntry(NonTerminal.factor_, TokenType.OP_COMMA, "factor_ ::= EPSILON ");

		// optargs
		table.setEntry(NonTerminal.optargs, TokenType.ID, "optargs ::= args");
		table.setEntry(NonTerminal.optargs, TokenType.OP_NOT, "optargs ::= args");
		table.setEntry(NonTerminal.optargs, TokenType.OP_MINUS, "optargs ::= args");
		table.setEntry(NonTerminal.optargs, TokenType.LPAREN, "optargs ::= args");
		table.setEntry(NonTerminal.optargs, TokenType.INT_LITERAL, "optargs ::= args");
		table.setEntry(NonTerminal.optargs, TokenType.REAL_LITERAL, "optargs ::= args");
		table.setEntry(NonTerminal.optargs, TokenType.BOOL_LITERAL, "optargs ::= args");
		table.setEntry(NonTerminal.optargs, TokenType.STRING_LITERAL, "optargs ::= args");
		table.setEntry(NonTerminal.optargs, TokenType.RPAREN, "optargs ::= EPSILON");

		// args
		table.setEntry(NonTerminal.args, TokenType.ID, "args ::= assign args_");
		table.setEntry(NonTerminal.args, TokenType.LPAREN, "args ::= assign args_");
		table.setEntry(NonTerminal.args, TokenType.INT_LITERAL, "args ::= assign args_");
		table.setEntry(NonTerminal.args, TokenType.OP_MINUS, "args ::= assign args_");
		table.setEntry(NonTerminal.args, TokenType.OP_NOT, "args ::= assign args_");
		table.setEntry(NonTerminal.args, TokenType.REAL_LITERAL, "args ::= assign args_");
		table.setEntry(NonTerminal.args, TokenType.STRING_LITERAL,
				"args ::= assign args_");
		table.setEntry(NonTerminal.args, TokenType.BOOL_LITERAL, "args ::= assign args_");

		// args_
		table.setEntry(NonTerminal.args_, TokenType.RPAREN, "args_ ::= EPSILON");
		table.setEntry(NonTerminal.args_, TokenType.OP_COMMA, "args_ ::= OP_COMMA args");

		// basic
		table.setEntry(NonTerminal.basic, TokenType.INT_TYPE, "basic ::=  INT_TYPE");
		table.setEntry(NonTerminal.basic, TokenType.REAL_TYPE, "basic ::= REAL_TYPE");
		table.setEntry(NonTerminal.basic, TokenType.STRING_TYPE, "basic ::=  STRING_TYPE");
		table.setEntry(NonTerminal.basic, TokenType.BOOL_TYPE, "basic ::=  BOOL_TYPE");
	}

}

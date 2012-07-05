package de.fuberlin.projecta.analysis;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISymbol;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.ast.nodes.AbstractSyntaxTree;
import de.fuberlin.projecta.analysis.ast.nodes.Args;
import de.fuberlin.projecta.analysis.ast.nodes.Array;
import de.fuberlin.projecta.analysis.ast.nodes.ArrayCall;
import de.fuberlin.projecta.analysis.ast.nodes.BasicType;
import de.fuberlin.projecta.analysis.ast.nodes.BinaryOp;
import de.fuberlin.projecta.analysis.ast.nodes.Block;
import de.fuberlin.projecta.analysis.ast.nodes.BoolLiteral;
import de.fuberlin.projecta.analysis.ast.nodes.Break;
import de.fuberlin.projecta.analysis.ast.nodes.Declaration;
import de.fuberlin.projecta.analysis.ast.nodes.Do;
import de.fuberlin.projecta.analysis.ast.nodes.FuncCall;
import de.fuberlin.projecta.analysis.ast.nodes.FuncDef;
import de.fuberlin.projecta.analysis.ast.nodes.Id;
import de.fuberlin.projecta.analysis.ast.nodes.If;
import de.fuberlin.projecta.analysis.ast.nodes.IfElse;
import de.fuberlin.projecta.analysis.ast.nodes.IntLiteral;
import de.fuberlin.projecta.analysis.ast.nodes.Params;
import de.fuberlin.projecta.analysis.ast.nodes.Print;
import de.fuberlin.projecta.analysis.ast.nodes.Program;
import de.fuberlin.projecta.analysis.ast.nodes.RealLiteral;
import de.fuberlin.projecta.analysis.ast.nodes.Record;
import de.fuberlin.projecta.analysis.ast.nodes.RecordVarCall;
import de.fuberlin.projecta.analysis.ast.nodes.Return;
import de.fuberlin.projecta.analysis.ast.nodes.Statement;
import de.fuberlin.projecta.analysis.ast.nodes.StringLiteral;
import de.fuberlin.projecta.analysis.ast.nodes.Type;
import de.fuberlin.projecta.analysis.ast.nodes.UnaryOp;
import de.fuberlin.projecta.analysis.ast.nodes.While;
import de.fuberlin.projecta.lexer.BasicTokenType;
import de.fuberlin.projecta.parser.NonTerminal;
import de.fuberlin.projecta.parser.Parser;
import de.fuberlin.projecta.parser.Symbol;
import de.fuberlin.projecta.parser.Symbol.Reserved;

public class SemanticAnalyzer {

	private static final String L_ATTRIBUTE = "LAttribute";

	private ISyntaxTree parseTree;

	private SymbolTableStack tables;

	private AbstractSyntaxTree AST;

	public SemanticAnalyzer(ISyntaxTree tree) {
		this.parseTree = tree;
		tables = new SymbolTableStack();
	}

	public void analyze() throws SemanticException {
		toAST(parseTree);
		parseTreeForRemoval();
		parseTreeForBuildingSymbolTable();
		AST.printTree();
		checkForValidity(AST);
	}

	public void toAST(ISyntaxTree tree) {
		toAST(tree, null);
	}

	private Symbol translate(ISymbol symbol) {
		if (symbol instanceof Symbol)
			return (Symbol) symbol;
		else {
			// let Symbol figure out which type this is
			return new Symbol(symbol.getName());
		}
	}

	/**
	 * Traverses through the parseTree in depth-first-search and adds new nodes
	 * to the given insertNode. Only l-attributed nodes must be passed through
	 * node-attributes!
	 * 
	 * The idea is that one node knows where it should get added to.
	 * 
	 * @param tree
	 *            current parseTree-node (with symbol)
	 * @param insertNode
	 *            syntaxTree-Node, in which new nodes get added
	 */
	public void toAST(ISyntaxTree tree, ISyntaxTree insertNode) {
		Symbol symbol = translate(tree.getSymbol());
		if (symbol.isNonTerminal()) {
			NonTerminal nonT = symbol.asNonTerminal();

			switch (nonT) {
			case program:
				AST = new Program();
				toAST(tree.getChild(0), AST);
				return;
			case funcs:
				for (int i = 0; i < tree.getChildrenCount(); i++) {
					toAST(tree.getChild(i), insertNode);
				}
				return;
			case func:
				FuncDef func = new FuncDef();
				for (int i = 0; i < tree.getChildrenCount(); i++) {
					toAST(tree.getChild(i), func);
				}
				insertNode.addChild(func);
				return;
			case type:
				if (translate(tree.getChild(0).getSymbol()).asTerminal() == TokenType.BASIC) {

					// this is type_ and it must exist!
					if (tree.getChild(1).getChildrenCount() != 0) {
						Type array = new Array();

						// the basic node gets added to this temporary node
						// and is passed to the new array
						// it doesn't matter which node to take as long as
						// it is a treenode.
						ISyntaxTree tmp = new Program();
						toAST(tree.getChild(0), tmp);

						array.addAttribute(L_ATTRIBUTE);
						// this is already the BasicType node
						boolean success = array.setAttribute(L_ATTRIBUTE,
								tmp.getChild(0));
						assert (success);

						insertNode.addChild(array);

						toAST(tree.getChild(1), array);

					} else
						// type_ is empty, hook the basic node in the parent
						toAST(tree.getChild(0), insertNode);

				} else { // we have a record! *CONGRATS*
					Record record = new Record();

					// test if we have an array of this record
					if (tree.getChild(4).getChildrenCount() != 0) {
						Type array = new Array();
						toAST(tree.getChild(2), record); // <-- decls!!!

						array.addAttribute(L_ATTRIBUTE);
						array.setAttribute(L_ATTRIBUTE, record);

						toAST(tree.getChild(4), array);

						insertNode.addChild(array);

					} else // this is simply one record
					{

						toAST(tree.getChild(2), record); // <-- decls!!!
						insertNode.addChild(record);
					}
				}
				return;
			case type_:
				// TODO: Width of array!!!

				toAST(tree.getChild(1), insertNode);

				if (tree.getChild(3).getChildrenCount() != 0) {
					Array array = new Array();
					array.addAttribute(L_ATTRIBUTE);
					array.setAttribute(L_ATTRIBUTE,
							insertNode.getAttribute(L_ATTRIBUTE));
					toAST(tree.getChild(3), array);
					insertNode.addChild(array);
				} else // array declaration stopped here...
				{
					insertNode.addChild((ISyntaxTree) insertNode
							.getAttribute(L_ATTRIBUTE));
				}

				return;
			case optparams:
				Params params = new Params();
				for (int i = 0; i < tree.getChildrenCount(); i++) {
					toAST(tree.getChild(i), params);
				}
				insertNode.addChild(params);
				return;
			case block:
				Block block = new Block();
				for (int i = 0; i < tree.getChildrenCount(); i++) {
					toAST(tree.getChild(i), block);
				}
				if (block.getChildrenCount() != 0) {
					insertNode.addChild(block);
				} // else func_ -> ;
				return;
			case decl:
				Declaration decl = new Declaration();
				for (int i = 0; i < tree.getChildrenCount(); i++) {
					toAST(tree.getChild(i), decl);
				}
				insertNode.addChild(decl);
				return;
			case stmt: {
				Statement stmt = null;
				ISyntaxTree firstChild = tree.getChild(0);
				Symbol firstChildSymbol = translate(firstChild.getSymbol());
				if (firstChildSymbol.isTerminal()) {
					TokenType terminal = translate(firstChild.getSymbol())
							.asTerminal();
					if (terminal == TokenType.IF) {
						ISyntaxTree stmt_ = tree.getChild(5); //
						if (stmt_.getChildrenCount() == 0)
							stmt = new If();
						else
							stmt = new IfElse();
					} else if (terminal == TokenType.WHILE) {
						stmt = new While();
					} else if (terminal == TokenType.DO) {
						stmt = new Do();
					} else if (terminal == TokenType.BREAK) {
						stmt = new Break();
					} else if (terminal == TokenType.RETURN) {
						stmt = new Return();
					} else if (terminal == TokenType.PRINT) {
						stmt = new Print();
					}
				} else if (firstChildSymbol.isNonTerminal()) {
					toAST(firstChild, insertNode);
				}

				if (stmt != null) {
					for (int i = 0; i < tree.getChildrenCount(); i++) {
						toAST(tree.getChild(i), stmt);
					}
					insertNode.addChild(stmt);
				}
				return;
			}
			case assign:
			case bool:
			case join:
			case equality:
			case rel:
			case expr:
			case term:
				if (tree.getChild(1).getChildrenCount() == 0) {
					toAST(tree.getChild(0), insertNode);
				} else {
					BinaryOp bOp = new BinaryOp(translate(
							tree.getChild(1).getChild(0).getSymbol())
							.asTerminal());

					// simply hang in both children trees
					toAST(tree.getChild(0), bOp); // rel
					toAST(tree.getChild(1), bOp); // equality'
					insertNode.addChild(bOp);
				}
				return;
			case expr_:
			case term_:
				if (tree.getChildrenCount() != 0) {
					// simply hang in both children trees
					toAST(tree.getChild(1), insertNode);
					toAST(tree.getChild(2), insertNode);
				}
				return;
			case unary: {
				Symbol firstChildSymbol = translate(tree.getChild(0)
						.getSymbol());
				if (firstChildSymbol.isNonTerminal()) {
					toAST(tree.getChild(0), insertNode);
				} else {
					UnaryOp uOp = new UnaryOp(firstChildSymbol.asTerminal());

					if (uOp != null) {
						// simply hang in both children trees
						toAST(tree.getChild(1), uOp); // unary
						insertNode.addChild(uOp);
					}
				}
			}
			case factor:
				if (translate(tree.getChild(0).getSymbol()).isNonTerminal()) {
					if (tree.getChildrenCount() >= 2) {
						if (tree.getChild(1).getChildrenCount() == 0) {
							toAST(tree.getChild(0), insertNode);
						} else {
							FuncCall call = new FuncCall();
							for (ISyntaxTree tmp : tree.getChildren()) {
								toAST(tmp, call);
							}
							insertNode.addChild(call);
						}
					}
				} else {
					for (ISyntaxTree tmp : tree.getChildren()) {
						toAST(tmp, insertNode);
					}
				}
				return;
			case factor_:
				if (tree.getChild(1).getChildrenCount() != 0) {
					Args args = new Args();
					toAST(tree.getChild(1), args);
					insertNode.addChild(args);
				}
				return;
			case loc:
				if (tree.getChild(1).getChildrenCount() == 0) {
					toAST(tree.getChild(0), insertNode);
				} else {
					ISyntaxTree tmp = new Program();
					toAST(tree.getChild(0), tmp);
					tree.getChild(1).addAttribute(L_ATTRIBUTE);
					// there is only one child (the id itself)!
					tree.getChild(1).setAttribute(L_ATTRIBUTE, tmp.getChild(0));
					toAST(tree.getChild(1), insertNode);
				}
				return;
			case loc__:
				tree.getChild(0).addAttribute(L_ATTRIBUTE);
				tree.getChild(0).setAttribute(L_ATTRIBUTE,
						tree.getAttribute(L_ATTRIBUTE));
				if (tree.getChild(1).getChildrenCount() == 0) {
					toAST(tree.getChild(0), insertNode);
				} else {
					ISyntaxTree tmp = new Program();
					toAST(tree.getChild(0), tmp);
					if (tmp.getChildrenCount() == 1) {
						tree.getChild(1).addAttribute(L_ATTRIBUTE);
						tree.getChild(1).setAttribute(L_ATTRIBUTE,
								tmp.getChild(0));
						toAST(tree.getChild(1), insertNode);
					}
				}
				return;
			case loc_:
				// TODO: not everything is well thought atm...
				if (translate(tree.getChild(0).getSymbol()).asTerminal() == TokenType.OP_DOT) {
					RecordVarCall varCall = new RecordVarCall();
					varCall.addChild((ISyntaxTree) tree
							.getAttribute(L_ATTRIBUTE));

					toAST(tree.getChild(1), varCall);
					insertNode.addChild(varCall);
				} else {
					ArrayCall array = new ArrayCall();
					toAST(tree.getChild(1), array);
					array.addChild((ISyntaxTree) tree.getAttribute(L_ATTRIBUTE));
					insertNode.addChild(array);
				}
				return;

				// list of nodes which use the default case:
				// assign_, bool_, decls, equality_, expr_, factor_,
				// func_, join_,
				// params, params_, rel_, stmt_, stmt__, stmts,
			default:
				// nothing to do here just pass it through
				for (ISyntaxTree tmp : tree.getChildren()) {
					toAST(tmp, insertNode);
				}
			}

		} else if (symbol.isTerminal()) {
			TokenType t = symbol.asTerminal();
			switch (t) {
			case BASIC:
				BasicTokenType type = (BasicTokenType) tree
						.getAttribute(Parser.TOKEN_VALUE);
				insertNode.addChild(new BasicType(type));
				return;
			case INT_LITERAL:
				insertNode.addChild(new IntLiteral((Integer) tree
						.getAttribute(Parser.TOKEN_VALUE)));
				return;
			case STRING_LITERAL:
				insertNode.addChild(new StringLiteral((String) tree
						.getAttribute(Parser.TOKEN_VALUE)));
				return;
			case REAL_LITERAL:
				insertNode.addChild(new RealLiteral((Double) tree
						.getAttribute(Parser.TOKEN_VALUE)));
				return;
			case BOOL_LITERAL:
				insertNode.addChild(new BoolLiteral((Boolean) tree
						.getAttribute(Parser.TOKEN_VALUE)));
				return;
			case ID:
				insertNode.addChild(new Id((String) tree
						.getAttribute(Parser.TOKEN_VALUE)));
				return;
			default:// everything, which has no class member in its node uses
					// the default.
			}
		} else if (symbol.isReservedTerminal()) {
			Reserved res = symbol.asReservedTerminal();
			switch (res) {
			case EPSILON:
				if (tree.getChildrenCount() == 1) {
					toAST(tree.getChild(0));
				} else {
					// this should never occur
					throw new SemanticException(
							"Epsilon in other position than head?");
				}
			case SP:
				// this should never occur
				// throw new SemanticException("Stack pointer in parsetree?");
			}
		}
	}

	/**
	 * Should find productions which are semantically nonsense. Like double
	 * assignment, funcCalls which aren't declared in this scope, assessing
	 * record members which aren't existing or where the record does not exist,
	 * ...
	 * 
	 * @param tree
	 * @return
	 */
	private boolean checkForValidity(ISyntaxTree tree) {
		return true;
	}

	/**
	 * Runs every nodes run method in depth-first-left-to-right order.
	 * 
	 * @param tree
	 */
	private void parseTreeForBuildingSymbolTable() {
		AST.buildSymbolTable(tables);
	}

	/**
	 * Checks whether the tree contains disallowed semantic.
	 */
	private void parseTreeForRemoval() {
		// TODO: think about how to smartly encode disallowed trees
	}

	public SymbolTableStack getTables() {
		return tables;
	}

	public AbstractSyntaxTree getAST() {
		return AST;
	}
}

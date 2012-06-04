package analysis;

import lexer.TokenType;
import lombok.Getter;
import parser.ISyntaxTree;
import parser.Tree.DefaultAttribute;
import parser.NonTerminal;
import parser.Symbol.Reserved;
import analysis.ast.nodes.Args;
import analysis.ast.nodes.Array;
import analysis.ast.nodes.ArrayCall;
import analysis.ast.nodes.BasicType;
import analysis.ast.nodes.BinaryOp;
import analysis.ast.nodes.Block;
import analysis.ast.nodes.BoolLiteral;
import analysis.ast.nodes.Break;
import analysis.ast.nodes.Declaration;
import analysis.ast.nodes.Do;
import analysis.ast.nodes.FuncCall;
import analysis.ast.nodes.FuncDef;
import analysis.ast.nodes.Id;
import analysis.ast.nodes.If;
import analysis.ast.nodes.IfElse;
import analysis.ast.nodes.IntLiteral;
import analysis.ast.nodes.Params;
import analysis.ast.nodes.Print;
import analysis.ast.nodes.Program;
import analysis.ast.nodes.RealLiteral;
import analysis.ast.nodes.Record;
import analysis.ast.nodes.RecordVarCall;
import analysis.ast.nodes.Return;
import analysis.ast.nodes.Statement;
import analysis.ast.nodes.StringLiteral;
import analysis.ast.nodes.Type;
import analysis.ast.nodes.UnaryOp;
import analysis.ast.nodes.While;

public class SemanticAnalyzer {

	private static final String lattribute = "lattribute";

	private ISyntaxTree parseTree;
	private SymbolTableStack tables;
	@Getter
	private ISyntaxTree AST;

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

		if (tree.getSymbol().isNonTerminal()) {
			NonTerminal nonT = tree.getSymbol().asNonTerminal();

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
				if (tree.getChild(0).getSymbol().isNonTerminal()) {
					if (tree.getChild(0).getSymbol().asNonTerminal() == NonTerminal.basic) {

						if (tree.getChild(1).getChildrenCount() != 0) { // this
																		// is
																		// type_
																		// and
																		// it
																		// must
																		// exist!
							Type array = new Array();

							// the basic node gets added to this temporary node
							// and is passed to the new array
							ISyntaxTree tmp = new Program(); // it doesn't
																// matter
																// which node to
																// take as long
																// as
																// it is a
																// treenode.
							toAST(tree.getChild(0), tmp);

							array.addAttribute(lattribute);
							boolean success = array.setAttribute(lattribute,
									tmp.getChild(0)); // this is already the
														// BasicType node
							assert (success);

							insertNode.addChild(array);

							toAST(tree.getChild(1), array);

						} else
							// type_ is empty, hook the basic node in the parent
							toAST(tree.getChild(0), insertNode);

					}
				} else { // we have a record! *CONGRATS*
					Record record = new Record();

					// test if we have an array of this record
					if (tree.getChild(4).getChildrenCount() != 0) {
						Type array = new Array();
						toAST(tree.getChild(2), record); // <-- decls!!!

						array.addAttribute(lattribute);
						array.setAttribute(lattribute, record);

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
					array.addAttribute(lattribute);
					array.setAttribute(lattribute,
							insertNode.getAttribute(lattribute));
					toAST(tree.getChild(3), array);
					insertNode.addChild(array);
				} else // array declaration stopped here...
				{
					insertNode.addChild((ISyntaxTree) insertNode
							.getAttribute(lattribute));
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
			case stmt:
				Statement stmt = null;
				ISyntaxTree firstChild = tree.getChild(0);
				if (firstChild.getSymbol().isTerminal()) {
					if (firstChild.getSymbol().asTerminal() == TokenType.IF) {
						ISyntaxTree stmt_ = tree.getChild(5); //
						if (stmt_.getChildrenCount() == 0)
							stmt = new If();
						else
							stmt = new IfElse();
					} else if (firstChild.getSymbol().asTerminal() == TokenType.WHILE) {
						stmt = new While();
					} else if (firstChild.getSymbol().asTerminal() == TokenType.DO) {
						stmt = new Do();
					} else if (firstChild.getSymbol().asTerminal() == TokenType.BREAK) {
						stmt = new Break();
					} else if (firstChild.getSymbol().asTerminal() == TokenType.RETURN) {
						stmt = new Return();
					} else if (firstChild.getSymbol().asTerminal() == TokenType.PRINT) {
						stmt = new Print();
					}
				} else if (firstChild.getSymbol().isNonTerminal()) {
					toAST(firstChild, insertNode);
				}

				if (stmt != null) {
					for (int i = 0; i < tree.getChildrenCount(); i++) {
						toAST(tree.getChild(i), stmt);
					}
					insertNode.addChild(stmt);
				} else {
					// throw new SemanticException(
					// "stmt could'nt be set to any node in semantic analyzer");
				}
				return;
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
					BinaryOp bOp = new BinaryOp(tree.getChild(1).getChild(0)
							.getSymbol().asTerminal());

					if (bOp != null) {
						// simply hang in both children trees
						toAST(tree.getChild(0), bOp); // rel
						toAST(tree.getChild(1), bOp); // equality'
						insertNode.addChild(bOp);
					}
				}
				return;
			case unary:
				if (tree.getChild(0).getSymbol().isNonTerminal()) {
					toAST(tree.getChild(0), insertNode);
				} else {
					UnaryOp uOp = new UnaryOp(tree.getChild(0).getSymbol()
							.asTerminal());

					if (uOp != null) {
						// simply hang in both children trees
						toAST(tree.getChild(1), uOp); // unary
						insertNode.addChild(uOp);
					}
				}
				return;
			case factor:
				if (tree.getChild(0).getSymbol().isNonTerminal()) {
					if (tree.getChild(1).getChildrenCount() == 0) {
						toAST(tree.getChild(0), insertNode);
					} else {
						FuncCall call = new FuncCall();
						for (ISyntaxTree tmp : tree.getChildren()) {
							toAST(tmp, call);
						}
						insertNode.addChild(call);
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
					tree.getChild(1).addAttribute(lattribute);
					// there is only one child (the id itself)!
					tree.getChild(1).setAttribute(lattribute, tmp.getChild(0));
					toAST(tree.getChild(1), insertNode);
				}
				return;
			case loc__:
				tree.getChild(0).addAttribute(lattribute);
				tree.getChild(0).setAttribute(lattribute,
						tree.getAttribute(lattribute));
				if (tree.getChild(1).getChildrenCount() == 0) {
					toAST(tree.getChild(0), insertNode);
				} else {
					ISyntaxTree tmp = new Program();
					toAST(tree.getChild(0), tmp);
					if (tmp.getChildrenCount() == 1) {
						tree.getChild(1).addAttribute(lattribute);
						tree.getChild(1).setAttribute(lattribute,
								tmp.getChild(0));
						toAST(tree.getChild(1), insertNode);
					}
				}
				return;
			case loc_:
				// TODO: not everything is well thought atm...
				if (tree.getChild(0).getSymbol().asTerminal() == TokenType.OP_DOT) {
					RecordVarCall varCall = new RecordVarCall();
					varCall.addChild((ISyntaxTree) tree
							.getAttribute(lattribute));

					toAST(tree.getChild(1), varCall);
					insertNode.addChild(varCall);
				} else {
					ArrayCall array = new ArrayCall();
					toAST(tree.getChild(1), array);
					array.addChild((ISyntaxTree) tree.getAttribute(lattribute));
					insertNode.addChild(array);
				}
				return;

				// list of nodes which use the default case:
				// assign_, basic, bool_, decls, equality_, expr_, factor_,
				// func_, join_,
				// params, params_, rel_, stmt_, stmt__, stmts,
			default:
				// nothing to do here just pass it through
				for (ISyntaxTree tmp : tree.getChildren()) {
					toAST(tmp, insertNode);
				}
				return;
			}

		} else if (tree.getSymbol().isTerminal()) {
			TokenType t = tree.getSymbol().asTerminal();
			switch (t) {
			case BOOL_TYPE:
				insertNode.addChild(new BasicType(TokenType.BOOL_TYPE));
				return;
			case INT_TYPE:
				insertNode.addChild(new BasicType(TokenType.INT_TYPE));
				return;
			case REAL_TYPE:
				insertNode.addChild(new BasicType(TokenType.REAL_TYPE));
				return;
			case STRING_TYPE:
				insertNode.addChild(new BasicType(TokenType.STRING_TYPE));
				return;
			case INT_LITERAL:
				insertNode.addChild(new IntLiteral((Integer) tree
						.getAttribute(DefaultAttribute.TokenValue.name())));
				return;
			case STRING_LITERAL:
				insertNode.addChild(new StringLiteral((String) tree
						.getAttribute(DefaultAttribute.TokenValue.name())));
				return;
			case REAL_LITERAL:
				insertNode.addChild(new RealLiteral((Double) tree
						.getAttribute(DefaultAttribute.TokenValue.name())));
				return;
			case BOOL_LITERAL:
				insertNode.addChild(new BoolLiteral((Boolean) tree
						.getAttribute(DefaultAttribute.TokenValue.name())));
				return;
			case ID:
				insertNode.addChild(new Id((String) tree
						.getAttribute(DefaultAttribute.TokenValue.name())));
				return;
			default:// everything, which has no class member in its node uses
					// the default.
				return;
			}
		} else if (tree.getSymbol().isReservedTerminal()) {
			Reserved res = tree.getSymbol().asReservedTerminal();
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
}

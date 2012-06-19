package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.SymbolTableHelper;
import de.fuberlin.projecta.parser.ISyntaxTree;

public class IfElse extends Statement {

	private Block block;

	@Override
	public boolean checkSemantics() {
		// semantics of if-else-statement is unambiguous
		// only need to check children
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (!((AbstractSyntaxTree) this.getChild(i)).checkSemantics()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String genCode() {
		String ret = "";
		block = getHighestBlock();
		if (block != null) {
			int[] regs = new int[6];
			// for (int i = 0; i < 4; i++) {
			// regs[i] = block.getNewRegister();
			// }
			int nots = 0;
			AbstractSyntaxTree newTree = (AbstractSyntaxTree) getChild(0);
			while (newTree instanceof UnaryOp) {
				if (((UnaryOp) newTree).getOp() == TokenType.OP_NOT) {
					nots++;
				}
				newTree = (AbstractSyntaxTree) newTree.getChild(0);
			}

			SymbolTableHelper helper = new SymbolTableHelper();
			// load value of id1 if it is an id!!!
			if (((BinaryOp) getChild(0)).getChild(0) instanceof Id) {
				regs[4] = block.getNewRegister();
				Id id = (Id) ((BinaryOp) getChild(0)).getChild(0);
				ret += "%"
						+ regs[4]
						+ " = load "
						+ (helper.lookup(id.getValue(), this)).getType()
								.genCode() + "* %" + id.getValue() + "\n";
			}
			// load value of id2 if it is an id!!!
			if (((BinaryOp) getChild(0)).getChild(1) instanceof Id) {
				Id id = (Id) ((BinaryOp) getChild(0)).getChild(1);
				regs[5] = block.getNewRegister();
				ret += "%"
						+ regs[5]
						+ " = load "
						+ (helper.lookup(id.getValue(), this)).getType()
								.genCode() + "* %" + id.getValue() + "\n";
			}
			// create new register for comparison
			regs[0] = block.getNewRegister();
			ret += "%" + regs[0] + " = "
					+ ((AbstractSyntaxTree) getChild(0)).genCode() + "\n";
			String block1, block2;
			// count nots 
			if (nots % 2 == 0) {
				regs[1] = block.getNewRegister();
				ret += "br i1 %" + regs[0] + ", label %" + regs[1];
				block1 = ((Statement) getChild(1)).genCode();
				regs[2] = block.getNewRegister();
				ret += ", label %" + regs[2] + "\n";
			} else {
				regs[1] = block.getNewRegister();
				ret += "br i1 %" + regs[0] + ", label %" + regs[2];
				block1 = ((Statement) getChild(1)).genCode();
				regs[2] = block.getNewRegister();
				ret += ", label %" + regs[1] + "\n";
			}
			ret += "; <label>:" + regs[1] + "\n";
			ret += block1 + "\n";
			block2 = ((Statement) getChild(2)).genCode();
			regs[3] = block.getNewRegister();
			ret += "br label %" + regs[3] + "\n";
			ret += "; <label>:" + regs[2] + "\n";
			ret += block2 + "\n";
			ret += "br label %" + regs[3] + "\n";
			ret += "; <label>:" + regs[3];
		}

		return ret;
	}

	protected boolean hasReturnStatement() {
		return ifBranchHasReturnStatement() && elseBranchHasReturnStatement();
	}

	protected boolean couldAmmendReturnStatement() {
		boolean ifWentWell = true;
		if (!ifBranchHasReturnStatement())
			ifWentWell = ammendReturnOnIf();
		if (!elseBranchHasReturnStatement()) {
			ISyntaxTree elseBranch = this.getChild(2);
			if (elseBranch instanceof Block) {
				System.out.println("It is a block! Hooray!");
				return ((Block) elseBranch).couldAmmendReturnStatement();
			} else if (elseBranch instanceof Do) {
				return ((Do) elseBranch).couldAmmendReturnStatement();
			} else if (elseBranch instanceof IfElse) {
				return ((IfElse) elseBranch).couldAmmendReturnStatement();
			} else if (elseBranch instanceof BinaryOp) {
				BinaryOp binOp = (BinaryOp) elseBranch;
				if (binOp.getOp() == TokenType.OP_ASSIGN) {
					// first child has to be an identifier. This is checked
					// beforehand!
					this.removeChild(this.getChildrenCount() - 1);
					Return r = new Return();
					r.addChild(binOp.getChild(0));
					Block block = new Block();
					block.addChild(binOp);
					block.addChild(r);
					this.addChild(block);
					return true;
				} // it is an operation. A return statement will be created with
					// this operation
			} else if (elseBranch instanceof Break
					|| elseBranch instanceof Print || elseBranch instanceof If
					|| elseBranch instanceof While) {
				return false;
			} else {
				Return r = new Return();
				r.addChild(elseBranch);
				this.removeChild(this.getChildrenCount() - 1);
				this.addChild(r);
			}
		}
		// For we know at this point that else went well :-)
		return ifWentWell;
	}

	private boolean ammendReturnOnIf() {
		ISyntaxTree ifBranch = this.getChild(1);
		if (ifBranch instanceof Block) {
			return ((Block) ifBranch).couldAmmendReturnStatement();
		} else if (ifBranch instanceof Do) {
			return ((Do) ifBranch).couldAmmendReturnStatement();
		} else if (ifBranch instanceof IfElse) {
			return ((IfElse) ifBranch).couldAmmendReturnStatement();
		} else if (ifBranch instanceof BinaryOp) {
			BinaryOp binOp = (BinaryOp) ifBranch;
			if (binOp.getOp() == TokenType.OP_ASSIGN) {
				// first child has to be an identifier. This is checked
				// beforehand!
				ISyntaxTree elseBranch = this.removeChild(2);
				this.removeChild(1);
				Block block = new Block();
				block.addChild(binOp);
				Return r = new Return();
				r.addChild(binOp.getChild(0));
				block.addChild(r);
				this.addChild(block);
				this.addChild(elseBranch);
				return true;
			} // it is an operation. A return statement will be created with
				// this operation
		} else if (ifBranch instanceof Break || ifBranch instanceof Print
				|| ifBranch instanceof If || ifBranch instanceof While) {
			return false;
		}
		Return r = new Return();
		r.addChild(ifBranch);
		this.removeChild(this.getChildrenCount() - 1);
		return true;
	}

	private boolean ifBranchHasReturnStatement() {
		ISyntaxTree ifBranch = this.getChild(1);
		if (ifBranch instanceof Block) {
			return ((Block) ifBranch).hasReturnStatement();
		} else if (ifBranch instanceof Do) {
			return ((Do) ifBranch).hasReturnStatement();
		} else if (ifBranch instanceof IfElse) {
			return ((IfElse) ifBranch).hasReturnStatement();
		} else if (ifBranch instanceof Return) {
			return true;
		}
		return false;
	}

	private boolean elseBranchHasReturnStatement() {
		ISyntaxTree elseBranch = this.getChild(2);
		if (elseBranch instanceof Block) {
			return ((Block) elseBranch).hasReturnStatement();
		} else if (elseBranch instanceof Do) {
			return ((Do) elseBranch).hasReturnStatement();
		} else if (elseBranch instanceof IfElse) {
			return ((IfElse) elseBranch).hasReturnStatement();
		} else if (elseBranch instanceof Return) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}

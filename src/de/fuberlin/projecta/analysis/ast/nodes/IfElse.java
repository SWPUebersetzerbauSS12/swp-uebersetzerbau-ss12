package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.codegen.LLVM;

public class IfElse extends Statement {

	private Block block;

	@Override
	public String genCode() {
		String ret = "";
		block = getHighestBlock();
		if (block != null) {
			AbstractSyntaxTree uOp = (AbstractSyntaxTree) getChild(0);
			boolean not = false;
			while (uOp instanceof UnaryOp) {
				if (((UnaryOp) uOp).getOp() == TokenType.OP_NOT) {
					not = !not;
				}
				uOp = (AbstractSyntaxTree) uOp.getChild(0);
			}
			int label = block.getNewVar();
			this.setBeginLabel(label);
			ret += "br label %" + label + "\n\n";
			ret += "; <label> %" + label + "\n";
			ret += ((AbstractSyntaxTree) getChild(0)).genCode();

			ret += LLVM.genBranch(this, ((AbstractSyntaxTree) getChild(1)),
					((AbstractSyntaxTree) getChild(2)), not, false);
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
		// check children and we are good.
		for (ISyntaxTree child : this.getChildren()) {
			if (!((AbstractSyntaxTree) child).checkTypes()) {
				return false;
			}
		}
		return true;
	}
}

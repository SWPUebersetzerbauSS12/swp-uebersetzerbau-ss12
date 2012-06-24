package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;

public class If extends Statement {
	private Block block;

	@Override
	public boolean checkSemantics() {
		// semantics of if-statement is unambiguous
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
			int[] regs = new int[3];
			int nots = 0;
			AbstractSyntaxTree newTree = (AbstractSyntaxTree) getChild(0);
			while (newTree instanceof UnaryOp) {
				if (((UnaryOp) newTree).getOp() == TokenType.OP_NOT) {
					nots++;
				}
				newTree = (AbstractSyntaxTree) newTree.getChild(0);
			}
			
			ret += ((AbstractSyntaxTree)getChild(0)).genCode();
			String block1 = "";
			// count !'s (nots)
			regs[0] = block.getCurrentRegister();
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
			ret += "; <label>:" + regs[1]+ "\n";
			ret += block1 + "\n";
			ret += "br label %" + regs[2];
			ret += "; <label>:" + regs[2];
		}

		return ret;
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

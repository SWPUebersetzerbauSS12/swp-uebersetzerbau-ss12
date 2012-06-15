package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.projecta.lexer.TokenType;

public class IfElse extends Statement {

	private Block block;

	public void buildSymbolTable(SymbolTableStack tables) {

	}

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
			int[] regs = new int[4];
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
			regs[0] = block.getNewRegister();
			ret += "%" + regs[0] + " = "
					+ ((AbstractSyntaxTree) getChild(0)).genCode() + "\n";
			String block1, block2;
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
}

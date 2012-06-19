package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.SymbolTableHelper;

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
			int[] regs = new int[5];
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
				Id id = (Id) ((BinaryOp) getChild(0)).getChild(0);
				regs[3] = block.getNewRegister();
				ret += "%"
						+ regs[3]
						+ " = load "
						+ (helper.lookup(id.getValue(), this)).getType()
								.genCode() + "* %" + id.getValue() + "\n";
			}
			// load value of id2 if it is an id!!!
			if (((BinaryOp) getChild(0)).getChild(1) instanceof Id) {
				Id id = (Id) ((BinaryOp) getChild(0)).getChild(1);
				regs[4] = block.getNewRegister();
				ret += "%"
						+ regs[4]
						+ " = load "
						+ (helper.lookup(id.getValue(), this)).getType()
								.genCode() + "* %" + id.getValue() + "\n";
			}
			// new register for comparison
			regs[0] = block.getNewRegister();
			ret += "%" + regs[0] + " = "
					+ ((AbstractSyntaxTree) getChild(0)).genCode() + "\n";
			String block1 = "";
			// count !'s
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
			ret += "; <label>:" + regs[2];
		}

		return ret;
	}

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}

}

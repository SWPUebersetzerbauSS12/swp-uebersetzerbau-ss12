package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.codegen.LLVM;

public class If extends Statement {

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
			
			if(!(getChild(0) instanceof Id)){
				ret += ((AbstractSyntaxTree) getChild(0)).genCode();
				ret += LLVM.genBranch(this, ((AbstractSyntaxTree) getChild(1)),null, not, false);
			} else {
				Id id = (Id) getChild(0);
				int tmp1 = block.getNewVar();
				ret += "%" + tmp1 + " = load i1* %" + id.getValue() + "\n";
				int tmp2 = block.getNewVar();
				ret += "%" + tmp2 + " = icmp ne i1 %" + tmp1 + ", 0\n";
				ret += ret += LLVM.genBranch(this, ((AbstractSyntaxTree) getChild(1)),null, not, false);
			}			
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

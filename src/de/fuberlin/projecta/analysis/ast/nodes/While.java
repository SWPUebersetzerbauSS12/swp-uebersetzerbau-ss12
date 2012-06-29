package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.codegen.LLVM;



public class While extends Statement {

	private Block block;
	
	@Override
	public boolean checkSemantics() {
		for(int i = 0; i < this.getChildrenCount(); i++){
			if(!((AbstractSyntaxTree)getChild(i)).checkSemantics()){
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
			AbstractSyntaxTree uOp = (AbstractSyntaxTree) getChild(0);
			boolean not = false;
			while (uOp instanceof UnaryOp) {
				if (((UnaryOp) uOp).getOp() == TokenType.OP_NOT) {
					not = !not;
				}
				uOp = (AbstractSyntaxTree) uOp.getChild(0);
			}
			int label = block.getNewMemory();
			this.setBeginLabel(label);
			ret += "br label %" + label + "\n\n";
			ret += "; <label> %" + label + "\n";
			ret += ((AbstractSyntaxTree) getChild(0)).genCode();

			ret += LLVM.genBranch(this, ((AbstractSyntaxTree) getChild(1)),null, not, true);
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

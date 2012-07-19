package de.fuberlin.projecta.analysis.ast;

import java.util.List;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.codegen.LLVM;

/**
 * root node of do-while-loop
 *
 */
public class Do extends Statement {

	private Block block;
	
	protected boolean hasReturnStatement() {
		return false;
	}
	

	protected boolean couldAmmendReturnStatement() {
		ISyntaxTree doBody = this.getChild(0);
		if (doBody instanceof Block) {
			return ((Block) doBody).couldAmmendReturnStatement();
		} else if (doBody instanceof Do) {
			return ((Do) doBody).couldAmmendReturnStatement();
		} else if (doBody instanceof IfElse) {
			return ((IfElse) doBody).couldAmmendReturnStatement();
		} else if (doBody instanceof BinaryOp) {
			BinaryOp binOp = (BinaryOp) doBody;
			if (binOp.getOp() == TokenType.OP_ASSIGN) {
				// first child has to be an identifier. This is checked beforehand!
				List<ISyntaxTree> children = this.getChildren();
				children.remove(0);
				Block block = new Block();
				block.addChild(binOp);
				Return r = new Return();
				r.addChild(binOp.getChild(0));
				block.addChild(r);
				for(ISyntaxTree tree : children){
					block.addChild(tree);
				}
				this.addChild(block);
				
				return true;
			} // it is an operation. A return statement will be created with this operation 
		} else if (doBody instanceof Break || doBody instanceof Print || doBody instanceof If || doBody instanceof While){
			return false;
		}
		
		Return r = new Return();
		r.addChild(doBody);
		this.removeChild(this.getChildrenCount() - 1);
		return true;
	}
	
	public String genCode(){
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
			ret += ((AbstractSyntaxTree)getChild(1)).genCode();
			ret += LLVM.genBranch(this, ((AbstractSyntaxTree) getChild(1)),null, not, true);
		}
		return ret;
	}

}

package de.fuberlin.projecta.analysis.ast.nodes;

import java.util.List;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.projecta.parser.ISyntaxTree;

/**
 * root node of do-while-loop
 *
 */
public class Do extends Statement {

	@Override
	public boolean checkSemantics() {
		for(int i = 0; i < this.getChildrenCount(); i++){
			if(!((AbstractSyntaxTree)this.getChild(i)).checkSemantics()){
				return false;
			}
		}
		return true;
	}

	@Override
	public String genCode() {
		// TODO Auto-generated method stub
		return null;
	}

	protected boolean hasReturnStatement() {
//		if (this.getChild(0) instanceof ControlStructure) {
//			ControlStructure cs = (ControlStructure) this.getChild(0);
//			return cs.hasReturnStatement();
//		}
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

	@Override
	public boolean checkTypes() {
		// TODO Auto-generated method stub
		return false;
	}
}

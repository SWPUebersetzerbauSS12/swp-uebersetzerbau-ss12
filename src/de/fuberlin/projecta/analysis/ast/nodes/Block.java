package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.SymbolTableStack;


public class Block extends Statement {
	/**
	 * Used for naming conventions in declarations
	 */
	private int registerCounter;
	
	@Override
	public void buildSymbolTable(SymbolTableStack stack){
		stack.push();
		for(int i = 0; i < this.getChildrenCount(); i++){
			((AbstractSyntaxTree)(this.getChild(i))).buildSymbolTable(stack);
		}
		table = stack.pop();
	}

	@Override
	public boolean checkSemantics() {
		for(int i = 0; i < this.getChildrenCount(); i++){
			if(!((AbstractSyntaxTree)this.getChild(i)).checkSemantics()){
				return false;
			}
		}
		return true;
	}


	// using super implementation for genCode
	
	public int getNewRegister(){
		return ++registerCounter;
	}
	
	public int getCurrentRegister(){
		return registerCounter;
	}

	protected boolean hasReturnStatement(){
		if(this.getChildrenCount() > 0){
			ISyntaxTree last = this.getChild(this.getChildrenCount() - 1);
			if(last instanceof Block){
				return ((Block) last).hasReturnStatement();
			}else{
				return last instanceof Return;
			}
		}
		return false;
	}

	protected boolean couldAmmendReturnStatement() {
		// Blocks don't exist if they are empty!
		ISyntaxTree lastStatement = this.getChild(this.getChildrenCount() - 1);
		if (lastStatement instanceof Block) {
			return ((Block) lastStatement).couldAmmendReturnStatement();
		} else if (lastStatement instanceof Do ) {
			return ((Do) lastStatement).couldAmmendReturnStatement();
		} else if (lastStatement instanceof IfElse ) {
			return ((IfElse) lastStatement).couldAmmendReturnStatement();
		} else if (lastStatement instanceof BinaryOp) {
			BinaryOp binOp = (BinaryOp) lastStatement;
			if (binOp.getOp() == TokenType.OP_ASSIGN) {
				// first child has to be an identifier. This is checked beforehand!
				Return r = new Return();
				r.addChild(binOp.getChild(0));
				this.addChild(r);
				return true;
			} // it is an operation. A return statement will be created with this operation 
		} else if (lastStatement instanceof Break || lastStatement instanceof Print || lastStatement instanceof If || lastStatement instanceof While){
			return false;
		}
		
		Return r = new Return();
		r.addChild(lastStatement);
		this.removeChild(this.getChildrenCount() - 1);
		this.addChild(r);
		return true;
		
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

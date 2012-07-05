package de.fuberlin.projecta.analysis.ast.nodes;

import java.util.ArrayList;
import java.util.List;

import de.fuberlin.commons.lexer.TokenType;
import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.projecta.lexer.BasicTokenType;
import de.fuberlin.commons.parser.ISyntaxTree;

public class FuncDef extends Type {

	@Override
	public boolean checkSemantics() {
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (!((AbstractSyntaxTree) this.getChild(i)).checkSemantics()) {
				return false;
			}
			// if not, we have an empty method. The semantics of empty methods
			// is defined as correct
			if (this.getChild(this.getChildrenCount() - 1) instanceof Block) {
				Block block = (Block) this
						.getChild(this.getChildrenCount() - 1);
				if (!(block.getChild(block.getChildrenCount() - 1) instanceof Return)) {
					if (((BasicType) this.getChild(0)).getTokenType() != BasicTokenType.VOID) {
						if(!canInsertReturn(block)){
							throw new SemanticException("Methods needs to return a value");
						}
					} else {
						// no return type. so just add empty return
						Return r = new Return();
						block.addChild(r);
					}
				}
			} else if (((BasicType) this.getChild(0)).getTokenType() != BasicTokenType.VOID) {
				// we do not have any statements but a return type => BAD!!!
				throw new SemanticException("Methods needs to return a value");
			}
		}
		return true;
	}

	@Override
	public void buildSymbolTable(SymbolTableStack stack) {
		Type type = (Type) getChild(0);
		Id id = (Id) getChild(1);

		stack.push();
		// these are parameters
		((AbstractSyntaxTree) getChild(2)).buildSymbolTable(stack);

		List<EntryType> parameters = stack.pop().getEntries();

		if (this.getChildrenCount() == 4) {
			// this is the block, it can
			// handle everything itself
			((AbstractSyntaxTree) getChild(3)).buildSymbolTable(stack);
			for (EntryType entry : parameters) {
				((AbstractSyntaxTree) getChild(3)).getTable()
						.insertEntry(entry);
			}
		}
		EntryType entry = new EntryType(id, type, parameters);
		stack.top().insertEntry(entry);

	}

	@Override
	public String genCode() {
		String blockCode = "";
		if (getChildrenCount() > 3)
			blockCode = ((Block)getChild(3)).genCode();
		return "define " + ((Type) getChild(0)).genCode() + " @"
				+ ((Id) getChild(1)).genCode() + "("
				+ ((Params) getChild(2)).genCode() + ") nounwind { \n"
				+ blockCode + "}";
	}

	/**
	 * Nodes that should produce an error: break, print Nodes that cannot pop up
	 * here: BasicType, Declaration, Params, Program, FuncDef, Record, (Return)
	 *
	 * @param methodBlock
	 * @return
	 */
	private boolean canInsertReturn(AbstractSyntaxTree methodBlock) {
		// We don't have return statement, so just insert one
		// which one depends on the last statement...

		AbstractSyntaxTree lastStatement = (AbstractSyntaxTree) methodBlock
				.getChild(methodBlock.getChildrenCount() - 1);

		ArrayList<Class<? extends Statement>> showStoppers = new ArrayList<Class<? extends Statement>>();
		showStoppers.add(Break.class);
		showStoppers.add(Print.class);
		showStoppers.add(If.class); // It doesn't matter whether 'If'-block has return statement or not. Condition could be 'false' -> no return can be implied 
		showStoppers.add(While.class); //see above
		if (showStoppers.contains(lastStatement.getClass())) {
			// or throw SemanticException...
			return false;
		}

		// We have to go through for possible return statements
		if (lastStatement instanceof Block) {
			Block block = (Block) lastStatement;
			if (!block.hasReturnStatement()) {
				return block.couldAmmendReturnStatement();
			} else {
				return true;
			}
		}

		if (lastStatement instanceof Do) {
			Do doLoop = (Do) lastStatement;
			if (!doLoop.hasReturnStatement()) {
				return doLoop.couldAmmendReturnStatement();
			} else {
				return true;
			}
		}

		if (lastStatement instanceof IfElse) {
			IfElse ifElse = (IfElse) lastStatement;
			if (!ifElse.hasReturnStatement()) {
				return ifElse.couldAmmendReturnStatement();
			} else {
				return true;
			}
		}

		if (lastStatement instanceof BinaryOp) {
			BinaryOp binOp = (BinaryOp) lastStatement;
			if (binOp.getOp() == TokenType.OP_ASSIGN) {
				// first child has to be an identifier. This is checked beforehand!
				Return r = new Return();
				r.addChild(binOp.getChild(0));
				methodBlock.addChild(r);
				return true;
			} // it is an operation. A return statement will be created with this operation 
		}
		// last statement is no control structure nor is it a show stopper =>
		// just hang last statement node under a new return node
		Return r = new Return();
		methodBlock.removeChild(methodBlock.getChildrenCount() - 1);
		r.addChild(lastStatement);
		methodBlock.addChild(r);

		return false;

	}

	@Override
	public boolean checkTypes() {
		// check all children and we are good
		for(ISyntaxTree child : this.getChildren()){
			if(!((AbstractSyntaxTree)child).checkTypes()){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toTypeString(){
		return ((Type)getChild(0)).toTypeString();
	}
}

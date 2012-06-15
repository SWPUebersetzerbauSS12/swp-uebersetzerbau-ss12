package de.fuberlin.projecta.analysis.ast.nodes;

import de.fuberlin.projecta.analysis.EntryType;
import de.fuberlin.projecta.analysis.SymbolTable;
import de.fuberlin.projecta.analysis.SymbolTableStack;
import de.fuberlin.projecta.lexer.BasicTokenType;

public class FuncDef extends AbstractSyntaxTree {

	@Override
	public boolean checkSemantics() {
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (!((AbstractSyntaxTree) this.getChild(i)).checkSemantics()) {
				return false;
			}
			// Last child of function definition is always a block
			int last = this.getChildrenCount() - 1;
			Block block = (Block) this.getChild(last);
			int blockChildrenCount = block.getChildrenCount();
			if (blockChildrenCount > 0) {
				AbstractSyntaxTree lastStatement = (AbstractSyntaxTree) block.getChild(blockChildrenCount - 1);
				if (!(lastStatement instanceof Return)) {
					// We don't have return statement, so just insert one :-)
					Return r = new Return();

					if (((BasicType) this.getChild(0)).getType() != BasicTokenType.VOID){
						//TODO: Do this only if last statement is no loop
						//TODO: If loop, look for return in loop...
						block.removeChild(blockChildrenCount - 1);
						r.addChild(lastStatement);
			        }
			        block.addChild(r);
			     }
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

		SymbolTable tmp = stack.pop();
		EntryType entry = new EntryType(id, type, tmp.getEntries());
		stack.top().insertEntry(entry);

		// TODO: musn't the parameters be also stored in the block
		// symbolTable???
		if (this.getChildrenCount() == 4)
			((AbstractSyntaxTree) getChild(3)).buildSymbolTable(stack); // this
																		// is
																		// the
																		// block,
																		// it
																		// can
		// handle everything itself

	}

	@Override
	public String genCode() {
		String ret = "define " + ((Type) getChild(0)).genCode() + " @"
				+ ((Id) getChild(1)).genCode() + "("
				+ ((Params) getChild(2)).genCode() + ") nounwind uwtable { \n"
				+ ((Block) getChild(3)).genCode() + "}";
		return ret;
	}
}

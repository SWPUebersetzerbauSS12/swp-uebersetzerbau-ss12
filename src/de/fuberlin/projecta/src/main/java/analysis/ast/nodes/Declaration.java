package analysis.ast.nodes;

import lexer.BasicTokenType;
import analysis.SymbolTableStack;

public class Declaration extends AbstractSyntaxTree {
	@Override
	public void buildSymbolTable(SymbolTableStack tables) {
		tables.top().insertEntry((Id) getChild(1), (Type) getChild(0));
	}

	@Override
	public boolean checkSemantics() {
		for (int i = 0; i < this.getChildrenCount(); i++) {
			if (this.getChild(i) instanceof BasicType
					&& ((BasicType) this.getChild(i)).getType() == BasicTokenType.VOID) {
				return false;
			}
			if (!((AbstractSyntaxTree) this.getChild(i)).checkSemantics()) {
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
}

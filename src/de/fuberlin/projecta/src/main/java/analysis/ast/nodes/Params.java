package analysis.ast.nodes;

import analysis.EntryType;
import analysis.SymbolTableStack;

public class Params extends AbstractSyntaxTree {
	@Override
	public void buildSymbolTable(SymbolTableStack tables) {
		for (int i = 0; i < getChildrenCount(); i += 2) {
			Type type = (Type) getChild(i);
			Id id = (Id) getChild(i + 1);
			EntryType entry = new EntryType(id, type);
			tables.top().insertEntry(entry);
		}
	}

	@Override
	public boolean checkSemantics() {
		// check for identifier that are used more than once!
		for(int i = 0; i < this.getChildrenCount(); i++){
			if(!((AbstractSyntaxTree)this.getChild(i)).checkSemantics()){
				return false;
			}
		}
		return true;
	}
}

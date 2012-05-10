package parser.nodes;

import lexer.IToken.TokenType;
import semantic.analysis.SemanticException;
import semantic.analysis.SymbolTableStack;

public class decl extends Tree {

	private type typeTree;
	private ID idTree;

	public decl(String name) {
		super(name);
	}

	public void init() {
		printTree();

		typeTree = (type) getChild(0);
		TokenType type = typeTree.getType();

		idTree = (ID) getChild(1);

		// debug
		System.out.println("ID: " + idTree.getLexeme() + ", Type: " + type);
		assert (idTree.getLexeme() != null);
	}

	@Override
	public void run(SymbolTableStack tables) {
		init();

		try {
			tables.top().insertEntry(idTree.getLexeme(), typeTree.getType());
		} catch (IllegalStateException e) {
			throw new SemanticException(e.toString());
		}
	}
}

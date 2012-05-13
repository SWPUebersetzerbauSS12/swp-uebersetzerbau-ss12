package parser.AstNodes;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import parser.Symbol;
import semantic.analysis.SymbolTableStack;


public class Block extends Statement {
	@Getter
	private List<Declaration> declarations;
	@Getter
	private List<Statement> statements;

	public void addDeclaration(Declaration decl){
		declarations.add(decl);
	}

	public void addStatement(Statement stmt){
		statements.add(stmt);
	}

	public Block(Symbol symbol) {
		super(symbol);
		declarations = new ArrayList<Declaration>();
		statements = new ArrayList<Statement>();
	}

	public void run(SymbolTableStack tables) {

	}
}

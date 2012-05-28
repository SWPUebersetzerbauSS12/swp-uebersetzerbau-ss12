package analysis.ast.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import parser.Tree;
import analysis.SymbolTableStack;

@AllArgsConstructor
public class Id extends Tree {
	
	@Getter
	private String value;
	
	public void run(SymbolTableStack tables) {

	}
}

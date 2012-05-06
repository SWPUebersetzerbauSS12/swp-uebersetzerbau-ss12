package parser.nodes;

import lombok.Getter;
import semantic.analysis.SymbolTableStack;


public class ID extends expr {

	@Getter
	private String lexeme;

	public ID(String name) {
		super(name);
	}

	@Override
	public void run(SymbolTableStack tables) {
		Object value = getAttribute(DefaultAttribute.TokenValue.name()).getValue();
		this.lexeme = (String)value;
	}
}

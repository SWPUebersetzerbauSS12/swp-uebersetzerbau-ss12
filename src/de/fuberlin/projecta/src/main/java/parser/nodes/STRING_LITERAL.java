package parser.nodes;

import parser.Terminal;

public class STRING_LITERAL extends Terminal {

	public STRING_LITERAL(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

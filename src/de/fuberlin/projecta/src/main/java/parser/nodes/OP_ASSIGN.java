package parser.nodes;

import parser.Terminal;

public class OP_ASSIGN extends Terminal {

	public OP_ASSIGN(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

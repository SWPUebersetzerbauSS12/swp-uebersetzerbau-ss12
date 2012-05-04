package parser.nodes;

import parser.Terminal;

public class OP_SEMIC extends Terminal {

	public OP_SEMIC(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

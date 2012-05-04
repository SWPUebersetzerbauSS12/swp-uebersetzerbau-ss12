package parser.nodes;

import parser.Terminal;

public class EPSILON extends Terminal {

	public EPSILON(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

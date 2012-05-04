package parser.nodes;

import parser.Terminal;

public class BREAK extends Terminal {

	public BREAK(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

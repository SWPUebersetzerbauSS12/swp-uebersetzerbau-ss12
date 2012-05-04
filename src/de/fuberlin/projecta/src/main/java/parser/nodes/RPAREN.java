package parser.nodes;

import parser.Terminal;

public class RPAREN extends Terminal {

	public RPAREN(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

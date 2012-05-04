package parser.nodes;

import parser.Terminal;

public class LPAREN extends Terminal {

	public LPAREN(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

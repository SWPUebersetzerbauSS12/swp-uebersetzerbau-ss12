package parser.nodes;

import parser.Terminal;

public class DEF extends Terminal {

	public DEF(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

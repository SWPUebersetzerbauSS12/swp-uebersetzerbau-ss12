package parser.nodes;

import parser.Terminal;

public class ELSE extends Terminal {

	public ELSE(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

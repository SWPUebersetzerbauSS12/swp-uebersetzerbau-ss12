package parser.nodes;

import parser.Terminal;

public class ID extends Terminal {

	public ID(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

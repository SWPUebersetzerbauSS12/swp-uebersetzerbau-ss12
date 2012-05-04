package parser.nodes;

import parser.Terminal;

public class RECORD extends Terminal {

	public RECORD(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

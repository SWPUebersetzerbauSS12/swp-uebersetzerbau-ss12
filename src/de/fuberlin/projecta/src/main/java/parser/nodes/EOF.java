package parser.nodes;

import parser.Terminal;

public class EOF extends Terminal {

	public EOF(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

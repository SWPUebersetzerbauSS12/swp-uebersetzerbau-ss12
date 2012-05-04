package parser.nodes;

import parser.Terminal;

public class RETURN extends Terminal {

	public RETURN(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

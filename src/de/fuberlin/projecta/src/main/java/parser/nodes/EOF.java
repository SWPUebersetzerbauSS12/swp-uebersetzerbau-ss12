package parser.nodes;


public class EOF extends Terminal {

	public EOF(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

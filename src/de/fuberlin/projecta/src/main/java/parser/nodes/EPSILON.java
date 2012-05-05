package parser.nodes;


public class EPSILON extends Terminal {

	public EPSILON(String name) {
		super(name);
	}
	
	@Override
	public void run(){
		// skip
	}
}

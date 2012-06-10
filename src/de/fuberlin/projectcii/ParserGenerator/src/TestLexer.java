
public class TestLexer {

	private String[] Stream = {"id","+","id","*","id","eof"};
	private int next;
	
	public TestLexer(){
		next = 0;
	}
	
	public String getNextToken(){
		
		String Token = Stream[next];
		next++;
		return Token;
	}
}

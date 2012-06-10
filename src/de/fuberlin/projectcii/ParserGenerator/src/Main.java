import java.io.IOException;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 * @try/catch RuntimeException
	 * @author Ying Wei
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
/* System.out.println(		"<assd>".matches("<\\w+>"));
 System.out.println(		"<A>".matches("<\\w+>"));
 System.out.println(		"<>".matches("<\\w+>"));
 System.out.println(		"as<d>".matches("<\\w+>"));
 System.out.println(		"<as>d".matches("<\\w+>"));
 System.out.println(		"<Aas<d>>".matches("<\\w+>"));
 System.out.println(		"<as<d>".matches("<\\w+>"));
 System.out.println(		"<as.d>".matches("<\\w+>"));
 System.out.println(		"<a!.d>".matches("<\\w+>"));
 System.out.println(		"<:!.d>".matches("<\\w+>"));
 System.out.println(		"//////////////////////");
 
 System.out.println(		" <A>  \"test\"  \"c\" "
		 .matches("((\\s+<\\w+>)|(\\s+\"\\w+\"))*"));
 
 System.out.println(		"<A> \"test\" \"c\" | <A> \"b\" \"c\" | \"x\" \"y\" | <B> <A> \"x\" | <B> \"y\""
		 .matches("((\\s+<\\w+>\\s+)|(\\s+\"\\w+\")\\s+\\|)*((\\s+<\\w+>\\s+)|(\\s+\"\\w+\"\\s+))"));
 
 */
		try {
			LL1Parser ll1 = new LL1Parser();
			// if(parsable_LL1{
			ll1.getSyntaxTree();
			// }
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			return;
		}
	}

}

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;


public class TokenParser {
	
	private final String EPSILON = "@";
	private final String EOF = "eof";

	private TestLexer lexer;
	
	public TokenParser(){
		lexer = new TestLexer();
	}
	
	public void ParseTokenStream(Map<String, HashMap<String,Vector<Integer>>> parserTable,
								 Map<String, Vector<Vector<String>>> grammar, String StartSymbol){
	
		Stack<String> stack = new Stack<String>();
		stack.push(EOF);
		stack.push(StartSymbol);
		String Token = lexer.getNextToken();
		while (!Token.equals(EOF)){
			if (stack.peek().equals(Token)){
				stack.pop();
				Token = lexer.getNextToken();
			}
			else if(parserTable.keySet().contains(stack.peek()) &&
				   (parserTable.get(stack.peek()).keySet().contains(Token))){
				String head = stack.pop();
				int productionNr = parserTable.get(head).get(Token).firstElement();
				Vector<String> Production = grammar.get(head).elementAt(productionNr);
				for (int i=Production.size()-1;i >= 0;i--){
					if (!Production.elementAt(i).equals(EPSILON)){
						stack.push(Production.elementAt(i));
					}
				}
				Printer.printProduction(grammar, head, productionNr);
			}
			else{
				System.out.println("ERROR!");
				break;
			}
		}
		if (Token.equals(EOF)){
			System.out.println("accepted");
		}
		else{
			System.out.println("ERROR2");
		}
	}
}

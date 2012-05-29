import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

//Testkommentar
public class TokenParser {

	private TestLexer lexer;
	
	public TokenParser(){
		lexer = new TestLexer();
	}
	
	public void ParseTokenStream(Map<String, HashMap<String,Vector<Integer>>> parserTable,
								 Map<String, Vector<Vector<String>>> grammar, String StartSymbol){
	
		Stack<String> stack = new Stack<String>();
		stack.push(Settings.getEOF());
		stack.push(StartSymbol);
		String Token = lexer.getNextToken();
		while (!Token.equals(Settings.getEOF())){
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
					if (!Production.elementAt(i).equals(Settings.getEPSILON())){
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
		if (Token.equals(Settings.getEOF())){
			System.out.println("accepted");
		}
		else{
			System.out.println("ERROR2");
		}
	}
}

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class TokenParser {

	private TestLexer lexer;
	private Map<String, HashMap<String,Vector<Integer>>> parserTable;
	private Map<String, Vector<Vector<String>>> grammar;
	private String StartSymbol;
	private String Token;
	
	public TokenParser(Map<String, HashMap<String,Vector<Integer>>> parserTable,
			 Map<String, Vector<Vector<String>>> grammar, String StartSymbol){
		lexer = new TestLexer();
		this.parserTable = parserTable;
		this.grammar = grammar;
		this.StartSymbol = StartSymbol;
	}
	
//	public void ParseTokenStream(){
//	
//		Stack<String> stack = new Stack<String>();
//		stack.push(Settings.getEOF());
//		stack.push(StartSymbol);
//		String Token = lexer.getNextToken();
//		while (!Token.equals(Settings.getEOF())){
//			if (stack.peek().equals(Token)){
//				stack.pop();
//				Token = lexer.getNextToken();
//			}
//			else if(parserTable.keySet().contains(stack.peek()) &&
//				   (parserTable.get(stack.peek()).keySet().contains(Token))){
//				String head = stack.pop();
//				int productionNr = parserTable.get(head).get(Token).firstElement();
//				Vector<String> Production = grammar.get(head).elementAt(productionNr);
//				for (int i=Production.size()-1;i >= 0;i--){
//					if (!Production.elementAt(i).equals(Settings.getEPSILON())){
//						stack.push(Production.elementAt(i));
//					}
//				}
//				Printer.printProduction(grammar, head, productionNr);
//			}
//			else{
//				System.out.println("ERROR!");
//				break;
//			}
//		}
//		if (Token.equals(Settings.getEOF())){
//			System.out.println("accepted");
//		}
//		else{
//			System.out.println("ERROR2");
//		}
//	}
	
	public void parseTokenStream(){
		Token = lexer.getNextToken();
		parseToken(StartSymbol);
		if (Token.equals(Settings.getEOF())){
			System.out.println("accepted");
		}
	}
	
	private void parseToken(String symbol){
		
		if (symbol.equals(Token)){
			Token = lexer.getNextToken();
		}
		else if(parserTable.keySet().contains(symbol) &&
			   (parserTable.get(symbol).keySet().contains(Token))){
			String head = symbol;
			int productionNr = parserTable.get(head).get(Token).firstElement();
			Vector<String> Production = grammar.get(head).elementAt(productionNr);
			Printer.printProduction(grammar, head, productionNr);
			for (int i=0;i < Production.size() ;i++){
				if (!Production.elementAt(i).equals(Settings.getEPSILON())){
					parseToken(Production.elementAt(i));
				}
			}
		}
		else{
			System.out.println("ERROR!");
		}
	return;
	}
}

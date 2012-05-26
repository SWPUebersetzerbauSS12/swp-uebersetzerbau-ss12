import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

/**
 * Debugging Class to visualize the current status of the used Datastructures
 * 
 * @author Patrick Schlott
 */
public class Printer {

	
	/**
	 * Use this Function to view the global Grammar inside the grammarReader.
	 * 
	 * @author Patrick Schlott
	 */
	static public void printGrammar(Vector<Productions> grammar){	
		
		for (int i = 0; i< grammar.size() ; i++){
			Productions nonterminal = grammar.elementAt(i);
			System.out.print("Head = "+nonterminal.getHead()+" - ");
			System.out.print("Rump = ");
			int productionNr = nonterminal.productions.size();
			for (int j = 0; j < productionNr; j++){
				Vector<String> production = nonterminal.productions.elementAt(j);
				int symbolNr = production.size();
				for (int k = 0; k < symbolNr; k++){
					String symbol = production.elementAt(k);
					System.out.print(symbol+".");
				}
				System.out.print("|");
			}
			System.out.println();
		}
		System.out.println("-----------");
	}
	
	/**
	 * Use this Function to view the global Grammar inside the ParserGenerator.
	 * 
	 * @author Patrick Schlott
	 */
	
	static public void printGrammar(Map<String, Vector<Vector<String>>> grammar){	
		
		for (String currentHead : grammar.keySet()){
			System.out.print("Head = "+currentHead);
			System.out.print(" - Rump = ");
			for (Vector<String> production : grammar.get(currentHead)) {
				for (String symbol : production) {
					System.out.print(symbol+".");
				}
				System.out.print("|");
			}
			System.out.println();
		}
		System.out.println("-----------");
	}
	
	/**
	 * Use this Function to view the global Grammar inside the ParserGenerator.
	 * 
	 * @author Patrick Schlott
	 */
	
	static public void printProduction(Map<String, Vector<Vector<String>>> grammar,String key, int productionNr){	
		
		System.out.print(key+" -> ");
		
		for (String symbol : grammar.get(key).elementAt(productionNr)){
			System.out.print(symbol+" ");
		}
		System.out.println();
		System.out.println("-----------");
	}
	
	/**
	 * Use this Function to view the Parsetable inside the ParserGenerator.
	 * 
	 * @author Patrick Schlott
	 */
	
	static public void printParserTable(Vector<String> Terminals,
										Vector<String> Nonterminal,
										Map<String, HashMap<String,Vector<Integer>>> parserTable) {
		System.out.print("\t"+"|");
		for (String terminal:Terminals){
			System.out.print(terminal+"\t"+"|");
		}
		
		for (String nonTerminal:Nonterminal){
			System.out.println("");
			for (int i = 0; i<Terminals.size();i++){
				System.out.print("_________");
			}
			System.out.println("");
			System.out.print(nonTerminal+"\t"+"|");
			for (String terminal:Terminals){
				Vector<Integer> productions = (parserTable.get(nonTerminal)).get(terminal);
				if (productions.size() == 0){
					System.out.print(""+"\t"+"|");
				}
				else if (productions.size() == 1){
					System.out.print(productions.elementAt(0)+"\t"+"|");
				}
				else{
					for (int i=0;i<productions.size();i++){
						if (i == productions.size()-1){
							System.out.print(productions.elementAt(i));
						}
						else{
							System.out.print(productions.elementAt(i)+",");
						}
					}
					System.out.print(""+"\t"+"|");
				}
			}
		}
		System.out.println();
		System.out.println("-----------");
	}
	
	/**
	 * Use this Function to view the Parsetable inside the ParserGenerator.
	 * 
	 * @author Patrick Schlott
	 */
	
	static public void printParserTable(Vector<String> Terminals,
										Vector<String> Nonterminal,
										Map<String, HashMap<String,Vector<Integer>>> parserTable,
										Map<String, Vector<Vector<String>>> grammar) {
		System.out.print("\t"+"|");
		for (String terminal:Terminals){
			System.out.print(terminal+"\t"+"|");
		}
		
		for (String nonTerminal:Nonterminal){
			System.out.println("");
			for (int i = 0; i<Terminals.size();i++){
				System.out.print("_________");
			}
			System.out.println("");
			System.out.print(nonTerminal+"\t"+"|");
			for (String terminal:Terminals){
				Vector<Integer> productions = (parserTable.get(nonTerminal)).get(terminal);
				if (productions.size() == 0){
					System.out.print(""+"\t"+"|");
				}
				else if (productions.size() == 1){
					Vector<Vector<String>> x = grammar.get(nonTerminal);
					Vector<String> y = x.elementAt(productions.elementAt(0));
					System.out.print(nonTerminal+" -> ");
					
					for (String z:y){
						System.out.print(z+" ");
					}
					System.out.print("\t"+"|");
				}
				else{
					for (int i=0;i<productions.size();i++){
						if (i == productions.size()-1){
							System.out.print(productions.elementAt(i));
						}
						else{
							System.out.print(productions.elementAt(i)+",");
						}
					}
					System.out.print(""+"\t"+"|");
				}
			}
		}
		System.out.println();
		System.out.println("-----------");
	}
	
	
	/**
	 * Use this Function to view the FirstSet inside the ParserGenerator.
	 * 
	 * @author Ying Wei, Patrick Schlott
	 */
	static public void printFirstSets(Map<String, Set<String>> firstSets) {
		for (Entry<String, Set<String>> fs : firstSets.entrySet()) {
			System.out.println("First(" + fs.getKey() + ") = " + fs.getValue());
		}
		System.out.println("-----------");
	}
	
	/**
	 * Use this Function to view the FirstSet inside the ParserGenerator.
	 * 
	 * @author Ying Wei, Patrick Schlott
	 */
	static public void printFollowSets(Map<String, Set<String>> followSets) {
		for (Entry<String, Set<String>> fs : followSets.entrySet()) {
			System.out.println("Follow(" + fs.getKey() + ") = " + fs.getValue());
		}
		System.out.println("-----------");
	}
	
	/**
	 * Use this Function to view the FirstSetProductions inside the ParserGenerator.
	 * 
	 * @author Christoph Schröder, Patrick Schlott
	 */
	static public void printFirstSetsProductions(Map<String, HashMap<String,Integer>> firstSetsProductions) {
		for (Entry<String, HashMap<String,Integer>> fs : firstSetsProductions.entrySet()) {
			System.out.println("First(" + fs.getKey() + ") = " + fs.getValue().keySet());
		}
		System.out.println("-----------");
	}
}

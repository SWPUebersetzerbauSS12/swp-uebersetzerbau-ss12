import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

/* 
 * The ParserGenerator reads a Grammar and tries to convert the 
 * Grammar in a way that it can be Parsed with Lookahead 1 by
 * eliminating Leftrekursions and factorising the Productions
 * 
 * Additional the ParserGenerator creates a Parsetable from the modified
 * Grammar.
 * 
 * Author: Patrick Schlott
 * 
 */

public class ParserGenerator {
	
	//Contains the Grammar the Parsetable is created from
	private Map<String, Vector<Vector<String>>> grammarMap;
	
	public Map<String, HashMap<String,Vector<Integer>>> parserTable = 
		new HashMap<String, HashMap<String,Vector<Integer>>>();
	
	private String start;
	private Vector<String> Terminals;
	private Vector<String> Nonterminal;
	
	public Map<String, Set<String>> followSets = new HashMap<String, Set<String>>();
	public Map<String, Set<String>> firstSets = new HashMap<String, Set<String>>();
	
	public ParserGenerator(){
		Terminals = new Vector<String>();
		Nonterminal = new Vector<String>();
	}
	
	/*
	 * initializes the grammar so a Parsetable can be created from it
	 */
	public void initialize(String file){
		//Read the Grammar from file
		readGrammar(file);
		
		fillTerminalNonterminal();
		
		/*
		 * computeFirstSet
		 */
		createFirstSet();
		
		/*
		 * computeFollowSet
		 */
		createFollowSet();
		/*
		 * Create Parsetable
		 */
		
		parserTable = createParserTable();
	}
	

	/*
	 * Reads the Grammar from a given file
	 * 
	 * Input: String file - Path to the file
	 * Return: Void
	 */
	private void readGrammar(String file){
		GrammarReader gR = new GrammarReader();
		grammarMap = gR.createGrammar(file);
		
		start = gR.getStartSymbol();
	}
	
	

	private void fillTerminalNonterminal(){
		
		for (String nonTerminal:grammarMap.keySet()){
			Nonterminal.add(nonTerminal);
		}
		for (String nonTerminal:grammarMap.keySet()){
			for (Vector<String> production:grammarMap.get(nonTerminal)){
				for (String symbol:production){
					if (!Nonterminal.contains(symbol) && !Terminals.contains(symbol)){
						Terminals.add(symbol);
					}
				}
			}
		}
		
	}
	
	

	private void createFirstSet() {

		for (String head : Nonterminal) {
			// String head = p.getHead();
			firstSets.put(head, evalFirstSet(head, grammarMap));
		}
		showFirstSets();
		
	}
	
	private Set<String> evalFirstSet(String head,
			Map<String, Vector<Vector<String>>> grammarMap) {
		if (firstSets.containsKey(head)) {
			return firstSets.get(head);
		}
		Set<String> fs = new HashSet<String>();
		for (Vector<String> production : grammarMap.get(head)) {
			String term = production.get(0);
			if (Terminals.contains(term)) {
				fs.add(term);
			} else {
				fs.addAll(evalFirstSet(term, grammarMap));
			}
		}
		return fs;
	}
	

	private void createFollowSet() {
		for (String head : Nonterminal) {
			// String head = p.getHead();
			followSets.put(head, evalFollowSet(head));
		}
		showFollowSets(); 
	}
	
	private Set<String> evalFollowSet(String head) {
		if (followSets.containsKey(head)) {
			return followSets.get(head);
		}
		Set<String> fs = new HashSet<String>();
		if (start.equals(head)) {
			fs.add("$");
		}
		
		for (String currentHead : grammarMap.keySet()) {
			for (Vector<String> product : grammarMap.get(currentHead)) {
				for (Iterator<String> itr = product.iterator(); itr.hasNext();) {
					if (itr.next().equals(head)) {
						if (itr.hasNext()) {
							// not last symbol
							String follow = itr.next();
							if (Terminals.contains(follow)){
								fs.add(follow);
							}
							else{
								HashSet<String> first = new HashSet<String>(firstSets.get(follow));

								if (first.contains("@")){
									if (!currentHead.equals(head)) { fs.addAll(evalFollowSet(currentHead));}
									first.remove("@");
									fs.addAll(first);
								} else {
									fs.addAll(first);
								}
							}
						}
						else{
							if (!currentHead.equals(head)){
								//  the last symbol
								fs.addAll(evalFollowSet(currentHead));
							}
						}						
						break;
					}
				}
			}
		}
		return fs;
	}
	
	
	/**
	 * print out all the First Set of nonterminal symbols
	 * 
	 * @author Ying Wei
	 */
	private void showFirstSets() {
		System.out.println("the First Set as follow: ");
		for (Entry<String, Set<String>> fs : firstSets.entrySet()) {
			System.out.println("First(" + fs.getKey() + ") = " + fs.getValue());
		}
	}

	private void showFollowSets() {
		System.out.println("the Follow Set as follow: ");
		for (Entry<String, Set<String>> fs : followSets.entrySet()) {
			System.out.println("Follow(" + fs.getKey() + ") = " + fs.getValue());
		}
	}
	
	private HashMap<String, HashMap<String,Vector<Integer>>> createParserTable(){
		
		HashMap<String, HashMap<String,Vector<Integer>>> ret = 
				new HashMap<String, HashMap<String,Vector<Integer>>>();
		
		for(String head : Nonterminal)
		{
			int epsProdIndex = 0;
			HashMap<String,Vector<Integer>> parseTableColumn = new HashMap<String,Vector<Integer>>();
			Set<String> currentFirstSet = firstSets.get(head);
			Set<String> currentFollowSet = followSets.get(head);
			Vector <Vector<String>> currentProductions = grammarMap.get(head);
			
			for(String terminal : Terminals)
			{
				Vector<Integer> parseTableEntry = new Vector<Integer>();
				int i = 0;
				
				if(currentFirstSet.contains(terminal))
				{
					if(!terminal.equals("@"))
					{
						System.out.println("Nonterminal: "+head+" Terminal: "+terminal);
						//ToDo add Procution
						/*
						for(Vector<String> currentProduction : currentProductions)
						{
							if(currentProduction.get(0) == terminal && !terminal.equals("@"))
							{
								System.out.println("Nonterminal: "+firstSetKey+" Terminal: "+firstItem);
								parseTableEntry.add(i);
							}						
							i++;					
						}
						parseTableEntry.add(i);*/
					}
					
				}
				
				if(currentFirstSet.contains("@") && currentFollowSet.contains(terminal))
				{
					//ToDo
					//add Epsilon Production
				}
				parseTableColumn.put(terminal, parseTableEntry);				
			}			
			ret.put(head, parseTableColumn);		
		}		
		return ret;		
	}

}

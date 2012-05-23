import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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

//comment
public class ParserGenerator {
	
	//Contains the Grammar the Parsetable is created from
	private Map<String, Vector<Vector<String>>> grammarMap;
	
	public Map<String, HashMap<String,Vector<Integer>>> parserTable = 
		new HashMap<String, HashMap<String,Vector<Integer>>>();
	
	private String start;
	private Vector<String> Terminals;
	private Vector<String> Nonterminal;
	
	//FollowSets of each NonTerminal
	public Map<String, Set<String>> followSets = new HashMap<String, Set<String>>();
	//FirstSets of each NonTerminal
	public Map<String, Set<String>> firstSets = new HashMap<String, Set<String>>();
	//FirstSets of each Production of each NonTerminal
	public Map<String, HashMap<String,Integer>> firstSetsProductions = new HashMap<String, HashMap<String,Integer>>();
	
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
		Printer.printGrammar(grammarMap);
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
		Printer.printParserTable(Terminals,Nonterminal,parserTable);
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
		Terminals.add("$");
		
	}
	
	

	private void createFirstSet() {

		for (String head : Nonterminal) {
			// String head = p.getHead();
			firstSets.put(head, evalFirstSet(head, grammarMap,true));
		}
		Printer.printFirstSetsProductions(firstSetsProductions);
		
	}
	
	private Set<String> evalFirstSet(String head,
			Map<String, Vector<Vector<String>>> grammarMap,Boolean newHead) {
		if (firstSets.containsKey(head)) {
			return firstSets.get(head);
		}
		
		//FirstSet of current head
		Set<String> fs = new HashSet<String>();
		//FirstSet of current head for each production
		HashMap<String,Integer> fsp = new HashMap<String,Integer>();
		
		int i = 0;
		for (Vector<String> production : grammarMap.get(head)) {
			//FirstSet of current production
			Set<String> currentFS = new HashSet<String>();
			String term = production.get(0);
			
			//FirstSet Evaluation Rule 1
			if (Terminals.contains(term)) 
			{
				currentFS.add(term);
			} 
			else 
			{
				currentFS.addAll(evalFirstSet(term, grammarMap,false));
			}
			
			//FirstSet Evaluation Rule 2 and 3
			if(currentFS.contains("@"))
			{
				for(int j = 1; j < production.size();j++)
				{
					//temporary Set for next Nonterminal if epsilon is in FirstSet of current head
					Set<String> tempFS = new HashSet<String>();
					String nextTerm = production.get(j);					
					
					//evaluate FirstSet of next char if it is a NonTerminal 
					//else add terminal to FirstSet
					if(Nonterminal.contains(nextTerm))
					{
						tempFS = evalFirstSet(nextTerm, grammarMap,false);
					}
					else
					{
						tempFS.add(nextTerm);
					}

					//add temporary FS to FS of current production
					currentFS.addAll(tempFS);
					//break if next char is not a nonterminal with epsilon production
					if(!tempFS.contains("@") || Terminals.contains(nextTerm))
					{
						//remove epsilon if there are following terminals
						//example: <A> ::= <B> a b and <B> ::= c | epsilon
						//epsilon have to be in FS(<B>) but not in FS(<A>)
						currentFS.remove("@");
						break;
					}
				}
			}
			
			//add FirstSet of current production to FirstSet of head
			fs.addAll(currentFS);
			
			//add FirstSet of current production and it's index to FS of this production
			if(newHead)
			{
				for(String item : currentFS)
				{
					fsp.put(item, i);
				}
				i++;
			}

		}
		
		//add FirstSets of each production to FirstSet of this NonTerminal
		if(newHead)
		{
			firstSetsProductions.put(head, fsp);
		}
		
		return fs;
	}
	

	private void createFollowSet() {
		for (String head : Nonterminal) {
			// String head = p.getHead();
			followSets.put(head, evalFollowSet(head));
		}
		Printer.printFollowSets(followSets);
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
	
	private HashMap<String, HashMap<String,Vector<Integer>>> createParserTable(){
		
		HashMap<String, HashMap<String,Vector<Integer>>> ret = 
				new HashMap<String, HashMap<String,Vector<Integer>>>();
		
		for(String head : Nonterminal)
		{
			HashMap<String,Vector<Integer>> parseTableColumn = new HashMap<String,Vector<Integer>>();
			Set<String> currentFirstSet = firstSets.get(head);
			Set<String> currentFollowSet = followSets.get(head);
			
			for(String terminal : Terminals)
			{
				Vector<Integer> parseTableEntry = new Vector<Integer>();
				
				if(currentFirstSet.contains(terminal))
				{
					if(!terminal.equals("@"))
					{
						//System.out.println("Nonterminal: "+head+" Terminal: "+terminal);
						
						//Get index of production for current FirstSet item
						parseTableEntry.add(firstSetsProductions.get(head).get(terminal));
					}
					
				}
				
				if(currentFirstSet.contains("@") && currentFollowSet.contains(terminal))
				{
					//Get index of production for current FirstSet item
					parseTableEntry.add(firstSetsProductions.get(head).get("@"));
				}
				parseTableColumn.put(terminal, parseTableEntry);				
			}			
			ret.put(head, parseTableColumn);		
		}		
		return ret;		
	}

}
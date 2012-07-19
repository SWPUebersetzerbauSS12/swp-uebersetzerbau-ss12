package de.fuberlin.projectcii.ParserGenerator.src;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * The ParserGenerator reads a Grammar and tries to convert the Grammar in a way
 * that it can be Parsed with Lookahead 1 by eliminating Leftrekursions and
 * factorising the Productions
 * 
 * Additional the ParserGenerator creates a Parsetable from the modified
 * Grammar.
 */
public class ParserGenerator {

	// Contains the Grammar the Parsetable is created from
	private Map<String, Vector<Vector<String>>> grammarMap;

	// Contains the Parsertable
	// key = Head (nonterminal on left side of a production rule)
	// value = HashMap where key = terminal and value = index of related
	// production in grammaMap
	private Map<String, HashMap<String, Vector<Integer>>> parserTable;

	// start symbol of grammar
	private String start;
	// Vector containing all terminal symbols
	private Vector<String> Terminals;
	// Vector containing all nonterminal symbols
	private Vector<String> Nonterminal;

	// FollowSets of each NonTerminal
	private Map<String, Set<String>> followSets;
	// FirstSets of each Production of each NonTerminal
	private Map<String, HashMap<String, Vector<Integer>>> firstSetsProductions;

	public ParserGenerator() {

	}

	/**
	 * Initializes the grammar so a parsetable can be created from it.
	 * 
	 * @param grammar
	 * @param printSelected 
	 * 
	 * @param file
	 *            Path to the grammar file
	 * @return returns the created parsertable
	 * @throws IOException
	 */
	public void initialize(boolean changeToLL1, String grammar, boolean printSelected)
			throws IOException {
		grammarMap = new HashMap<String, Vector<Vector<String>>>();
		parserTable = new HashMap<String, HashMap<String, Vector<Integer>>>();
		start = "";
		followSets = new HashMap<String, Set<String>>();
		firstSetsProductions = new HashMap<String, HashMap<String, Vector<Integer>>>();
		Terminals = new Vector<String>();
		Nonterminal = new Vector<String>();
		readGrammar(changeToLL1, grammar,printSelected);
		fillTerminalNonterminal();
		/*
		 * computeFirstSet
		 */
		firstSetsProductions = createFirstSet(grammarMap);
		if (Settings.getFIRSTSET() && printSelected){
		    System.out.println("First-Set:");
		    Printer.printFirstSetsProductions(firstSetsProductions);
		}
		/*
		 * computeFollowSet
		 */
		followSets = createFollowSet(grammarMap);
		if (Settings.getFOLLOWSET() && printSelected){
		    System.out.println("Follow-Set:");
		    Printer.printFollowSets(followSets);
		}
		/*
		 * Create Parsetable
		 */
		parserTable = createParserTable();
		if (Settings.getPARSERTABLE() && printSelected){
		    System.out.println("Parsertabelle");
		    Printer.printParserTable(Terminals, Nonterminal, parserTable);
		}
	}

	/**
	 * Returns the parsertable created by "createParserTable"
	 */
	public Map<String, HashMap<String, Vector<Integer>>> getParseTable() {
		return this.parserTable;
	}

	/**
	 * Returns the Startsymbol of the grammar
	 */
	public String getStartSymbol() {
		return this.start;
	}

	/**
	 * Reads the Grammar from a file specified in SETTINGS.
	 * 
	 * @param grammar
	 * @param printSelected 
	 * @throws IOException
	 */
	private void readGrammar(boolean changeToLL1, String grammar, boolean printSelected)
			throws IOException {
		GrammarReader gR = new GrammarReader();
		grammarMap = gR.createGrammar(5, changeToLL1, grammar, printSelected);

		start = gR.getStartSymbol();
	}

	/**
	 * Generates 2 Vectors of Strings containing all nonterminals and terminals.
	 */
	private void fillTerminalNonterminal() {

		for (String nonTerminal : grammarMap.keySet()) {
			Nonterminal.add(nonTerminal);
		}
		for (String nonTerminal : grammarMap.keySet()) {
			for (Vector<String> production : grammarMap.get(nonTerminal)) {
				for (String symbol : production) {
					if (!Nonterminal.contains(symbol)
							&& !Terminals.contains(symbol)) {
						Terminals.add(symbol);
					}
				}
			}
		}
		Terminals.add(Settings.getEOF());

	}

	/**
	 * Evaluates the firstsets of a grammar.
	 * 
	 * @param grammarMap
	 *            contains the grammar
	 * @return returns a Map with all firstsets where key = head of a production
	 *         and value = HashMap with firstitems as key and indexes of related
	 *         productions in grammarMap as values
	 */
	private Map<String, HashMap<String, Vector<Integer>>> createFirstSet(
			Map<String, Vector<Vector<String>>> grammarMap) {
		Map<String, HashMap<String, Vector<Integer>>> firstSet = new HashMap<String, HashMap<String, Vector<Integer>>>();
		for (String head : Nonterminal) {
			firstSet.put(head,
					evalFirstSet(head, grammarMap, new HashSet<String>(), true));
		}
		return firstSet;
	}

	/**
	 * Evaluates the firstset of a given Nonterminal regarding to a grammarMap.
	 * 
	 * @param head
	 *            Nonterminal on left side of production rule
	 * @param grammarMap
	 *            contains the grammar
	 * @return returns firstset as Hashmap where key = item of firstset and
	 *         value = index of related production in grammarMap
	 */
	private HashMap<String, Vector<Integer>> evalFirstSet(String head,
			Map<String, Vector<Vector<String>>> grammarMap,
			Set<String> visitedNonTerminals, Boolean firstCall) {

		if (firstSetsProductions.containsKey(head)) {
			return firstSetsProductions.get(head);
		}

		// FirstSet of current head for each production
		HashMap<String, Vector<Integer>> fsp = new HashMap<String, Vector<Integer>>();

		int i = 0;
		for (Vector<String> production : grammarMap.get(head)) {
			if (firstCall) {
				visitedNonTerminals.clear();
			}

			// FirstSet of current production
			Set<String> currentFS = new HashSet<String>();
			String term = production.get(0);

			// FirstSet Evaluation Rule 1
			if (Terminals.contains(term)) {
				currentFS.add(term);
			} else {
				// Check if NonTerminal allready evaluated
				if (!visitedNonTerminals.contains(term)) {
					//mark NonTerminal as visited
					visitedNonTerminals.add(head);
					if (term.equals(head)) {
						currentFS.addAll(evalFirstSet(head, grammarMap,
								visitedNonTerminals, false).keySet());
					} else {
						currentFS.addAll(evalFirstSet(term, grammarMap,
								visitedNonTerminals, false).keySet());
					}
				}
			}

			// FirstSet Evaluation Rule 2 and 3
			if (currentFS.contains(Settings.getEPSILON())) {
				for (int j = 1; j < production.size(); j++) {
					// temporary Set for next Nonterminal if epsilon is in
					// FirstSet of current head
					Set<String> tempFS = new HashSet<String>();
					String nextTerm = production.get(j);

					// evaluate FirstSet of next char if it is a NonTerminal
					// else add terminal to FirstSet
					if (Nonterminal.contains(nextTerm)) {
						// Check if NonTerminal allready evaluated
						if (!visitedNonTerminals.contains(nextTerm)) {
							visitedNonTerminals.add(head);
							if (nextTerm.equals(head)) {
								currentFS.addAll(evalFirstSet(head, grammarMap,
										visitedNonTerminals, false).keySet());
							} else {
								tempFS = evalFirstSet(nextTerm, grammarMap,
										visitedNonTerminals, false).keySet();
							}
						}
					} else {
						tempFS.add(nextTerm);
					}

					// add temporary FS to FS of current production
					currentFS.addAll(tempFS);
					// break if next char is not a nonterminal with epsilon
					// production
					if (!tempFS.contains(Settings.getEPSILON())
							|| Terminals.contains(nextTerm)) {
						// remove epsilon if there are following terminals
						// example: <A> ::= <B> a b and <B> ::= c | epsilon
						// epsilon have to be in FS(<B>) but not in FS(<A>)
						currentFS.remove(Settings.getEPSILON());
						break;
					}
				}
			}

			// add FirstSet of current production and it's index to FS of this
			// production
			for (String item : currentFS) {
				Vector<Integer> tempProd;
				if (fsp.get(item) == null) {
					tempProd = new Vector<Integer>();
				} else {
					tempProd = fsp.get(item);
				}
				tempProd.add(i);
				fsp.put(item, tempProd);
			}
			i++;

		}

		return fsp;
	}

	/**
	 * Evaluates all followsets of a grammar given by a grammarMap.
	 * 
	 * @param grammarMap
	 *            contains the grammar
	 * @return HashMap with nonterminals as key and followsets as value
	 */
	private HashMap<String, Set<String>> createFollowSet(
			Map<String, Vector<Vector<String>>> grammarMap) {
		HashMap<String, Set<String>> followSets = new HashMap<String, Set<String>>();
		for (String head : Nonterminal) {
			// String head = p.getHead();
			followSets.put(head,
					evalFollowSet(head, grammarMap, new HashSet<String>()));
		}
		return followSets;
	}

	/**
	 * Evaluates follow set of a nonterminal regarding to a grammarMap
	 * 
	 * @param head
	 *            nonterminal at left side of a production rule
	 * @param grammarMap
	 *            contains the grammar
	 * @return returns a set with all follow items of given head
	 */
	private Set<String> evalFollowSet(String head,
			Map<String, Vector<Vector<String>>> grammarMap,
			Set<String> visitedNonTerminals) {

		visitedNonTerminals.add(head);
		if (followSets.containsKey(head)) {
			return followSets.get(head);
		}
		// add "eof" into follow(start symbol)
		Set<String> fs = new HashSet<String>();

		if (start.equals(head)) {
			fs.add(Settings.getEOF());
		}

		for (String currentHead : grammarMap.keySet()) {
			for (Vector<String> product : grammarMap.get(currentHead)) {
				for (Iterator<String> itr = product.iterator(); itr.hasNext();) {
					if (itr.next().equals(head)) {
						if (itr.hasNext()) {
							// not last symbol,
							String follow = itr.next(); // string follow is
														// terminal or
														// nonterminal.
							// terminal
							if (Terminals.contains(follow)) {
								fs.add(follow);
							}
							// nonterminal
							else {
								// get all elements from first set of this
								// nonterminal symbol
								HashSet<String> first = new HashSet<String>(
										firstSetsProductions.get(follow)
												.keySet());
								// the first set hat epsilon.
								if (first.contains(Settings.getEPSILON())) {
									if (!currentHead.equals(head)) {
										fs.addAll(evalFollowSet(currentHead,
												grammarMap, visitedNonTerminals));
									}
									first.remove(Settings.getEPSILON());
									fs.addAll(first);
								} else {
									fs.addAll(first);
								}
							}
						} else {
							// check if the symbol has already been visited in
							// the rekursive tree
							if (!currentHead.equals(head)
									&& !visitedNonTerminals
											.contains(currentHead)) {
								// the last symbol
								fs.addAll(evalFollowSet(currentHead,
										grammarMap, visitedNonTerminals));

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
	 * Evaluates the parsertable.
	 * 
	 * @return returns parsertable as HashMap where: Key = nonterminal Value =
	 *         HasMap where Key = terminal and values = index of related
	 *         production in grammarMap
	 */
	private HashMap<String, HashMap<String, Vector<Integer>>> createParserTable() {
		
		HashMap<String, HashMap<String, Vector<Integer>>> ret = new HashMap<String, HashMap<String, Vector<Integer>>>();

		//eval each row (each NonTerminal) of parsertable
		for (String head : Nonterminal) {
			HashMap<String, Vector<Integer>> parseTableRow = new HashMap<String, Vector<Integer>>();
			//get FirstSet of current head
			Set<String> currentFirstSet = firstSetsProductions.get(head)
					.keySet();
			//get FollowSet of current head
			Set<String> currentFollowSet = followSets.get(head);

			//eval each table element (each Terminal) of the current row
			for (String terminal : Terminals) {
				Vector<Integer> parseTableEntry = new Vector<Integer>();

				//1st Rule (Productions with FirstSet entry)
				if (currentFirstSet.contains(terminal)) {
					if (!terminal.equals(Settings.getEPSILON())) {
						// Get index of production for current FirstSet item
						parseTableEntry.addAll(firstSetsProductions.get(head)
								.get(terminal));
					}

				}

				//2nd Rule (add apsilon producion)
				if (currentFirstSet.contains(Settings.getEPSILON())
						&& currentFollowSet.contains(terminal)) {
					// Get index of production for current FirstSet item
					parseTableEntry.addAll(firstSetsProductions.get(head).get(
							Settings.getEPSILON()));
				}
				//add table element to row
				parseTableRow.put(terminal, parseTableEntry);
			}
			//add row to parsertable
			ret.put(head, parseTableRow);
		}
		return ret;
	}

	/**
	 * Get method for grammarMap.
	 * 
	 * @return map containing the grammar.
	 */
	public Map<String, Vector<Vector<String>>> getGrammar() {

		return grammarMap;
	}

	/**
	 * check out if the garmmar is LL(1)-parsable.
	 * 
	 * @param parsertable
	 * @return boolean
	 */
	public boolean parsable_LL1(
			Map<String, HashMap<String, Vector<Integer>>> parsertable) {
		HashMap<String, Vector<Integer>> parseTableRow = new HashMap<String, Vector<Integer>>();
		Vector<Integer> parseTableEntry = new Vector<Integer>();

		for (String head : Nonterminal) {
			parseTableRow = parsertable.get(head);
			for (String terminal : Terminals) {
				parseTableEntry = parseTableRow.get(terminal);
				// check the no. of entries of each terminal
				if ((parseTableEntry.size() > 1)) {
					Vector<String> prod0 = this.grammarMap.get(head).get(parseTableEntry.get(0));
					Vector<String> prod1 = this.grammarMap.get(head).get(parseTableEntry.get(1));
					Vector<String> prod = new Vector<String>();
							
					//If there are 2 productions in one entry, and one of them is an epsilon production, print a warning and remove epsilon Production
					if(parseTableEntry.size() == 2 && (prod0.get(0).equals(Settings.getEPSILON()) || prod1.get(0).equals(Settings.getEPSILON())) )
					{
						
						String production = head+" ::= ";
						//remove epsilon production
						if(prod0.get(0).equals(Settings.getEPSILON()))
						{
							prod = prod1;
							this.parserTable.get(head).get(terminal).remove(0);
						}
						else
						{
							prod = prod0;
							this.parserTable.get(head).get(terminal).remove(1);
						}
						
						//Add rump to output of production
						production += prod.get(0);
						for (int i = 1;i<prod.size();i++)
						{
							production += "."+prod.get(i);
						}
						
						//Print Warning
						System.out.println();
						System.out.println("Warning: There are 2 productions at entry for nonterminal: <"+head+"> " +
								"and terminal \""+terminal+"\" whereas one of them is an epsilon production!");
						System.out.println("Parser removed epsilon production to recover LL(1) parseability!");
						System.out.println("Production used by Parser: "+production);
						System.out.println();					
					}
					else
					{
						System.out.println();
						System.out.println("the grammar is NOT parsable for LL(1), please input a new one...");
						System.out.println();
						return false;
					}
				}
			}
		}
		System.out.println();
		System.out.println("the grammar is parsable for LL(1).  ");
		System.out.println();
		return true;

	}

    public Map<String, HashMap<String, Vector<Integer>>> getFirstSetsProductions() {
        // TODO Auto-generated method stub
        return firstSetsProductions;
    }

    public Map<String, Set<String>> getFollowSets() {
        // TODO Auto-generated method stub
        return followSets;
    }

}
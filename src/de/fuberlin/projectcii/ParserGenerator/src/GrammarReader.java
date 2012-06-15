package de.fuberlin.projectcii.ParserGenerator.src;
import java.io.*;
import java.util.*;

/**
 * Reads a grammar from a given file and initialises it for better use. Before
 * the Grammar is returned, direct-, and indirect left-rekursions are eliminated
 * and left-factorisation is performed. Elimination of left-rekursions is fixed
 * at 4 Runs after which the Grammar is deemed not LL(1)-parsable.
 * 
 */
public class GrammarReader {

	// the Startsymbol
	private String startSymbol;

	private Vector<String> heads;

	private enum ReadingState {
		TERMINAL, NONTERMINAL, NONE
	}

	/**
	 * Getter for the Startsymbol
	 * 
	 * @return The start symbol for the read Grammar
	 * @author Patrick Schlott
	 */
	public String getStartSymbol() {
		return startSymbol;
	}

	/**
	 * Reads a grammar from the given file in BNF and converts it to a Hash-Map
	 * containing the Productions. Before the grammar is returned, direct-, and
	 * indirect left-rekursions are eliminated and left-factorisation is
	 * performed. elimination of left-rekursions is fixed at 4 runs after which
	 * the Grammar is deemed not LL(1)-parsable.
	 * 
	 * @author Patrick Schlott,Ying Wei
	 * @param NItr The maximum number of iterations for clearing Leftrekursions,
	 *             if more iterations are needes, the whole grammar will be cleared
	 * @return The grammar as HashMap where the keys are the heads of the
	 *         production and the values are vectors containing the productions
	 *         itself. Each production is represented as a vector of terminal
	 *         and nonterminal symbols
	 * @throws IOException 
	 *            
	 * @throws RuntimeException  if the No. of iterations more than NItr, 
	 *                           then there is too much iterations (endless iteration)
	 * 
	 */
	public Map<String, Vector<Vector<String>>> createGrammar(int NItr)
			throws IOException {
		// Read the file
		Vector<Productions> grammar = ReadFile();
		Printer.printGrammar(grammar);
		// Perform Leftfactorisation
		grammar = combineLeftFactorization(grammar);
		Printer.printGrammar(grammar); 
		// Eliminate indirect and direct leftrekursions

		heads = new Vector<String>();
		for (Productions nonterminal : grammar) {
			heads.add(nonterminal.getHead());
		}
		Boolean[] rekursive = { true };
		int iteration = 0;
		grammar = eliminateDirectLeftRekursion(grammar);
		Printer.printGrammar(grammar);

		while (rekursive[0] && iteration < NItr) {
			rekursive[0] = false;
			grammar = eliminateIndirectLeftRekursion(grammar, rekursive);
			grammar = eliminateDirectLeftRekursion(grammar);
			iteration++;
		}
		if (iteration < NItr) {
			// definie first Element in Vector as startsymbol
			startSymbol = grammar.elementAt(0).getHead();
		} else {
			throw new RuntimeException(
					"the no. of the iterations:"
							+ iteration
							+ " >= Max No. of Iterations "
							+ NItr
							+ "\n"
							+ "Too Many Iterations, it's unparsable with LL(1), please input a right file!!");

			// System.out.println("the no. of the iterations:"+iteration+" >= Max No. of Iterations "+NItr);
			// System.out.println("Too Many Iterations, it's unparsable with LL(1), please input a right file!!");
			// grammar.clear();
		}

		return buildGrammarMap(grammar);

	}

	/**
	 * Reads a grammar from the given file in BNF and converts it to Vector of
	 * Productions.
	 * 
	 * @author Patrick Schlott, Ying Wei
	 * @param file
	 *            The path to the file containing the grammar that shall be
	 *            used.
	 * @return The grammar as a Vector of Productions
	 * @throws IOException
	 * @throws RuntimeException check not correct format of grammar
	 * if no "::=", or either the part of left side or right side is empty.(No BNF)
	 * if the left side(it must be nonterminal symbol) of production is not in format <A>.(false Production/grammar)
	 */        
	private Vector<Productions> ReadFile() throws IOException {

		// Vector the nonterminals are saved to
		Vector<Productions> grammar = new Vector<Productions>();

		// Read File
		BufferedReader in = new BufferedReader(new FileReader(
				Settings.getGRAMMAR_PATH()));

		ReadingState state;
		String line;
		int lineNo=0;
		while ((line = in.readLine()) != null) {
			lineNo++;
			if (!line.equals("")) {
				// Split line in head and rump at ::=
				String[] nonterminalLR = line.split("::=");
				//if there is no ::= in grammar or either one part of nonterminalLR is empty (Y.)
				if(nonterminalLR.length!=2||nonterminalLR[0].isEmpty()||nonterminalLR[1].isEmpty()){
					throw new RuntimeException("invalid input grammar at line "+lineNo+".\n " +
							"correct format looks like <A> ::= <A> \"test\" \"c\".... \n" +
							"please input a correct grammar ");
					
				}
				// Create Nonterminal and fill head and rump
				Productions nonterminal = new Productions(
						nonterminalLR[0].trim());
				//regulaere Ausdruck, check out the left side of the productions, 
				//they(all are non-terminal) must be <A>,e.g.<<A> or <> or <A>a or A>>. ect. are uncorrect format(Y.)
				if(!nonterminal.getHead().matches("<\\w+>")){
					throw new RuntimeException("invalid input garmmar at line "+lineNo+".\n " +"the format for nonterminal is false!!"); 
				}

				String rump = "";
				//why "for"?? size of nonterminalLR is 2 fix!
				// there is nonterminalLR[0] and nonterminalLR[1].. then no any more
				for (int i = 1; i < nonterminalLR.length; i++) {
					rump += nonterminalLR[i];
				}

				Vector<String> productionVector = new Vector<String>();
				state = ReadingState.NONE;
				String symbol = "";
				for (char c : rump.toCharArray()) {
					switch (state) {

					case NONE:

						if (c == '"') {
							state = ReadingState.TERMINAL;
						} else if (c == '<') {
							state = ReadingState.NONTERMINAL;
						} else if (c == '|') {
							nonterminal.InsertProduction(productionVector);
							productionVector = new Vector<String>();
						}
						break;

					case TERMINAL:

						if (c == '"') {
							state = ReadingState.NONE;
							productionVector.add(symbol);
							symbol = "";
						} else {
							symbol += c;
						}
						break;

					case NONTERMINAL:

						if (c == '>') {
							state = ReadingState.NONE;
							productionVector.add("<" + symbol + ">");
							symbol = "";
						} else {
							symbol += c;
						}
						break;
					}
				}
				nonterminal.InsertProduction(productionVector);
				// Finaly add the Nonterminal to the Grammar
				grammar.add(nonterminal);
			}
		}
		in.close();

		return grammar;
	}

	/**
	 * Performs left-factorisation on a given grammar.
	 * 
	 * @author Patrick Schlott
	 * @param grammar
	 *            Grammar to be left-factorised
	 * @return The Modified grammar as a Vector of Productions
	 */
	private Vector<Productions> combineLeftFactorization(
			Vector<Productions> grammar) {
		// Vector containing the modified grammar
		Vector<Productions> grammarMod = new Vector<Productions>();

		// Go through every Nonterminal in the grammar
		for (Productions nonterminal : grammar) {
			// Counter for naming the new productions
			int counter = 0;

			// Go through the Nonterminal as long as there is something to do
			boolean goOn = true;
			while (goOn) {
				Vector<Vector<String>> productions = nonterminal.productions;

				// Calculates which Factor is the longest
				// Returns 0: size of longest Factor
				// 1: smallest Productionnumber containing this faktor

				int[] factor = calculateLongestFactor(productions);

				int productionNr = factor[1];
				int longest = factor[0];

				// check if a Factor exists
				if (longest > 0) {
					// First Production containing the Factor
					Vector<String> matchingProduction = nonterminal.productions
							.elementAt(productionNr);
					// Initiate new Nonterminals
					String head = nonterminal.getHead();
					String head1 = head.substring(0, head.length() - 1)
							+ counter + ">";
					Productions nonTerminalOld = new Productions(head);
					Productions nonTerminalNew = new Productions(head1);
					// Add Factor + Head1 to Old Production
					Vector<String> factorisation = new Vector<String>();
					for (int j = 0; j < longest; j++) {
						factorisation.add(productions.elementAt(productionNr)
								.elementAt(j));
					}
					factorisation.add(head1);
					nonTerminalOld.InsertProduction(factorisation);
					// Go through all Productions and check if they Containing
					// the Factor
					for (Vector<String> production : productions) {
						if (production.size() >= longest) {
							boolean matched = true;
							int i = 0;
							// check weather the Production contains the Factor
							while (matched) {
								if (i < longest) {
									if (matchingProduction.elementAt(i).equals(
											production.elementAt(i))) {
										i++;
									} else
										matched = false;
								} else
									matched = false;
							}
							// deside where to put the production depending on i
							// and longest
							if (i < longest) {
								// Productions is not part of the factorisation
								nonTerminalOld.InsertProduction(production);
							}
							// Production contains the longest factor
							else if (i == longest) {
								if (longest == production.size()) {
									Vector<String> epsilon = new Vector<String>();
									epsilon.add(Settings.getEPSILON());
									nonTerminalNew.InsertProduction(epsilon);
								} else {
									Vector<String> newProduction = new Vector<String>();
									for (int k = i; k < production.size(); k++) {
										newProduction.add(production
												.elementAt(k));
									}
									nonTerminalNew
											.InsertProduction(newProduction);
								}
							}
						} else {
							nonTerminalOld.InsertProduction(production);
						}
					}
					// add new and old Production back in the modified grammar
					grammarMod.remove(nonterminal);
					nonterminal = nonTerminalOld;
					grammarMod.add(nonTerminalOld);
					grammarMod.add(nonTerminalNew);
					// keep the sorting of productions intact
					for (int i = 0; i < counter; i++) {
						// int productNr = 0;
						for (Productions p : grammarMod) {
							if (p.getHead().equals(
									head.substring(0, head.length() - 1) + i
											+ ">")) {
								// Productions savedProduction = p;
								grammarMod.remove(p);
								grammarMod.add(p);
								break;
							}

						}
					}
					counter++;
				} else {
					if (counter == 0) {
						grammarMod.add(nonterminal);
					}
					goOn = false;
				}
			}
		}
		return grammarMod;
	}

	/**
	 * Calculates the longest Factor in the Production
	 * 
	 * @author Patrick Schlott
	 * @param productions
	 *            The Productions of the nonterminal
	 * @return An array containing [0]: size of the longest factor [1]: first
	 *         production containing the factor
	 */
	private int[] calculateLongestFactor(Vector<Vector<String>> productions) {
		int productionNr = 0; // Returnvalue 1
		int longest = 0; // Returnvalue 0
		// iterate through every production
		int productionCnt = productions.size();
		for (int i = 0; i < productionCnt; i++) {
			Vector<String> production = productions.elementAt(i);
			// compare symbol-vice with other productions
			for (int j = i + 1; j < productionCnt; j++) {
				Vector<String> productionMatch = productions.elementAt(j);
				boolean matched = true;
				int k = 0;
				// calculate longest match
				while (matched) {
					if (production.size() > k && productionMatch.size() > k) {
						if (production.elementAt(k).equals(
								productionMatch.elementAt(k))) {
							k++;
						} else
							matched = false;
					} else
						matched = false;
				}
				// check if new Factor is longer than the old
				if (longest < k) {
					longest = k;
					productionNr = i;
				}
			}
		}
		int[] factor = { longest, productionNr };
		return factor;
	}

	/**
	 * Eliminates indirect left-factorisation on a given grammar.
	 * 
	 * @author Patrick Schlott
	 * @param grammar
	 *            Grammar to be examined
	 * @param rekursive
	 *            Call by Reference bool, indicating weather the grammar has
	 *            been modified
	 * @return The Modified grammar as a Vector of Productions
	 */
	private Vector<Productions> eliminateIndirectLeftRekursion(
			Vector<Productions> grammar, Boolean[] rekursive) {

		Vector<Productions> grammarMod = new Vector<Productions>();
		int NonTerminalCounter = grammar.size();
		// add first grammar-element as it can not be indirect left rekursive
		grammarMod.add(grammar.firstElement());
		// go through all grammar-elements
		for (int i = 1; i < NonTerminalCounter; i++) {
			Productions nonTerminalCurrent = grammar.elementAt(i);
			// initalise bitmap indicating which Productions are leftrekursive
			boolean[] bitmap = new boolean[nonTerminalCurrent.productions
					.size()];
			for (int y = 0; y < nonTerminalCurrent.productions.size(); y++) {
				bitmap[y] = false;
			}
			// init new nonTerminal
			Productions nonTerminalNew = new Productions(
					nonTerminalCurrent.getHead());
			rekursive[0] = false;
			// check all nonterminals after i if they have i as first Element
			for (int j = 0; j < i; j++) {
				Productions nonTerminalChecked = grammarMod.elementAt(j);
				int x = 0;
				for (Vector<String> production : nonTerminalCurrent.productions) {
					if (production.firstElement().equals(
							nonTerminalChecked.getHead())) {
						rekursive[0] = true;
						bitmap[x] = true;
						for (Vector<String> productionChecked : nonTerminalChecked.productions) {
							Vector<String> newProduction = new Vector<String>();
							for (String symbol : productionChecked) {
								newProduction.add(symbol);
							}
							nonTerminalNew.InsertProduction(newProduction);
							for (int k = 1; k < production.size(); k++) {
								nonTerminalNew.productions.lastElement().add(
										production.elementAt(k));
							}
						}
					}
					x++;
				}
			}
			if (rekursive[0]) {
				for (int y = 0; y < nonTerminalCurrent.productions.size(); y++) {
					if (!bitmap[y]) {
						nonTerminalNew
								.InsertProduction(nonTerminalCurrent.productions
										.elementAt(y));
					}
				}
				grammarMod.add(nonTerminalNew);
			} else {
				grammarMod.add(nonTerminalCurrent);
			}
		}
		return grammarMod;
	}

	/**
	 * Eliminates direct leftrekursions by introducing new Nonterminal <"N"$>
	 * 
	 * @author Patrick Schlott
	 * @param grammar Grammar to be examined
	 * @return The Modified grammar as a Vector of Productions
	 */
	private Vector<Productions> eliminateDirectLeftRekursion(
			Vector<Productions> grammar) {
		// Vector containing the modified grammar
		Vector<Productions> grammarMod = new Vector<Productions>();
		// Go through every Nonterminal in the grammar
		for (Productions nonterminal : grammar) {
			// indicates weather the grammar in leftrekursive or not
			boolean lRekursiv = false;

			String head = nonterminal.getHead();
			// initialise Bitmap indicating which productions contain
			// direct leftrekursions
			int productionCnt = nonterminal.productions.size();
			boolean[] bitmap = new boolean[productionCnt];
			// counter for Bitmap
			int i = 0;
			// interate through every production and check if first Element
			// equals head
			for (Vector<String> production : nonterminal.productions) {
				String first = production.firstElement();
				if (first.equals(head)) {
					bitmap[i] = true;
					lRekursiv = true;
				} else {
					bitmap[i] = false;
				}
				i++;
			}
			/*
			 * If the whole Nonterminal has no leftrekursions return the
			 * Nonterminal as is, elso eliminate leftrekursion and return new
			 * Nonterminal <"N"> and <"N"1>
			 */
			if (lRekursiv) {
				// initialise new nonterminal <"N">
				Productions nonTerminalMod = new Productions(head);
				// initialise new nonterminal <"N"1>
				String head1 = head.substring(0, head.length() - 1) + "_";

				// prevent duplicate Productions
				boolean alreadyExists = true;
				while (alreadyExists) {
					if (heads.contains(head1 + ">")) {
						head1 += "_";
					} else {
						alreadyExists = false;
					}
				}
				head1 += ">";
				heads.add(head1);
				Productions nonTerminalNew = new Productions(head1);
				i = 0;
				for (Vector<String> production : nonterminal.productions) {
					/*
					 * if production is lrekursiv add to <"N"1> without first
					 * element and add <"N"1> at end of production
					 */
					if (bitmap[i]) {
						production.remove(0);
						production.add(head1);
						nonTerminalNew.InsertProduction(production);
					}
					/*
					 * if production is not lrekursiv add to <"N"> with element
					 * <"N"1> at end of production
					 */
					else {
						if (!production.elementAt(0).equals(
								Settings.getEPSILON())) {
							production.add(head1);
							nonTerminalMod.InsertProduction(production);
						}
					}
					i++;
				}
				// Add eplison-production to <"N"1>
				Vector<String> production = new Vector<String>();
				production.add(Settings.getEPSILON());
				// Add new nonTerminal to old production if no productions are
				// left
				if (nonTerminalMod.productions.size() == 0) {

					Vector<String> finalProduction = new Vector<String>();
					finalProduction.add(head1);
					nonTerminalMod.productions.add(finalProduction);
				}
				nonTerminalNew.InsertProduction(production);
				grammarMod.add(nonTerminalMod);
				grammarMod.add(nonTerminalNew);
			} else {
				grammarMod.add(nonterminal);
			}

		}
		return grammarMod;
	}

	/**
	 * Transforms the internal Vector to an equivalent Hashmap. The Hashmap has
	 * the form: Map<String, Vector<Vector<String>>> with the head of the
	 * productions as keys.
	 * 
	 * @author Ying Wei
	 * @param grammar
	 *            Grammar to be transformed
	 * @return The transformed Grammar
	 */
	private Map<String, Vector<Vector<String>>> buildGrammarMap(
			Vector<Productions> grammar) {
		Map<String, Vector<Vector<String>>> gMap = new LinkedHashMap<String, Vector<Vector<String>>>();
		for (Productions p : grammar) {
			gMap.put(p.getHead(), p.productions);
		}
		return gMap;
	}

}

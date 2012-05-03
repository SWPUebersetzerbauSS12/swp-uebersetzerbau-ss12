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

public class ParserGenerator {
	//Contains the Grammar the Parsetable is created from
	private Vector <Productions> grammar;
	
	private Vector<String> Terminals;
	private Vector<String> Nonterminal;
	
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
		
		/*
		 * do leftfactorisation
		 */
		combineLeftFactorization();
		
		/*
		 * Eliminate indirect leftrecursions
		 */
		
		eliminateIndirectLeftRekursion();
		
		/*
		 * Eliminate direct leftrekursions
		 */
		eliminateDirectLeftRekursion();
		
		//Create Terminal- and NonterminalSet
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
		
		createParseTable();
		
		//testprints begin
		System.out.println();
		System.out.println("Nicht Rekursive Grammatik");
		printGrammar();
		//testprints end
	}
	private void createParseTable() {
		// TODO Auto-generated method stub
		
	}

	private void eliminateIndirectLeftRekursion() {
		// TODO Auto-generated method stub
		
	}

	private void combineLeftFactorization() {
		// TODO Auto-generated method stub
		
	}

	private void createFollowSet() {
		// TODO Auto-generated method stub
		
	}

	private void createFirstSet() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Reads the Grammar from a given file
	 * 
	 * Input: String file - Path to the file
	 * Return: Void
	 */
	private void readGrammar(String file){
		GrammarReader g = new GrammarReader(file);
		grammar = g.ReadFile();
		//testprints begin
		System.out.println("Ausgangsgrammatik");
		printGrammar();
		//testprints end
	}
	/*
	 * Eliminates direct leftrekursions be introducing new Nonterminal
	 * <"N"1>
	 * 
	 * Input: Void
	 * Return: Void
	 */
	private void eliminateDirectLeftRekursion(){
		//Vector containing the modified grammar
		Vector <Productions> grammarMod = new Vector <Productions>();
		//Go through every Nonterminal in the grammar
		for (Productions nonterminal:grammar){
			//indicates weather the grammar in leftrekursive or not
			boolean lRekursiv = false;
			
			String head = nonterminal.getHead();
			//initialise Bitmap indicating which productions contain
			//direct leftrekursions
			int productionCnt = nonterminal.productions.size();
			boolean[] bitmap = new boolean[productionCnt];
			//counter for Bitmap
			int i=0;
			//interate through every production and check if first Element equals head 
			for (Vector<String> production:nonterminal.productions){

				String first = production.firstElement();
				if (first.equals(head)){
					bitmap[i] = true;
					lRekursiv = true;
				}
				else{
					bitmap[i] = false;
				}
				i++;
			}
			/*
			 * If the whole Nonterminal has no leftrekursions return the
			 * Nonterminal as is, elso eliminate leftrekursion and return
			 * new Nonterminal <"N"> and <"N"1> 
			 */
			if (lRekursiv){
				//initialise new nonterminal <"N">
				Productions nonTerminalMod = new Productions(head);
				//initialise new nonterminal <"N"1>
				String head1 = head.substring(0, head.length()-1)+"1>";
				Productions nonTerminalNew = new Productions(head1);
				i=0;
				for (Vector<String> production:nonterminal.productions){
					/* 
					 * if production is lrekursiv add to <"N"1> without first
					 * element and add <"N"1> at end of production
					 */
					if (bitmap[i]){
						production.remove(0);
						production.add(head1);
						nonTerminalNew.InsertProduction(production);
					}
					/* 
					 * if production is not lrekursiv add to <"N"> with element
					 * <"N"1> at end of production
					 */
					else{
						production.add(head1);
						nonTerminalMod.InsertProduction(production);
					}
					i++;
				}
				//Add eplison-production to <"N"1>
				Vector<String> production = new Vector<String>();
				production.add(" ");
				nonTerminalNew.InsertProduction(production);
				grammarMod.add(nonTerminalMod);
				grammarMod.add(nonTerminalNew);
			}
			else {
				grammarMod.add(nonterminal);
			}
			
		}
		grammar = grammarMod;
	}
	
	//Visual debug-Function printing the grammar 
	private void printGrammar(){
		
		for (int i = 0; i< grammar.size() ; i++){
			Productions nonterminal = grammar.elementAt(i);
			System.out.print("Head = "+nonterminal.getHead()+" ; ");
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
	}
	
	private void fillTerminalNonterminal(){
		
		for (Productions nonterminal:grammar){
			Nonterminal.add(nonterminal.getHead());
		}
		for (Productions nonterminal:grammar){
			for (Vector<String> production:nonterminal.productions){
				for (String symbol:production){
					if (!Nonterminal.contains(symbol) && !Terminals.contains(symbol)){
						Terminals.add(symbol);
					}
				}
			}
		}
		
	}

}

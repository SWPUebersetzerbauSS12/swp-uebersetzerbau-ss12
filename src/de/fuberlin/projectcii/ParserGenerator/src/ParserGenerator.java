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
		
		System.out.println();
		System.out.println("Linksfaktorisierte Grammatik");
		printGrammar();
		
		/*
		 * Eliminate indirect leftrecursions
		 */
		
		//eliminateIndirectLeftRekursion();
		
		System.out.println();
		System.out.println("Indirekte Linksrekursion weg");
		printGrammar();
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

		Vector <Productions> grammarMod = new Vector <Productions>();
		int NonTerminalCounter = grammar.size();
		grammarMod.add(grammar.firstElement());
		
		for (int i = 1; i < NonTerminalCounter;i++){
			Productions nonTerminalCurrent = grammar.elementAt(i);
			Productions nonTerminalNew = new Productions(nonTerminalCurrent.getHead());
			for (int j = 0; j<i; j++){
				Productions nonTerminalChecked = grammar.elementAt(j);
				grammarMod.add(nonTerminalCurrent);
				for (Vector<String> production:nonTerminalCurrent.productions){
					if (production.firstElement().equals(nonTerminalChecked.getHead())){
						for (Vector<String> productionChecked:nonTerminalChecked.productions){
							nonTerminalNew.InsertProduction(productionChecked);
							for (int k=1;k<production.size();k++){
								nonTerminalNew.productions.lastElement().add(production.elementAt(k));
							}
						}
					}
					else{
						nonTerminalNew.InsertProduction(production);
					}
				}
				
			}
		}
		grammar = grammarMod;
	}

	private void combineLeftFactorization() {
		//Vector containing the modified grammar
		Vector <Productions> grammarMod = new Vector <Productions>();

		//Go through every Nonterminal in the grammar
		for (Productions nonterminal:grammar){
			
			int counter=0;
			
			boolean goOn = true;
			while (goOn){
				Vector<Vector<String>> productions = nonterminal.productions;
				
				int[]factor = calculateLongestFactor(productions);
				
				int productionNr=factor[1];
				int longest=factor[0];
				
				if (longest > 0){
					Vector<String> matchingProduction = nonterminal.productions.elementAt(productionNr);
					String head=nonterminal.getHead();
					String head1 = head.substring(0, head.length()-1)+counter+">";
					Productions nonTerminalOld = new Productions(head);
					Productions nonTerminalNew = new Productions(head1);
					Vector<String> factorisation = new Vector<String>();
					for (int j=0; j<longest;j++){
						factorisation.add(productions.elementAt(productionNr).elementAt(j));
					}
					factorisation.add(head1);
					nonTerminalOld.InsertProduction(factorisation);
					
					for (Vector<String> production:productions){
						if (production.size() >= longest){
							boolean matched = true;
							int i = 0;
							while (matched){
								if (i<longest){
									if (matchingProduction.elementAt(i).equals(production.elementAt(i))){
										i++;
									}
									else matched = false;
								}
								else matched = false;
							}
							if (i<longest){
								nonTerminalOld.InsertProduction(production);
							}
							else if (i==longest){
								if (longest == production.size()){
									Vector<String> epsilon = new Vector<String>();
									epsilon.add("@");
									nonTerminalNew.InsertProduction(epsilon);
								}
								else{
									for (int k=i; k<production.size() ;k++){
										Vector<String> newProduction = new Vector<String>();
										newProduction.add(production.elementAt(k));
										nonTerminalNew.InsertProduction(newProduction);
										
									}
								}
							}
						}
						else{
							nonTerminalOld.InsertProduction(production);
						}
					}
					counter++;
					grammarMod.remove(nonterminal);
					nonterminal = nonTerminalOld;
					grammarMod.add(nonTerminalOld);
					grammarMod.add(nonTerminalNew);
				}
				else{
					if(counter == 0){
						grammarMod.add(nonterminal);
					}
					goOn=false;
				}
				System.out.println(nonterminal.getHead()+" : "+longest+" : "+productionNr);
			}
		}
		grammar = grammarMod;
	}

	private int[] calculateLongestFactor(Vector<Vector<String>> productions) {
		int productionNr=0;
		int longest=0;
		
		int productionCnt = productions.size();
		for (int i=0; i<productionCnt;i++){
			Vector<String> production = productions.elementAt(i);
			for(int j=i+1;j<productionCnt;j++){
				Vector<String> productionMatch = productions.elementAt(j);
				boolean matched = true;
				int k=0;
				while (matched){
					if (production.size() > k && productionMatch.size() > k){
						if (production.elementAt(k).equals(productionMatch.elementAt(k))){
							k++;
						}
						else matched = false;
					}
					else matched = false;
				}
				if (longest < k){
					longest = k;
					productionNr = i;
				}
			}				
		}
		int[] factor = {longest,productionNr};
		return factor ;
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
				String head1 = head.substring(0, head.length()-1)+"$>";
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
				production.add("@");
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

import java.io.*;
import java.util.*;

/* 
 * GrammarReader reads a Grammar in BNF from a given file
 * 
 * Author: Patrick Schlott
 * 
 */
public class GrammarReader {
	
	public Vector<Productions> grammar;
	private String startSymbol;
	/*
	 * Reads the file from the given file in BNF
	 * and Converts it to a Vector of Nonterminals
	 * 
	 * Input:()
	 * Returnvalue: Vector<Nonterminals> containing the read grammar
	 */
	
	public String getStartSymbol(){
		return startSymbol;
	}
	
	public Map<String, Vector<Vector<String>>> createGrammar(String file){
		Vector<Productions>grammar=ReadFile(file);
		grammar=combineLeftFactorization(grammar);
		grammar=eliminateIndirectLeftRekursion(grammar);
		grammar=eliminateDirectLeftRekursion(grammar);
		this.grammar = grammar;
		printGrammar();
		startSymbol = grammar.elementAt(0).getHead();
		return buildGrammarMap(grammar);
	}
	
	private Vector<Productions> ReadFile(String file){
		
		//Vector the nonterminals are saved to
		Vector<Productions> grammar = new Vector<Productions>();
		
		// Read File
		try {
		    BufferedReader in = new BufferedReader(new FileReader(file));
		    
		    String line;
		    while ((line = in.readLine()) != null) {
		    	//Split line in head and rump at ::=
		    	String[] nonterminalLR = line.split("::=");
		    	//Create Nonterminal and fill head and rump
		    	Productions nonterminal = new Productions(nonterminalLR[0].trim());
		    	//Split productions in Rump at |
		    	String[] b = nonterminalLR[1].split("\\|");
		    	//Insert every Production to the Nonterminal
		        for (String production: b) {
		        	//Split Elementes at Space
		    		String[] c = production.trim().split(" ");
		    		List<String> list = Arrays.asList(c);
		    		Vector<String> productionVector = new Vector<String>(list);
		    		nonterminal.InsertProduction(productionVector);
		    	}
		        //Finaly add the Nonterminal to the Grammar
		    	grammar.add(nonterminal);
		    }
		    in.close();
		} catch (IOException e) {
		}
		
		
		return grammar;
	}
	
	private Vector<Productions> combineLeftFactorization(Vector<Productions> grammar) {
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
		return grammarMod;
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
	
	private Vector <Productions> eliminateIndirectLeftRekursion(Vector <Productions> grammar) {

		Vector <Productions> grammarMod = new Vector <Productions>();
		int NonTerminalCounter = grammar.size();
		grammarMod.add(grammar.firstElement());
		
		for (int i = 1; i < NonTerminalCounter;i++){
			Productions nonTerminalCurrent = grammar.elementAt(i);
			boolean[] bitmap = new boolean[nonTerminalCurrent.productions.size()];
			for (int y=0; y<nonTerminalCurrent.productions.size();y++){
				bitmap[y] = false;
			}
			Productions nonTerminalNew = new Productions(nonTerminalCurrent.getHead());
			boolean rekursive = false;
			for (int j = 0; j<i; j++){
				Productions nonTerminalChecked = grammarMod.elementAt(j);
				int x = 0;
				for (Vector<String> production:nonTerminalCurrent.productions){
					if (production.firstElement().equals(nonTerminalChecked.getHead())){
						rekursive = true;
						bitmap[x] = true;
						for (Vector<String> productionChecked:nonTerminalChecked.productions){
							Vector<String> newProduction = new Vector<String>();
							for (String symbol:productionChecked){
								newProduction.add(symbol);
							}
							nonTerminalNew.InsertProduction(newProduction);
							for (int k=1;k<production.size();k++){
								nonTerminalNew.productions.lastElement().add(production.elementAt(k));
							}
						}
					}
					x++;
				}
			}
			if (rekursive){
				for (int y=0; y<nonTerminalCurrent.productions.size();y++){
					if (!bitmap[y]){
						nonTerminalNew.InsertProduction(nonTerminalCurrent.productions.elementAt(y));
					}
				}
				grammarMod.add(nonTerminalNew);
			}
			else{
				grammarMod.add(nonTerminalCurrent);
			}
		}
		return grammarMod;
	}

	/*
	 * Eliminates direct leftrekursions be introducing new Nonterminal
	 * <"N"1>
	 * 
	 * Input: Void
	 * Return: Void
	 */
	private Vector <Productions> eliminateDirectLeftRekursion(Vector <Productions> grammar){
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
		return grammarMod;
	}
	
	private Map<String, Vector<Vector<String>>> buildGrammarMap(Vector<Productions> grammar) {
		Map<String, Vector<Vector<String>>> gMap = new LinkedHashMap<String, Vector<Vector<String>>>();
		for (Productions p : grammar) {
			gMap.put(p.getHead(), p.productions);
		}
		return gMap;
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
	
}

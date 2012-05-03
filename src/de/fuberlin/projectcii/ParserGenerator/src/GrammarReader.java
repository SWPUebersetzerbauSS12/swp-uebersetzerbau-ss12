import java.io.*;
import java.util.*;

/* 
 * GrammarReader reads a Grammar in BNF from a given file
 * 
 * Author: Patrick Schlott
 * 
 */
public class GrammarReader {

	// Path to the file
	File file; 
	
	/*
	 * Input: String containing the path of the file
	 */
	public GrammarReader(String file){
		this.file = new File(file);
	}
	
	/*
	 * Reads the file from the given file in BNF
	 * and Converts it to a Vector of Nonterminals
	 * 
	 * Input:()
	 * Returnvalue: Vector<Nonterminals> containing the read grammar
	 */
	
	public Vector<Productions> ReadFile(){
		
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
	
}

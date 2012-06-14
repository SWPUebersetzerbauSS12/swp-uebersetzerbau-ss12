package de.fuberlin;

import java.util.HashMap;

import de.fuberlin.commons.lexer.ILexer;

import de.fuberlin.commons.parser.IParser;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projectci.lrparser.LRParser;

import de.fuberlin.optimierung.LLVM_Optimization;



class Main {

	
	/*
	 * Konstanten für die Parameter
	 */	
	// Parser
	static final String PARAM_LR_PARSER = "-lr"; // benutzt den LR Parser
	static final String PARAM_LL_PARSER = "-ll"; // benutzt den LL Parser
	// Lexer
	static final String PARAM_BI_LEXER  = "-bi"; // benutzt die indirekte Umwandlung
	static final String PARAM_BII_LEXER = "-bii"; // benutzt die direkte Umwandlung
	// Allgemein
	static final String PARAM_SOURCE_FILE = "-f"; // Gibt den Pfad zum Quellprogramm an
	
	/*
	 * Standard Parameter
	 */
	static final String DEFAULT_GRAMMAR_FILE = "/input/parserci/quellsprache_bnf.txt";
	
	/*
	 * Ideen für Konsolenparameter:
	 * Datei für Programm in Quellsprache: 	-f "/path/to/inputProgram"
	 * Lexer von Gruppe bi verwenden:		-bi
	 * Lexer von Gruppe bii verwenden:		-bii
	 * Parser von Gruppe ci verwenden:		-lr [Optional]"/path/to/bnfGrammar"
	 * Parser von Gruppe cii verwenden:		-ll [Optional]"/path/to/bnfGrammar"
	 * 
	 * Standardkonfiguration: -f "irgendeinBeispielprogramm" -bi -lr
	 */
	
	public static void main(String args[]) {
		System.out.println("Hier die Code-Schnipsel einfuegen!");
		
		HashMap<String,String> arguments = readParams(args);
		
		// path of input-program
		String inputFile = arguments.get(PARAM_SOURCE_FILE);		// -f "/path/to/inputProgram"
		if( inputFile == null )						// no -f or -f without path
			inputFile = "/path/to/exampleInputProgram";
		
		
		//--------------------------
		/*
		 *	Lexer
		 *	input:
		 *	output:
		 */
		ILexer lexer = null;
		
		if( arguments.containsKey(PARAM_BII_LEXER) ){		// -bii
			
			// Codeschnipsel von bii
			
		} else {									// [-bi]
			
			// Codeschnipsel von bi
			
		}
		//--------------------------
		
		
		//--------------------------
		/*
		 *	Parser
		 *	input:	ILexer lexerObject, String grammarFilePath
		 *	output:	ISyntaxTreee parseTree
		 */
		ISyntaxTree parseTree = null;
		if( arguments.get(PARAM_LR_PARSER) != null ){			// -lr "/path/to/bnfGrammar"
			IParser parser = new LRParser();
			parseTree = parser.parse(lexer, arguments.get(PARAM_LR_PARSER));
			
		} else if( arguments.containsKey(PARAM_LL_PARSER) ) {	// -ll ["/path/to/bnfGrammar"]
			
			// Codeschnipsel von cii
			
		} else {									// -lr or no explicit parameter for parser
			//FIXME Dieser Fall wird niemals eintreten!
			IParser parser = new LRParser();
			parseTree = parser.parse(lexer, DEFAULT_GRAMMAR_FILE);
		}

		//--------------------------



		String llvm_code = "";	// Hier der generierte LLVM-Code


		//--------------------------
		/*
		 *	Optimierung
		 *	input:	String llvm_code
		 *	output:	String optimized_llvm_code
		 */
		LLVM_Optimization llvm_optimizer = new LLVM_Optimization();

		String optimized_llvm_code = llvm_optimizer.optimizeCodeFromString(llvm_code);	// Muss angepasst werden
		//--------------------------
	}
	
	// alle Parameter, die mit "-" beginnen als Key benutzen und eventueller 
	// Folgeparameter(vielleicht nur mit Anführungszeichen akzeptieren) als Value
	// was nicht diesem Schema entspricht, ignorieren
	private static HashMap<String,String> readParams(String args[]){
		HashMap<String,String> arguments = new HashMap<String,String>();
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase(PARAM_SOURCE_FILE)) {
				// Es wurde ein -f gelesen, erwarte nun Quelldatei
				System.out.println("Para: -f");
				if(i+1 < args.length && !args[i+1].trim().startsWith("-")) { // Eine Quelldatei wurde angegeben
					System.out.println("-f Option: "+args[i+1].trim());
					arguments.put(PARAM_SOURCE_FILE, args[i+1].trim());
					i++;
				} else {
					// Keine Quelldatei angeben
					System.out.println("Keine Quelldatei angegeben!");
					arguments.put(PARAM_SOURCE_FILE, null);
				}
			} else if(args[i].equalsIgnoreCase(PARAM_BI_LEXER) || args[i].equalsIgnoreCase(PARAM_BII_LEXER)) {
				String lexerType = args[i];
				System.out.println("Benutze "+lexerType+" Lexer");
				// Mehrfacheinträge vermeiden
				arguments.remove(PARAM_BI_LEXER);
				arguments.remove(PARAM_BII_LEXER);
				
				arguments.put(lexerType, null);
				
			}  else if(args[i].equalsIgnoreCase(PARAM_LL_PARSER) || args[i].equalsIgnoreCase(PARAM_LR_PARSER)) {
				// Der Parser wird festgelegt  Prüfe, ob eine Grammatik Datei eingelesen werden soll.
				String parserType = args[i];
				// Mehrfacheinträge vermeiden
				arguments.remove(PARAM_LR_PARSER);
				arguments.remove(PARAM_LL_PARSER);
				
				System.out.println("Benutze "+parserType+" Parser");
				if(i+1 < args.length && !args[i+1].trim().startsWith("-")) {
					String pathToGrammar = args[i+1].trim();
					System.out.println("Benutze Grammatik: "+pathToGrammar);
					arguments.put(parserType, pathToGrammar);
					i++;
				} else {
					// Benutze Default Grammatik
					System.out.println("Benutze Default Grammatik: "+DEFAULT_GRAMMAR_FILE);
					arguments.put(parserType, DEFAULT_GRAMMAR_FILE);
				}
			} else {
				System.err.println("Unbekannte Option: "+args[i]);
			}
		}

		return arguments;
	}
}
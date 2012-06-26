package de.fuberlin;

import java.io.File;
import java.util.HashMap;

import de.fuberlin.bii.lexergen.BuilderType;
import de.fuberlin.bii.lexergen.Lexergen;
import de.fuberlin.bii.lexergen.LexergeneratorException;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;
import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.IParser;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.projectci.lrparser.LRParser;



class Main {

	
	/*
	 * Konstanten für die Parameter
	 */	
	// Parser
	static final String PARAM_LR_PARSER = "-lr"; // benutzt den LR Parser
	static final String PARAM_LL_PARSER = "-ll"; // benutzt den LL Parser
	// Lexer
	static final String PARAM_DEF_FILE = "-d"; // Gibt den Pfad zur Datei mit den regulären Definitionen an
	static final String PARAM_BI_LEXER  = "-bi"; // benutzt die indirekte Umwandlung
	static final String PARAM_BII_LEXER = "-bii"; // benutzt die direkte Umwandlung
	static final String PARAM_REBUILD_DFA = "-rb"; //Gibt an, dass der DFA neu erstellt werden soll
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
		boolean rebuildDFA = arguments.containsKey(PARAM_REBUILD_DFA);
		
		// path of input-program
		String defFile = arguments.get(PARAM_DEF_FILE); 			// -d "/path/to/definitionFile"
		if( defFile == null )						// no -d or -d without path
			defFile = "input/de/fuberlin/bii/de/tokendefinition.rd";
		String inputFile = arguments.get(PARAM_SOURCE_FILE);		// -f "/path/to/inputProgram"
		if( inputFile == null )						// no -f or -f without path
			inputFile = "/path/to/exampleInputProgram";
		
		
		//--------------------------
		/*
		 *	Lexer
		 *	input: Pfad zu der Datei mit den regulären Definitionen und Pfad zu der Programmdatei
		 *	output: IToken-Objekt beim aufruf von getNextToken
		 */
		ILexer lexer = null;
		
		if( arguments.containsKey(PARAM_BII_LEXER) ){		// -bii			
			try {
				lexer = new Lexergen(new File(defFile), new File(inputFile), BuilderType.directBuilder, CorrectionMode.PANIC_MODE, rebuildDFA);
			} catch (LexergeneratorException e) {
				e.printStackTrace();
			}			
		} else {									// [-bi]			
			try {
				lexer = new Lexergen(new File(defFile), new File(inputFile), BuilderType.indirectBuilder, CorrectionMode.PANIC_MODE, rebuildDFA);
			} catch (LexergeneratorException e) {
				e.printStackTrace();
			}			
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
			} else if(args[i].equalsIgnoreCase(PARAM_DEF_FILE)) {
				// Es wurde ein -d gelesen, erwarte nun Quelldatei
				System.out.println("Para: -d");
				if(i+1 < args.length && !args[i+1].trim().startsWith("-")) { // Eine Quelldatei wurde angegeben
					System.out.println("-f Option: "+args[i+1].trim());
					arguments.put(PARAM_DEF_FILE, args[i+1].trim());
					i++;
				} else {
					// Keine Quelldatei angeben
					System.out.println("Keine Definitionsdatei angegeben!");
					arguments.put(PARAM_DEF_FILE, null);
				}			
			} else if(args[i].equalsIgnoreCase(PARAM_BI_LEXER) || args[i].equalsIgnoreCase(PARAM_BII_LEXER)) {
				String lexerType = args[i];
				System.out.println("Benutze "+lexerType+" Lexer");
				// Mehrfacheinträge vermeiden
				arguments.remove(PARAM_BI_LEXER);
				arguments.remove(PARAM_BII_LEXER);
				
				arguments.put(lexerType, null);
			} else if(args[i].equalsIgnoreCase(PARAM_REBUILD_DFA)) {
				String rebuild = args[i];
				// Mehrfacheinträge vermeiden
				arguments.remove(PARAM_REBUILD_DFA);
								
				arguments.put(rebuild, null);				
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
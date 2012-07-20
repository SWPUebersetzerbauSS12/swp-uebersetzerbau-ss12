package de.fuberlin;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import de.fuberlin.bii.lexergen.BuilderType;
import de.fuberlin.bii.lexergen.Lexergen;
import de.fuberlin.bii.lexergen.LexergeneratorException;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.AbstractSyntaxTree;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;
import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.commons.parser.IParser;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_OptimizationException;
import de.fuberlin.projectF.CodeGenerator.CodeGenerator;
import de.fuberlin.projecta.analysis.DebuggingHelper;
import de.fuberlin.projecta.analysis.SemanticAnalyzer;
import de.fuberlin.projecta.analysis.SemanticException;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projecta.parser.ParseException;
import de.fuberlin.projecta.parser.Parser;
import de.fuberlin.projectci.lrparser.LRParserMain;
import de.fuberlin.projectcii.ParserGenerator.src.LL1Parser;

public class Main {

	
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
	// Codegenerierung
	static final String PARAM_ASM_TYPE = "-asmType"; // "gnu" oder "intel" waehlt den Assemblertyp. Standard ist "gnu"
	// Allgemein
	static final String PARAM_SOURCE_FILE = "-f"; // Gibt den Pfad zum Quellprogramm an
	static final String PARAM_OUTPUT_FILE = "-o"; // Gibt den Pfad zur Ausgabedatei an
	static final String PARAM_LLVM_INPUT_FILE = "-llvm"; // Gibt den Pfad zur LLVM Quelldatei an
	static final String PARAM_LOG_LEVEL_CONSOLE = "-logLevelConsole"; // Gibt den zu verwendenen LogLevel für die Console an
	static final String PARAM_LOG_LEVEL_FILE = "-logLevelFile"; // Gibt den zu verwendenen LogLevel für die Console an
	static final String PARAM_LOG_FILE = "-logFile"; // Gibt den Pfad für eine Logdatei an
	
	// Standard Parameter
	static final String DEFAULT_GRAMMAR_FILE = "input/de/fuberlin/projectci/non-ambigous.txt";
	static final String DEFAULT_TOKEN_DEFINITION_FILE = "input/de/fuberlin/bii/def/tokendefinition.rd";
	static final String DEFAULT_SOURCE_FILE = "input/de/fuberlin/common/default.src";
	
	// interne Daten
	static String generatedLLVMCode = "";

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
		HashMap<String,String> arguments = readParams(args);
		initLogging(arguments);

		boolean success = runFrontend(arguments);
		if (!success) {
			System.out.println("Failed to run frontend. Stop.");
			return;
		}
		
		success = runBackend(arguments);
		if (!success) {
			System.out.println("Failed to run backend. Stop.");
		}
	}

	
	private static void initLogging(HashMap<String,String> arguments){
		Level logLevelConsole=null;
		Level logLevelFile=null;
		File logFile=null;
		
		String strLogLevelConsole=arguments.get(PARAM_LOG_LEVEL_CONSOLE);
		if (strLogLevelConsole!=null){
			try {
				logLevelConsole=Level.parse(strLogLevelConsole);
			} catch (IllegalArgumentException e) {
				System.err.println("Failed to parse param "+PARAM_LOG_LEVEL_CONSOLE +": "+strLogLevelConsole);				
			}
		}
		String strLogLevelFile=arguments.get(PARAM_LOG_LEVEL_FILE);
		if (strLogLevelFile!=null){
			try {
				logLevelFile=Level.parse(strLogLevelFile);
			} catch (IllegalArgumentException e) {
				System.err.println("Failed to parse param "+PARAM_LOG_LEVEL_FILE +": "+strLogLevelFile);				
			}
		}
		String logFilePath=arguments.get(PARAM_LOG_FILE);
		if (logFilePath!=null){
			logFile=new File(logFilePath);
//			if (!logFile.canWrite()){
//				System.err.println("Cannot write to log file configuted by param "+PARAM_LOG_FILE +": "+logFilePath);	
//				logFile=null;
//			}
		}
		LogFactory.init(logLevelConsole, logLevelFile, logFile!=null?logFile.getAbsolutePath():null);
	}
	
	/**
	 * Frontend-Phase
	 * 
	 * Eingabe: Input-File
	 * Ausgabe: Generierter LLVM-Code
	 * 
	 * @see generatedLLVMCode
	 */
	public static boolean runFrontend(HashMap<String,String> arguments) {
		System.out.println("Starting frontend Phase");

		// -d "/path/to/definitionFile"
		String defFile = arguments.get(PARAM_DEF_FILE);
		if( defFile == null )
			defFile = DEFAULT_TOKEN_DEFINITION_FILE; // fall-back
		
		// -f "/path/to/inputProgram"
		String inputFile = arguments.get(PARAM_SOURCE_FILE);
		if (inputFile == null || inputFile.isEmpty()) {
			System.out.println("Warning: No input file! Use default input file");
			inputFile = DEFAULT_SOURCE_FILE;			
		}

		//--------------------------
		/*
		 *	Lexer
		 *	input: Pfad zu der Datei mit den regulären Definitionen und Pfad zu der Programmdatei
		 *	output: IToken-Objekt beim aufruf von getNextToken
		 */
		File file = new File(inputFile);
		if (!file.canRead()) {
			System.out.println("Error: Invalid file: " + file.getAbsolutePath());
			return false;
		}

		ILexer lexer = null;
		final boolean rebuildDFA = arguments.containsKey(PARAM_REBUILD_DFA);
		
		// Lexer from bii
		if( arguments.containsKey(PARAM_BII_LEXER) ) {
			try {
				lexer = new Lexergen(new File(defFile), file, BuilderType.directBuilder, CorrectionMode.PANIC_MODE, rebuildDFA);
			} catch (LexergeneratorException e) {
				e.printStackTrace();
				return false;
			}

		// Lexer from bi
		} else if (arguments.containsKey(PARAM_BI_LEXER)) {
			try {
				lexer = new Lexergen(new File(defFile), file, BuilderType.indirectBuilder, CorrectionMode.PANIC_MODE, rebuildDFA);
			} catch (LexergeneratorException e) {
				e.printStackTrace();
				return false;
			}			
		}

		// Lexer from projecta, default
		else {
			ICharStream stream = new FileCharStream(inputFile);
			lexer = new Lexer(stream);
		}
		//--------------------------


		//--------------------------
		/*
		 *	Parser
		 *	input:	ILexer lexerObject, String grammarFilePath
		 *	output:	ISyntaxTreee parseTree
		 */
		IParser parser = null;
		String grammarFile = "";
		// LR-Parser, -lr "/path/to/bnfGrammar"
		if( arguments.get(PARAM_LR_PARSER) != null ){
			parser = new LRParserMain();
			grammarFile = arguments.get(PARAM_LR_PARSER);
		// LL-Parser, -ll ["/path/to/bnfGrammar"]
		} else if( arguments.containsKey(PARAM_LL_PARSER) ) {
			parser = new LL1Parser();
			grammarFile = arguments.get(PARAM_LL_PARSER);
		// Parser from projecta, default
		} else {
			parser = new Parser();
		}

		ISyntaxTree parseTree = null;
		try {
			parseTree = parser.parse(lexer, grammarFile);
		} catch (ParseException e) { 
			System.out.println(e.getMessage() +
					" (error at line: " + e.getLineNumber() +
					", column: " + e.getOffset() +
					", token: \"" + e.getText() + "\")"
			);
			System.out.println("Details:");
			System.out.println(e.getDetails());
		}

		if (parseTree == null) {
			System.out.println("Failed to parse!");
			return false;
		}
		//--------------------------

		//--------------------------
		/*
		 * Semantic analysis
		 * input:	Parse tree
		 * inter:	Abstract Syntax Tree (AST)
		 * output:	LLVM-Code
		 */
		SemanticAnalyzer analyzer = new SemanticAnalyzer(parseTree);
		
		try {
			analyzer.analyze();
		} catch (SemanticException e) {
			System.out.println("\nError: Failed to parse.");
			System.out.println(e.getMessage());

			IToken token = DebuggingHelper.extractPosition(e.getNode());
			if (token != null)
				System.out.println("Error near: '" + token.getText() + "' near line: " + token.getLineNumber() + ", column: " + token.getOffset());

			// abort
			return false;
		}

		// debug
		System.out.println();
		analyzer.getAST().printTree();

		// Generate LLVM-Code
		generatedLLVMCode = analyzer.getAST().genCode();
		return true;
	}

	/**
	 * Backend-Phase
	 * 
	 * Eingabe: Generierte LLVM Code
	 * Ausgabe: Maschinencode
	 */
	public static boolean runBackend(HashMap<String,String> arguments) {
		System.out.println("Starting backend Phase");

		//--------------------------
		/*
		 *	Optimierung
		 *	input:	String llvm_code
		 *	output:	String optimized_llvm_code
		 */
		
		System.out.println(generatedLLVMCode);
		
		String optimized_llvm_code = "";
		LLVM_Optimization llvm_optimizer = new LLVM_Optimization();
		try{
			if(arguments.containsKey(PARAM_LLVM_INPUT_FILE)) {
				optimized_llvm_code = llvm_optimizer.optimizeCodeFromFile(arguments.get(PARAM_LLVM_INPUT_FILE));
			} else{
				optimized_llvm_code = llvm_optimizer.optimizeCodeFromString(generatedLLVMCode);
			}
			System.out.println(optimized_llvm_code);
		}catch (LLVM_OptimizationException e){
			// Fehlermeldung anzeigen
			System.err.println("; OPTIMIZATION-ERROR: " + e.getMessage());
			
			// Nutze nicht optimierten Code
			optimized_llvm_code = generatedLLVMCode;
		}catch(Exception e){
			System.err.println(e.getMessage());
			System.err.println("Optimization not done!\nUse unoptimized code!\n");
			
			// Nutze nicht optimierten Code
			optimized_llvm_code = generatedLLVMCode;
		}
		
		//--------------------------
		
		
		//--------------------------
		/*
		 * Codegenerierung
		 * input : String llvm_code
		 * output: String machineCode 
		 */
		boolean debug = true;
		boolean guiFlag = false;
		String outputFile = null;
		String configFile = "mc_config.cfg";
		if(arguments.containsKey(PARAM_OUTPUT_FILE)) {
			outputFile = arguments.get(PARAM_OUTPUT_FILE);
		}
		boolean exec = false;
		String asmType = "gnu";
		if(arguments.containsKey(PARAM_ASM_TYPE)) {
			asmType = arguments.get(PARAM_ASM_TYPE);
		}
		
		
		// TODO Der Assemblertyp ('gnu' oder 'intel') (siehe de.fuberlin.projectF.CodeGenerator.Translator) sollte über die Kommandozeile definierbar sein können		
		String machineCode = CodeGenerator.generateCode(optimized_llvm_code, asmType, debug, guiFlag);

		if (outputFile != null) {
			CodeGenerator.writeFile(exec, outputFile, machineCode);
		}
		
		if (exec) {
			CodeGenerator.exec(outputFile, configFile);
		}

		//--------------------------
		return true;
	}
	
	
	// alle Parameter, die mit "-" beginnen als Key benutzen und eventueller 
	// Folgeparameter(vielleicht nur mit Anführungszeichen akzeptieren) als Value
	// was nicht diesem Schema entspricht, ignorieren
	private static HashMap<String,String> readParams(String args[]){
		HashMap<String,String> arguments = new HashMap<String,String>();
		
		for(int i = 0; i < args.length; i++) {
			if(args[i].equalsIgnoreCase(PARAM_SOURCE_FILE)) {
				// Es wurde ein -f gelesen, erwarte nun Quelldatei
				if(i+1 < args.length && !args[i+1].trim().startsWith("-")) { // Eine Quelldatei wurde angegeben
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
			} else if(args[i].equalsIgnoreCase(PARAM_LLVM_INPUT_FILE)) {
				System.out.println("Benutze LLVM Code Datei: "+args[++i]);
				arguments.put(PARAM_LLVM_INPUT_FILE, args[i]);				
			} else if(args[i].equalsIgnoreCase(PARAM_OUTPUT_FILE)) {
				System.out.println("Schreibe Maschinen Code in Datei: "+args[++i]);
				arguments.put(PARAM_OUTPUT_FILE, args[i]);
			} else if(args[i].equalsIgnoreCase(PARAM_LOG_LEVEL_CONSOLE)) {
				System.out.println("Setze ConsoleLogLevel auf : "+args[++i]);
				arguments.put(PARAM_LOG_LEVEL_CONSOLE, args[i]);
			} else if(args[i].equalsIgnoreCase(PARAM_LOG_LEVEL_FILE)) {
				System.out.println("Setze LogLevel für Logdatei auf : "+args[++i]);
				arguments.put(PARAM_LOG_LEVEL_FILE, args[i]);
			} else if(args[i].equalsIgnoreCase(PARAM_LOG_FILE)) {
				System.out.println("Benutze Logdatei : "+args[++i]);
				arguments.put(PARAM_LOG_FILE, args[i]);
			}
			
			else {
				System.err.println("Unbekannte Option: "+args[i]);
			}
		}

		return arguments;
	}
}
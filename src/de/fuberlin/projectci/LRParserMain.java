package de.fuberlin.projectci;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.fuberlin.bii.lexergen.BuilderType;
import de.fuberlin.bii.lexergen.Lexergen;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;
import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.IParser;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.io.FileCharStream;
import de.fuberlin.projecta.lexer.io.ICharStream;
import de.fuberlin.projectci.grammar.BNFGrammarReader;
import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.lrparser.LRParser;
import de.fuberlin.projectci.lrparser.LRParserException;


public class LRParserMain implements IParser{
	private static Logger logger = LogFactory.getLogger(LRParserMain.class);
	
	private static final String VERSION="LRParser V0.1";
	private static final String DEFAULT_GRAMMAR_FILE = "input/de/fuberlin/projectci/non-ambigous.txt";
	private static final String DEFAULT_TOKEN_DEFINITION_FILE = "input/de/fuberlin/bii/def/tokendefinition.rd";
	private static final String DEFAULT_LEXER="bi";
	private static final Level DEFAULT_LOG_LEVEL_CONSOLE = Level.INFO;
	private static final Level DEFAULT_LOG_LEVEL_FILE = Level.ALL;
	
	private ISyntaxTree syntaxTree=null;
	
	private boolean reduceToAbstractSyntaxTree=false;
	private boolean removeEpsilonNodes=true; // Der SemanticAnalyzer erwartet einen Parsebaum ohne Epsilon-Knoten
	private boolean displayParseTableGui=false;
	
	
	/**
	 * Fassade des LRParsers zur Außenwelt.
	 */
	public LRParserMain() {
	}

	/**
	 * Ermöglicht das Reduzieren des erzeugten SyntaxTree durch "Hochziehen aller Einzelkinder".
	 * @param reduceToAbstractSyntaxTree
	 */
	public void setReduceToAbstractSyntaxTree(boolean reduceToAbstractSyntaxTree) {
		this.reduceToAbstractSyntaxTree = reduceToAbstractSyntaxTree;
	}

	/**
	 * Ermöglicht das Entfernen der Epsilon-Knoten aus dem erzeugten SyntaxTree
	 * @param removeEpsilonNodes
	 */
	public void setRemoveEpsilonNodes(boolean removeEpsilonNodes) {
		this.removeEpsilonNodes = removeEpsilonNodes;
	}

	/**
	 * Öffnet eine Swing-GUI zum Darstellen der Parsetabelle mit ACTION- und GOTO-Funktionen
	 * @param displayParseTableGui
	 */
	public void setDisplayParseTableGui(boolean displayParseTableGui) {
		this.displayParseTableGui = displayParseTableGui;
	}

	/**
	 * Schreibt die XML-Repräsentation des SyntaxTree in den übergebenen StringBuffer.
	 * @param strBuf
	 */
	public void printParseTree(StringBuffer strBuf) {
		 if (syntaxTree==null){
			 throw new IllegalStateException("printParseTree must not be called before parse.");
		 }
		 strBuf.append(syntaxTree.toString());
	}
	
	/**
	 * Parst ein Eingabeprogramm für eine Grammatik und gibt den erzeugten ISyntaxTree zurück.
	 * @param lexer ILexer für das Eingabeprogramm
	 * @param grammarPath Pfad zu einer Grammtikdatei in BNF
	 * @return der resultierende Parsebaum
	 * @throws #{@link LRParserException}, falls das Parsen fehlschlägt.
	 */
	@Override
	public ISyntaxTree parse(ILexer lexer, String grammarPath) {		
		File grammarFile = new File(grammarPath);
		return parse(lexer, grammarFile);
	}
	/**
	 * Parst ein Eingabeprogramm für eine Grammatik und gibt den erzeugten ISyntaxTree zurück.
	 * @param lexer ILexer für das Eingabeprogramm
	 * @param grammarFile Grammtikdatei in BNF
	 * @return der resultierende Parsebaum
	 * @throws #{@link LRParserException}, falls das Parsen fehlschlägt.
	 */
	public ISyntaxTree parse(ILexer lexer, File grammarFile) {
		Grammar grammar = null;
		// Grammatik einlesen
		try {
			GrammarReader grammarReader=new BNFGrammarReader();
			grammar=grammarReader.readGrammar(grammarFile.getAbsolutePath());
		} 
		catch (BNFParsingErrorException e) {
			logger.log(Level.WARNING, "Failed to read grammar from file: "+grammarFile.getAbsolutePath(), e);
			throw new LRParserException("Failed to read grammar from file: "+grammarFile.getAbsolutePath(),e);
		}
		return parse(lexer, grammar);
	}
	 
	/**
	 * Parst ein Eingabeprogramm für eine Grammatik und gibt den erzeugten ISyntaxTree zurück.
	 * @param lexer ILexer für das Eingabeprogramm
	 * @param grammarReader #{@link java.io.Reader} für eine Grammtikdatei in BNF
	 * @return der resultierende Parsebaum
	 * @throws #{@link LRParserException}, falls das Parsen fehlschlägt.
	 */
	public ISyntaxTree parse(ILexer lexer, Reader grammarReader) {
		Grammar grammar = null;
		// Grammatik einlesen
		try {
			GrammarReader _grammarReader=new BNFGrammarReader();
			grammar=_grammarReader.readGrammar(grammarReader);
		} 
		catch (BNFParsingErrorException e) {
			logger.log(Level.WARNING, "Failed to read grammar from Reader.", e);
			throw new LRParserException("Failed to read grammar from Reader.",e);
		}		
		return parse(lexer, grammar);
	}
	
	/**
	 * Parst ein Eingabeprogramm für eine Grammatik und gibt den erzeugten ISyntaxTree zurück.
	 * @param lexer ILexer für das Eingabeprogramm
	 * @param grammar Grammtik
	 * @return der resultierende Parsebaum
	 * @throws #{@link LRParserException}, falls das Parsen fehlschlägt.
	 */
	private ISyntaxTree parse(ILexer lexer, Grammar grammar) {
		this.syntaxTree=null; // reset SyntaxTree
		LRParser parser=new LRParser();
		parser.setDisplayParseTableGui(displayParseTableGui);
		parser.setReduceToAbstractSyntaxTree(reduceToAbstractSyntaxTree);
		parser.setRemoveEpsilonNodes(removeEpsilonNodes);
		this.syntaxTree= parser.parse(lexer, grammar); // SyntaxTree für printParseTree speichern
		return this.syntaxTree;
	}
	/**
	 * 
	 * @return Beschreibung des Kommandozeilen-Interfaces.
	 */
	private static String usage(){
		StringBuffer strBuf=new StringBuffer();
		strBuf.append("NAME");
		strBuf.append("\n\tLRParserMain - parses a source file using the LR-parse-algorithm.");
		strBuf.append("\nSYNOPSIS");
		strBuf.append("\n\tLRParserMain [OPTIONS] <source_file>");
		strBuf.append("\nOPTIONS");
		strBuf.append("\n\t-v --version");
		strBuf.append("\n\t\tPrint version and exit.");
		strBuf.append("\n\t-h --help");
		strBuf.append("\n\t\tPrint usage and exit");
		strBuf.append("\n\t-o --print-parse-tree <target_file>");
		strBuf.append("\n\t\tPrint the resulting parse tree to <target_file>.");
		strBuf.append("\n\t-l --lexer bi|bii|a");
		strBuf.append("\n\t\tUse lexer bi, bii or a. Defaults to "+DEFAULT_LEXER);
		strBuf.append("\n\t-t --token-definitions <token_definitions_file>");
		strBuf.append("\n\t\tUse token defintions from <token_definitions_file>. Defaults to "+DEFAULT_TOKEN_DEFINITION_FILE);
		strBuf.append("\n\t-g --grammar <grammar_file>");
		strBuf.append("\n\t\tUse grammar from <grammar_file>. Defaults to "+DEFAULT_GRAMMAR_FILE);
		strBuf.append("\n\t--reduce-to-abstract-syntax-tree");
		strBuf.append("\n\t\tReduce the resulting parse tree by pulling up all single child nodes. Defaults to don't reduce");
		strBuf.append("\n\t--dont-remove-epsilon-nodes");
		strBuf.append("\n\t\tDon't remove epsilon nodes from the resulting parse tree. Defaults to remove epsilon nodes");
		strBuf.append("\n\t--displayParseTable");
		strBuf.append("\n\t\tOpen a swing gui displaying the Action and Goto table.");
		strBuf.append("\n\t--log-level-console OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST|ALL");
		strBuf.append("\n\t\tSet the logging level for console output. Defaults to "+DEFAULT_LOG_LEVEL_CONSOLE);
		strBuf.append("\n\t--log-level-file OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST|ALL");
		strBuf.append("\n\t\tSet the logging level for logfile output. Defaults to "+DEFAULT_LOG_LEVEL_FILE);
		strBuf.append("\n\t--log-file <log_file>");
		strBuf.append("\n\t\tWrite logging output to <log_file>. Defaults to no logfile.");
		return strBuf.toString();
	}
	
	/**
	 * Ermöglicht die Verwendung des LRParsers über die Kommandozeile.
	 <code>
		NAME
			LRParserMain - parses a source file using the LR-parse-algorithm.
		SYNOPSIS
			LRParserMain [OPTIONS] <source_file>
		OPTIONS
			-v --version
				Print version and exit.
			-h --help
				Print usage and exit
			-o --print-parse-tree <target_file>
				Print the resulting parse tree to <target_file>.
			-l --lexer bi|bii|a
				Use lexer bi, bii or a. Defaults to bi
			-t --token-definitions <token_definitions_file>
				Use token defintions from <token_definitions_file>. Defaults to input/de/fuberlin/bii/def/tokendefinition.rd
			-g --grammar <grammar_file>
				Use grammar from <grammar_file>. Defaults to input/de/fuberlin/projectci/non-ambigous.txt
			--reduce-to-abstract-syntax-tree
				Reduce the resulting parse tree by pulling up all single child nodes. Defaults to don't reduce
			--dont-remove-epsilon-nodes
				Don't remove epsilon nodes from the resulting parse tree. Defaults to remove epsilon nodes
			--displayParseTable
				Open a swing gui displaying the Action and Goto table.
			--log-level-console OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST|ALL
				Set the logging level for console output. Defaults to INFO
			--log-level-file OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST|ALL
				Set the logging level for logfile output. Defaults to ALL
			--log-file <log_file>
				Write logging output to <log_file>. Defaults to no logfile.
	 </code>
	 */
	public static void main(String[] args) {
		if (args.length==0){
			System.out.println(usage());
			return;
		}
		
		CommandLine commandLine=new CommandLine(args);
		if (commandLine.getValueForOption(CommandLine.Option.DisplayVersion)!=null){
			System.out.println(VERSION);
			return;
		}
		
		if (commandLine.getValueForOption(CommandLine.Option.DisplayHelp)!=null){
			System.out.println(usage());
			return;
		}
		
		if (commandLine.getArguments().size()==0){
			System.err.println("Error: Missing argument <sourceFile>");
			System.out.println(usage());
			return;
		}
		if (commandLine.getArguments().size()>1){
			System.err.println("Error: Ambigious argument <sourceFile>");
			System.out.println(usage());
			return;
		}
		
		File sourceFile= new File(commandLine.getArguments().get(0));
		if (!sourceFile.exists()){
			System.err.println("Error: <sourceFile> doesn't exists: "+commandLine.getArguments().get(0));
			return;
		}
		
		File tokenDefinitionFile=null;
		if (commandLine.getValueForOption(CommandLine.Option.TokenDefinitions)!=null){
			tokenDefinitionFile=new File((String) commandLine.getValueForOption(CommandLine.Option.TokenDefinitions));
		}
		else{
			tokenDefinitionFile=new File(DEFAULT_TOKEN_DEFINITION_FILE);
		}
		if (!tokenDefinitionFile.exists()){
			System.err.println("Error: <token_definitions_file> doesn't exists: "+tokenDefinitionFile);
			return;
		}
		
		File grammarFile=null;
		if (commandLine.getValueForOption(CommandLine.Option.Grammar)!=null){
			grammarFile=new File((String) commandLine.getValueForOption(CommandLine.Option.Grammar));
		}
		else{
			grammarFile=new File(DEFAULT_GRAMMAR_FILE);
		}
		if (!grammarFile.exists()){
			System.err.println("Error: <grammar_file> doesn't exists: "+grammarFile);
			return;
		}
		
		initLogging(commandLine);
		
		String strLexer=(String) commandLine.getValueForOption(CommandLine.Option.Lexer);
		if (strLexer==null) strLexer=DEFAULT_LEXER;		
		ILexer lexer=null;
		boolean rebuildDFA=false;
		if ("bi".equals(strLexer)){
			lexer = new Lexergen(tokenDefinitionFile, sourceFile, BuilderType.indirectBuilder, CorrectionMode.PANIC_MODE, rebuildDFA);
		}
		else if ("bii".equals(strLexer)){
			lexer = new Lexergen(tokenDefinitionFile, sourceFile, BuilderType.directBuilder, CorrectionMode.PANIC_MODE, rebuildDFA);
		}
		else if ("a".equals(strLexer)){
			ICharStream stream = new FileCharStream(sourceFile.getAbsolutePath());
			lexer = new Lexer(stream);
		}
		else{
			System.err.println("Error: Unknown lexer "+strLexer);
			System.out.println(usage());
			return;
		}
						
		LRParserMain parserMain=new LRParserMain();
		
		if (commandLine.getValueForOption(CommandLine.Option.DisplayParseTable)!=null){
			parserMain.setDisplayParseTableGui(true);
		}
		if (commandLine.getValueForOption(CommandLine.Option.ReduceToAbstractSyntaxTree)!=null){
			parserMain.setReduceToAbstractSyntaxTree(true);
		}
		if (commandLine.getValueForOption(CommandLine.Option.DontRemoveEpsilonNodes)!=null){
			parserMain.setRemoveEpsilonNodes(false);
		}
		
		ISyntaxTree syntaxTree=parserMain.parse(lexer, grammarFile);
		if (syntaxTree==null){
			System.err.println("Error: Failed to parse "+sourceFile);
			return;
		}
		
		if (commandLine.getValueForOption(CommandLine.Option.PrintParseTree)!=null){
			String outputFilePath=(String) commandLine.getValueForOption(CommandLine.Option.PrintParseTree);
			try {
			    BufferedWriter out = new BufferedWriter(new FileWriter(outputFilePath));
			    out.write(syntaxTree.toString());
			    out.close();
			} catch (IOException e) {
				System.err.println("Error: Failed to write parsetree to file "+outputFilePath);
				e.printStackTrace();
				return;
			}
		}
		System.out.println("OK");
	}
	
	/** Initialisiert das Logging anhand der Kommandozeilen-Argumente */
	private static void initLogging(CommandLine commandLine){
		Level logLevelConsole=DEFAULT_LOG_LEVEL_CONSOLE;
		Level logLevelFile=DEFAULT_LOG_LEVEL_FILE;
		File logFile=null;
		
		String strLogLevelConsole=(String) commandLine.getValueForOption(CommandLine.Option.LogLevelConsole);
		if (strLogLevelConsole!=null){
			try {
				logLevelConsole=Level.parse(strLogLevelConsole);
			} catch (IllegalArgumentException e) {
				System.err.println("Error: Invalid log level for console: "+strLogLevelConsole+". Use default: "+logLevelConsole);				
			}
		}
		String strLogLevelFile=(String) commandLine.getValueForOption(CommandLine.Option.LogLevelFile);
		if (strLogLevelFile!=null){
			try {
				logLevelFile=Level.parse(strLogLevelFile);
			} catch (IllegalArgumentException e) {
				System.err.println("Error: Invalid log level for logfile: "+strLogLevelFile+". Use default: "+logLevelFile);				
			}
		}
		String logFilePath=(String) commandLine.getValueForOption(CommandLine.Option.LogFile);
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
	 * Hilfsklasse zum Parsen der Kommandozeilen-Argumente.
	 */
	private static class CommandLine{
		private Map<Option,Object> option2Value= new HashMap<Option, Object>();
		private List<String> arguments=new ArrayList<String>();
		
		public CommandLine(String[] args){
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				if (Option.pattern2Option.containsKey(arg)){
					Option option=Option.pattern2Option.get(arg);
					if (option.defaultValue!=null){
						option2Value.put(option, option.defaultValue);
					}
					else{
						if (i<args.length-1){ // nicht das letzte Argument
							String value=args[++i];
							if ((value.startsWith("-") || value.startsWith("--"))){
								throw new CommandLineParseException("Illegal value ('"+value+"' for option "+option);
							}
							option2Value.put(option,value);
						}
						else{
							throw new CommandLineParseException("Missing value for option "+option);
						}
					}
				}
				else{
					arguments.add(arg);
				}
			}
		}
		
		public Object getValueForOption(Option option){
			return option2Value.get(option);
		}
		
		public List<String> getArguments(){
			return arguments;
		}
		
		/**
		 * Beschreibung der erlaubten Kommandozeilen-Optionen
		 * 
		 */
		private static enum Option{
			DisplayVersion("--version", "-v", true),
			DisplayHelp("--help", "-h", true),
			PrintParseTree("--print-parse-tree","-o", null),
			Lexer("--lexer", "-l", null),
			TokenDefinitions("--token-definitions", "-t", null),
			Grammar("--grammar", "-g" , null),
			ReduceToAbstractSyntaxTree("--reduce-to-abstract-syntax-tree", null, true),
			DontRemoveEpsilonNodes("--dont-remove-epsilon-nodes", null, true),
			DisplayParseTable("--displayParseTable", null, true),
			LogLevelConsole("--log-level-console", null, null),
			LogLevelFile("--log-level-file", null, null),
			LogFile("--log-file", null, null);
			
			private final String pattern;
			private final String aliasPattern;
			private final Object defaultValue;
			
			/**
			 * Erzeugt eine neue Kommandozeilen-Option
			 * @param pattern Hauptpattern für die Kommandozeilen-Option
			 * @param alias Alternativpattern für die Kommandozeilen-Option
			 * @param defaultValue Default-Wert für Switches (Null, wenn zur Option noch ein Wert übergeben werden muss).
			 */
			private Option(String pattern, String alias, Object defaultValue){
				this.pattern=pattern;
				this.aliasPattern=alias;
				this.defaultValue=defaultValue;
			}
			
			private static Map<String,Option> pattern2Option= new HashMap<String, Option>();

			static{
				for(Option anOption : EnumSet.allOf(Option.class)){
					pattern2Option.put(anOption.pattern, anOption);
					if (anOption.aliasPattern!=null){
						pattern2Option.put(anOption.aliasPattern, anOption);
					}
				}
			}
		}
		/** Signalisiert einen Fehler in den Kommandozeilen-Argumenten */
		@SuppressWarnings("serial")
		private static class CommandLineParseException extends RuntimeException{
			public CommandLineParseException(String message) {
				super(message);				
			}			
		}
	}
}
 

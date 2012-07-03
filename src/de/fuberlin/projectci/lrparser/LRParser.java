package de.fuberlin.projectci.lrparser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.fuberlin.commons.parser.IParser;
import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.Production;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.parseTable.InvalidGrammarException;
import de.fuberlin.projectci.parseTable.ParseTable;
import de.fuberlin.projectci.parseTable.ParseTableBuilder;

public class LRParser implements IParser {
	private static Logger logger = LogFactory.getLogger(LRParser.class);
	
	private Grammar grammar;
	private ParseTable parseTable;
	
	// TODO Konstruktoren evtl ganz weglassen und in z.B. parse Methode überprüfen, 
	// ob erzeugte Grammatik die selbe ist wie die zuvor benutzte und dann das Erstellen
	// der Parsetabelle weglassen
	public LRParser() {
		grammar = null;
		parseTable = null;
	}

	public LRParser(File grammarFile) throws BNFParsingErrorException, InvalidGrammarException {
		
		// Grammatik einlesen
		try {
			GrammarReader grammarReader=new GrammarReader();
			this.grammar=grammarReader.readGrammar(grammarFile.getAbsolutePath());
		} 
		catch (BNFParsingErrorException e) {
			logger.log(Level.WARNING, "Failed to read grammar from file: "+grammarFile.getAbsolutePath(), e);
			throw e;
		}
		// Grammatik erweitern
		extendGrammar(this.grammar);
		
		// ParseTable erstellen
		ParseTableBuilder parseTableBuilder=ParseTableBuilder.createParseTableBuilder(grammar);
		try {
			this.parseTable=parseTableBuilder.buildParseTable();
		} 
		catch (InvalidGrammarException e) {
			logger.log(Level.WARNING, "Failed to build ParseTable for grammar from file: "+grammarFile.getAbsolutePath(), e);
			throw e;
		}
	}
	
	/*
	public ISyntaxTree parse(ILexer lexer) {
		Driver driver=new Driver();
		return driver.parse(lexer, grammar, parseTable);
	}
	*/
	 
	/**
	 * Erweitert die übergebene Grammatik mit einem neuen Startsymbol 
	 * und einer neuen Produktion vom neuen Startsymbol auf das alte Startsymbol.
	 * XXX Grammatik als Argument evtl weglassen, weil man im Konstruktor sowieso eine Grammatik übergeben muss, oder vllt Methode statisch?
	 * @param grammar eine Grammatik, die erweitert werden soll
	 */
	public void extendGrammar(Grammar grammar){
		// alle Namen der Nichtterminale aus der Grammatik holen
		Set<String> nonTerminalsNames = grammar.getAllNonterminalNames();
		
		NonTerminalSymbol oldStartSymbol = grammar.getStartSymbol();
		
		String freeName = null;
		
		// zuerst folgende Namen für neues Startsymbol probieren
		String[] firstChoiceStartSymbols = new String[]{"S0","S'","Start","Startsymbol","start"};
		for(String s : firstChoiceStartSymbols){
			if(!nonTerminalsNames.contains(s)){
				freeName = s;
				break;
			}
		}
		
		// ansonsten Zufallsname "S"+TIMESTAMP probieren
		while(freeName == null){
			String s = "S"+System.currentTimeMillis();
			if(!nonTerminalsNames.contains(s))
				freeName = s;
		}
		
		// neues Nichtterminal erzeugen und als Startsymbol festlegen
		NonTerminalSymbol startSymbol = grammar.createNonTerminalSymbol(freeName);
		grammar.setStartSymbol(startSymbol);
		
		// neue Produktion vom neuen zum alten Startsymbol anlegen
		List<Symbol> rhs = new LinkedList<Symbol>();
		rhs.add(oldStartSymbol);
		Production production = new Production(startSymbol, rhs);
		grammar.addProduction(production);
	}

	@Override
	public ISyntaxTree parse(ILexer lexer, String grammarPath) {
		File grammarFile = new File(grammarPath);
		
		/////////////// copy&paste vom Konstruktor (Exceptions unterdrückt)
		// Grammatik einlesen
		try {
			GrammarReader grammarReader=new GrammarReader();
			this.grammar=grammarReader.readGrammar(grammarFile.getAbsolutePath());
		} 
		catch (BNFParsingErrorException e) {
			logger.log(Level.WARNING, "Failed to read grammar from file: "+grammarFile.getAbsolutePath(), e);
			// throw e; // TODO Exception werfen
		}
		// Grammatik erweitern
		extendGrammar(this.grammar);
		
		// ParseTable erstellen
		ParseTableBuilder parseTableBuilder=ParseTableBuilder.createParseTableBuilder(grammar);
		try {
			this.parseTable=parseTableBuilder.buildParseTable();
		} 
		catch (InvalidGrammarException e) {
			logger.log(Level.WARNING, "Failed to build ParseTable for grammar from file: "+grammarFile.getAbsolutePath(), e);
			// throw e; // TODO Exception werfen
		}
		/////////////// copy&paste vom Konstruktor (Exceptions unterdrückt)
		
		
		/////////////// copy&paste von alter parse Methode
		Driver driver=new Driver();
		ISyntaxTree syntaxTree= driver.parse(lexer, grammar, parseTable);
		
		if (syntaxTree==null){
			logger.warning("LRParser failed.");
			// TODO Besser den Driver eine Exception werfen lassen und als (LR)ParserException weiterreichen
			throw new RuntimeException("LRParser failed.");
		}
		// Der SemanticAnalyzer erwartet einen Parsebaum ohne Epsilon-Knoten
		((SyntaxTreeNode)syntaxTree).removeAllEpsilonNodes();
		return syntaxTree;
	}


}
 

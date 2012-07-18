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
import de.fuberlin.projectci.grammar.BNFGrammarReader;
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
	
	
	public LRParser() {
	}

	/**
	 * Erweitert die übergebene Grammatik mit einem neuen Startsymbol 
	 * und einer neuen Produktion vom neuen Startsymbol auf das alte Startsymbol.	
	 * @param grammar eine Grammatik, die erweitert werden soll
	 */
	private void extendGrammar(Grammar grammar){
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
		
		// neue Produktion vom neuen zum alten Startsymbol anlegen
		List<Symbol> rhs = new LinkedList<Symbol>();
		rhs.add(oldStartSymbol);
		Production production = new Production(startSymbol, rhs);
		grammar.addProduction(production);
		
		grammar.setStartSymbol(startSymbol);
	}

	@Override
	public ISyntaxTree parse(ILexer lexer, String grammarPath) {
		logger.fine("Start parsing...");
		File grammarFile = new File(grammarPath);
		Grammar grammar = null;
		ParseTable parseTable = null;
		// Grammatik einlesen
		try {
			GrammarReader grammarReader=new BNFGrammarReader();
			grammar=grammarReader.readGrammar(grammarFile.getAbsolutePath());
		} 
		catch (BNFParsingErrorException e) {
			logger.log(Level.WARNING, "Failed to read grammar from file: "+grammarFile.getAbsolutePath(), e);
			throw new LRParserException("Failed to read grammar from file: "+grammarFile.getAbsolutePath(),e);
		}
		// Grammatik erweitern
		extendGrammar(grammar);
		
		// ParseTable erstellen
		ParseTableBuilder parseTableBuilder=ParseTableBuilder.createParseTableBuilder(grammar);
		try {
			parseTable=parseTableBuilder.buildParseTable();
		} 
		catch (InvalidGrammarException e) {
			logger.log(Level.WARNING, "Failed to build ParseTable for grammar from file: "+grammarFile.getAbsolutePath(), e);
			throw new LRParserException("Failed to build ParseTable for grammar from file: "+grammarFile.getAbsolutePath(), e);
		}
		
		Driver driver=new Driver();
		ISyntaxTree syntaxTree= driver.parse(lexer, parseTable);
		
		if (syntaxTree==null){
			logger.warning("LRParser failed.");			
			throw new LRParserException("LRParser failed.");
		}
		// Der SemanticAnalyzer erwartet einen Parsebaum ohne Epsilon-Knoten
		((SyntaxTreeNode)syntaxTree).removeAllEpsilonNodes();
		logger.info("Parsing succeed.");
		return syntaxTree;
	}
}
 

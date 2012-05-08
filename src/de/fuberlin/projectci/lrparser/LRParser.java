package de.fuberlin.projectci.lrparser;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.projectci.extern.ILexer;
import de.fuberlin.projectci.extern.ISyntaxTree;
import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.parseTable.InvalidGrammarException;
import de.fuberlin.projectci.parseTable.ParseTable;
import de.fuberlin.projectci.parseTable.ParseTableBuilder;

public class LRParser {
	private static Logger logger = LogFactory.getLogger(LRParser.class);
	
	private Grammar grammar;
	private ParseTable parseTable;

	public LRParser(File grammarFile) throws BNFParsingErrorException, InvalidGrammarException {
		
		// Grammatik einlesen
		try {
			this.grammar=GrammarReader.readGrammar(grammarFile.getAbsolutePath());
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
	
	public ISyntaxTree parse(ILexer lexer) {
		Driver driver=new Driver();
		return driver.parse(lexer, grammar, parseTable);
	}
	 
	/**
	 * Erweitert die übergebene Grammatik mit einem neuen Startsymbol 
	 * und einer neuen Produktion vom neuen Startsymbol auf das alte Startsymbol.
	 * @param grammar eine Grammatik, die erweitert werden soll
	 * @return eine erweiterte Kopie der übergebenen Grammatik
	 */
	public void extendGrammar(Grammar grammar){
		// TODO Implementiere mich.
	}
}
 

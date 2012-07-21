package de.fuberlin.projectci.test.driver;

import static org.junit.Assert.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.projecta.lexer.Lexer;
import de.fuberlin.projecta.lexer.io.StringCharStream;
import de.fuberlin.projectci.grammar.BNFGrammarReader;
import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.parseTable.InvalidGrammarException;
import de.fuberlin.projectci.parseTable.ParseTable;
import de.fuberlin.projectci.parseTable.ParseTableBuilder;
import de.fuberlin.projectci.parseTable.SLRParseTableBuilder;
import de.fuberlin.projectci.test.driver.DriverTest.DriverTestDataProvider;

/** 
 * Driver-Testdaten für das Fibonacci-Beispiel.
 *
 */
public class DriverTestDataProvider2 implements DriverTestDataProvider{
	private static Logger logger = LogFactory.getLogger(DriverTestDataProvider1.class);
	private ILexer lexer;
	private ParseTable parseTable;
	private Grammar grammar;

	public DriverTestDataProvider2() {
		super();
		setUp();
	}

	// **************************************************************************** 
	// * Implementierung von DriverTestDataProvider
	// ****************************************************************************

	@Override
	public ParseTable getParseTable() {
		return parseTable;
	}

	@Override
	public Grammar getGrammar() {
		return grammar;
	}

	@Override
	public ILexer getLexer() {
		return lexer;
	}
	
	@Override
	public ISyntaxTree expectedResult(){
		return null;
	}
	
	private void setUp(){
		
		grammar=sourceGrammar();
		ParseTableBuilder ptb=new SLRParseTableBuilder(grammar);
		try {
			parseTable=ptb.buildParseTable();
			logger.info("Valid source grammar.");
			//System.out.println(parseTable.toString());
		} catch (InvalidGrammarException e) {
			logger.log(Level.INFO, "Invalid source grammar.",e);
		}	

		String strProgram=
				"def int fib(int n){\n"+
				"  if (n <= 1) return n;\n"+
				"  else {\n"+
				"    int fib;\n"+
				"    fib=fib(n-1) + fib(n-2);\n"+
				"    return fib;\n"+
				"  }\n"+
				"}\n"+
				"\n"+
				"def int main(){\n"+
				"  int n;\n"+
				"  int fib;\n"+
				"  fib=fib(n);\n"+
				"  print fib;\n"+
				"  return fib;\n"+
				"}\n";
		lexer=new Lexer(new StringCharStream(strProgram));
	}

	private Grammar sourceGrammar(){
		GrammarReader grammarReader = new BNFGrammarReader();
		Grammar g3 = null;
		try {
			g3 = grammarReader.readGrammar("./input/de/fuberlin/projectci/non-ambigous.txt");
		} catch (BNFParsingErrorException e) {
			fail(e.getClass()+": "+e.getMessage());
		}
		return g3;
	}
	
}

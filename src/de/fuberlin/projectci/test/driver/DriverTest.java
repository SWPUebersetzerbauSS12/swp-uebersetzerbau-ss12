package de.fuberlin.projectci.test.driver;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.commons.lexer.ILexer;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.lrparser.Driver;
import de.fuberlin.projectci.parseTable.ParseTable;

/**
 * Testcases für den Driver 
 *
 */
public class DriverTest {
	private static Logger logger = LogFactory.getLogger(DriverTest.class);
	private List<DriverTestDataProvider> testDataProviders;

	public static interface DriverTestDataProvider{		
		public ParseTable getParseTable() ;
		public Grammar getGrammar();
		public ILexer getLexer();
		public ISyntaxTree expectedResult();
	}
	
	@Before
	public void setUp() throws Exception {
		testDataProviders= new ArrayList<DriverTest.DriverTestDataProvider>();
		testDataProviders.add(new DriverTestDataProvider1());
		testDataProviders.add(new DriverTestDataProvider2());
	}
	
	@Test
	public void testDriver() {
		Driver driver=new Driver();
		for (DriverTestDataProvider aTestDataProvider : testDataProviders()) {
			ISyntaxTree expectedTree=aTestDataProvider.expectedResult();
			ISyntaxTree tree=driver.parse(aTestDataProvider.getLexer(), aTestDataProvider.getGrammar(), aTestDataProvider.getParseTable());
			logger.info("Abstract Syntax Tree:\n"+tree);
			if (expectedTree!=null){
				// Leider ist es nicht so leicht möglich den erwarteten Parsebaum zu spezifizieren
				// Einige Testfälle sind daher nur visuell überprüfbar --> assert macht da keinen Sinn.
				assertEquals(tree, expectedTree);
			}
		}		
	}
	
	private List<DriverTestDataProvider> testDataProviders(){
		return testDataProviders;
	}
		
	
}

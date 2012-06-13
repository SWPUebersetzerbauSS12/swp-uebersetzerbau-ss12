package de.fuberlin.projectci.test.parseTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;
import de.fuberlin.projectci.parseTable.AcceptAction;
import de.fuberlin.projectci.parseTable.Goto;
import de.fuberlin.projectci.parseTable.InvalidGrammarException;
import de.fuberlin.projectci.parseTable.LR0Item;
import de.fuberlin.projectci.parseTable.ParseTable;
import de.fuberlin.projectci.parseTable.ReduceAction;
import de.fuberlin.projectci.parseTable.SLRParseTableBuilder;
import de.fuberlin.projectci.parseTable.ShiftAction;
import de.fuberlin.projectci.parseTable.State;
import de.fuberlin.projectci.test.driver.DriverTestDataProvider1;

public class SLRParseTableBuilderTest {
	private static Logger logger = LogFactory.getLogger(SLRParseTableBuilderTest.class);
	
	private Grammar grammar=null;
	private SLRParseTableBuilder slrParseTableBuilder=null;
	private Set<LR0Item> i0=null;
	private Set<LR0Item> i1=null;
	private Set<LR0Item> i2=null;
	private Set<LR0Item> i3=null;
	private Set<LR0Item> i4=null;
	private Set<LR0Item> i5=null;
	private Set<LR0Item> i6=null;
	private Set<LR0Item> i7=null;
	private Set<LR0Item> i8=null;
	private Set<LR0Item> i9=null;
	private Set<LR0Item> i10=null;
	private Set<LR0Item> i11=null;
	
	@Before
	public void setUp() throws Exception {
		// Testdaten aus dem Drachenbuch Kapitel 4.6 / S.294ff
		String strGrammar="" +
				"<E0> ::= <E>\n"+
				"<E>  ::= <E> \"+\" <T> | <T>\n"+
				"<T>  ::= <T> \"*\" <F> | <F>\n"+
				"<F>  ::= \"(\" <E> \")\" | \"id\"";

		GrammarReader grammarReader=new GrammarReader();
		
		try {
			grammar=grammarReader.readGrammar(new StringReader(strGrammar)); 
			grammar.setStartSymbol(grammar.getProductionAtIndex(0).getLhs()); // <E0>
			
		} catch (BNFParsingErrorException e) {
			fail(e.getClass()+": "+e.getMessage());
		}
		
		slrParseTableBuilder=new SLRParseTableBuilder(grammar);
		
		i0=new HashSet<LR0Item>();	
		i0.add(new LR0Item(grammar.getProductionAtIndex(0), 0)); // E0 --> · E
		i0.add(new LR0Item(grammar.getProductionAtIndex(1), 0)); // E --> · E "+" T
		i0.add(new LR0Item(grammar.getProductionAtIndex(2), 0)); // E --> · T
		i0.add(new LR0Item(grammar.getProductionAtIndex(3), 0)); // T --> · T "*" F
		i0.add(new LR0Item(grammar.getProductionAtIndex(4), 0)); // T --> · F
		i0.add(new LR0Item(grammar.getProductionAtIndex(5), 0)); // F --> · "(" E ")"
		i0.add(new LR0Item(grammar.getProductionAtIndex(6), 0)); // F --> · "id"
		
		i1=new HashSet<LR0Item>();	
		i1.add(new LR0Item(grammar.getProductionAtIndex(0), 1)); // E0 --> E ·
		i1.add(new LR0Item(grammar.getProductionAtIndex(1), 1)); // E --> E · "+" T
		
		i2=new HashSet<LR0Item>();	
		i2.add(new LR0Item(grammar.getProductionAtIndex(2), 1)); // E --> T ·
		i2.add(new LR0Item(grammar.getProductionAtIndex(3), 1)); // T --> T · "*" F
		
		i3=new HashSet<LR0Item>();			
		i3.add(new LR0Item(grammar.getProductionAtIndex(4), 1)); // T --> F ·
		
		i4=new HashSet<LR0Item>();	
		i4.add(new LR0Item(grammar.getProductionAtIndex(5), 1)); // F --> "(" · E ")"
		i4.add(new LR0Item(grammar.getProductionAtIndex(1), 0)); // E --> · E "+" T
		i4.add(new LR0Item(grammar.getProductionAtIndex(2), 0)); // E --> · T
		i4.add(new LR0Item(grammar.getProductionAtIndex(3), 0)); // T --> · T "*" F
		i4.add(new LR0Item(grammar.getProductionAtIndex(4), 0)); // T --> · F
		i4.add(new LR0Item(grammar.getProductionAtIndex(5), 0)); // F --> · "(" E ")"
		i4.add(new LR0Item(grammar.getProductionAtIndex(6), 0)); // F --> · "id"
		
		i5=new HashSet<LR0Item>();	
		i5.add(new LR0Item(grammar.getProductionAtIndex(6), 1)); // F --> "id" ·
				
		i6=new HashSet<LR0Item>();	
		i6.add(new LR0Item(grammar.getProductionAtIndex(1), 2)); // E --> E "+" · T
		i6.add(new LR0Item(grammar.getProductionAtIndex(3), 0)); // T --> · T "*" F
		i6.add(new LR0Item(grammar.getProductionAtIndex(4), 0)); // T --> · F
		i6.add(new LR0Item(grammar.getProductionAtIndex(5), 0)); // F --> · "(" E ")"
		i6.add(new LR0Item(grammar.getProductionAtIndex(6), 0)); // F --> · "id"
//		System.out.println(prettyPrintLR0ItemSet(grammar, i6));
		
		i7=new HashSet<LR0Item>();	
		i7.add(new LR0Item(grammar.getProductionAtIndex(3), 2)); // T --> T "*" · F		
		i7.add(new LR0Item(grammar.getProductionAtIndex(5), 0)); // F --> · "(" E ")"		
		i7.add(new LR0Item(grammar.getProductionAtIndex(6), 0)); // F --> · "id"
		
		i8=new HashSet<LR0Item>();	
		i8.add(new LR0Item(grammar.getProductionAtIndex(1), 1)); // E --> E · "+" T
		i8.add(new LR0Item(grammar.getProductionAtIndex(5), 2)); // F --> "(" E · ")"	
		
		i9=new HashSet<LR0Item>();	
		i9.add(new LR0Item(grammar.getProductionAtIndex(1), 3)); // E --> E "+" T · 
		i9.add(new LR0Item(grammar.getProductionAtIndex(3), 1)); // T --> T · "*" F
		
		i10=new HashSet<LR0Item>();			
		i10.add(new LR0Item(grammar.getProductionAtIndex(3), 3)); // T --> T "*" F · 
		
		i11=new HashSet<LR0Item>();			
		i11.add(new LR0Item(grammar.getProductionAtIndex(5), 3)); // F --> "(" E ")" · 
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildParseTable() {
		ParseTable parseTable=null;
		
		// 1. Test gegen die Ausdrucksgrammatik aus dem Drachenbuch
		try {
			parseTable=slrParseTableBuilder.buildParseTable();
		} catch (InvalidGrammarException e) {
			logger.log(Level.INFO, "InvalidGrammarException",e);
			fail("InvalidGrammarException with valid grammar");
		}
		
		DriverTestDataProvider1 testDataProvider=new DriverTestDataProvider1();
		ParseTable expectedParseTable=testDataProvider.getParseTable();		
		assertEquals(expectedParseTable, parseTable);
		
//		System.err.println(expectedParseTable);
//		System.err.println();
//		System.err.println(parseTable);
		
		// 2. Test mit der Nicht-SLR(1) Grammatik aus dem Drachenbuch
		
		Grammar nonSLRGrammar=nonSLRGrammar();
		SLRParseTableBuilder ptb=new SLRParseTableBuilder(nonSLRGrammar);
		try {
			parseTable=ptb.buildParseTable();
			System.err.println(parseTable);
			fail("Invalid Grammar not recognized.");
		} catch (InvalidGrammarException e) {
			logger.info("Succed to recognize Non-SLR(1) Grammar");
		}
		
		// 3. Test, ob die Dangling-Else-Grammatik aus dem Drachenbuch korrekt behandelt wird.
		Grammar danglingElseGrammar=danglingElseGrammar();
		ptb=new SLRParseTableBuilder(danglingElseGrammar);
		try {
			parseTable=ptb.buildParseTable();
			expectedParseTable=danglingElseParseTable(danglingElseGrammar);
			assertEquals(expectedParseTable, parseTable);
			logger.info("Dangline-Else-Test succeed.");
//			System.err.println(parseTable);
		} catch (InvalidGrammarException e) {
			logger.log(Level.INFO, "Dangline-Else-Test failed.",e);
			fail("Dangline-Else-Test failed");
		}		
		
		// 4. Test, ob die Quellgrammatik geparst werden kann.
		Grammar sourceGrammar=sourceGrammar();
		ptb=new SLRParseTableBuilder(sourceGrammar);
		try {
			parseTable=ptb.buildParseTable();
			logger.info("Valid source grammar.");
//			System.out.println(parseTable.toString());
		} catch (InvalidGrammarException e) {
			logger.log(Level.INFO, "Invalid source grammar.",e);
		}	
	}

	private Grammar sourceGrammar(){
		GrammarReader grammarReader = new GrammarReader();
		Grammar g3 = null;
		try {
			g3 = grammarReader.readGrammar("./doc/quellsprache_bnf.txt");
		} catch (BNFParsingErrorException e) {
			fail(e.getClass()+": "+e.getMessage());
		}
		return g3;
	}
	
	private Grammar nonSLRGrammar(){
		// Grammatik (4.16) aus dem Beispiel 4.34 aus dem Drachenbuch
		String strGrammar="" +
				"<S0> ::= <S>\n"+
				"<S>  ::= <L> \"=\" <R> | <R>\n"+
				"<L>  ::= \"*\" <R> | \"id\"\n"+
				"<R>  ::= <L>\n";

		GrammarReader grammarReader=new GrammarReader();
		Grammar grammar=null;
		try {
			grammar=grammarReader.readGrammar(new StringReader(strGrammar)); 
			grammar.setStartSymbol(grammar.getProductionAtIndex(0).getLhs()); // <E0>
			
		} catch (BNFParsingErrorException e) {
			fail(e.getClass()+": "+e.getMessage());
		}
		return grammar;
	}
	
	private Grammar danglingElseGrammar(){
		// Grammatik (4.18) aus dem Drachenbuch
		String strGrammar="" +
				"<S0> ::= <S>\n"+
				"<S>  ::= \"if\" <S> \"else\" <S> | \"if\" <S> | \"a\"\n";

		GrammarReader grammarReader=new GrammarReader();
		Grammar grammar=null;
		try {
			grammar=grammarReader.readGrammar(new StringReader(strGrammar)); 
			grammar.setStartSymbol(grammar.getProductionAtIndex(0).getLhs()); // <E0>
			
		} catch (BNFParsingErrorException e) {
			fail(e.getClass()+": "+e.getMessage());
		}
		return grammar;
	}
	
	private ParseTable danglingElseParseTable(Grammar danglingElseGrammar){
		String strParseTable=
				"	if	else	a	$	S\n"+
				"0	s2		s3		1\n"+
				"1				acc	\n"+
				"2	s2		s3		4\n"+
				"3		r3		r3	\n"+
				"4		s5		r2	\n"+
				"5	s2		s3		6\n"+
				"6		r1		r1	";
		return parseParseTable(danglingElseGrammar, strParseTable);
	}
	
	/**
	 * Hilfsmethode zum Erstellen eines ParseTables aus einer Stringrepräsentation.
	 * 
	 */
	private ParseTable parseParseTable(Grammar grammar, String strParseTable){
		try {
			BufferedReader bufReader=new BufferedReader(new StringReader(strParseTable));
			String[] strSymbols=bufReader.readLine().split("\t");
			
			Symbol[] symbols=new Symbol[strSymbols.length-1]; // erste Spalte ist leer!
			symbolLoop: for (int i = 1; i < strSymbols.length; i++) {
				String aSymbolString = strSymbols[i];
				for (Symbol aSymbol : grammar.getAllSymbols()) {
					if (aSymbol.getName().equals(aSymbolString)){
						symbols[i-1]=aSymbol;
						continue symbolLoop;
					}
					if (Grammar.EMPTY_STRING.equals(aSymbolString)){
						symbols[i-1]=Grammar.EPSILON;
						continue symbolLoop;
					}
					if ("$".equals(aSymbolString)){
						symbols[i-1]=Grammar.INPUT_ENDMARKER;
						continue symbolLoop;
					}
				}
				logger.warning("Failed to parse symbol: "+aSymbolString);
				return null;
			}
			List<State> states=new ArrayList<State>();
			List<String[]> lines=new ArrayList<String[]>();
			
			String strLine=bufReader.readLine();
			while (strLine!=null){
				String[] strLineArray=strLine.split("\t",-1); // limit=-1, damit auch leere Spalten am Ende nicht abgeschnitten werden.
				if (strLineArray.length!=strSymbols.length){
					logger.warning("Failed to parse a line: "+strLine);
					return null;
				}
				states.add(new State(Integer.parseInt(strLineArray[0])));
				lines.add(strLineArray);
				strLine=bufReader.readLine();
			}
			ParseTable parseTable=new ParseTable();
			for (int i = 0; i < lines.size(); i++) {
				State aState=states.get(i);
				String[] strLineArray =lines.get(i);		
				for (int j = 0; j < symbols.length; j++) {
					Symbol aSymbol=symbols[j];
					String strActionOrGoto=strLineArray[j+1];
					if (strActionOrGoto.trim().length()==0){
						continue; // leere Spalten ignorieren
					}
					if  (grammar.getAllTerminalSymols().contains(aSymbol) || Grammar.INPUT_ENDMARKER.equals(aSymbol)){
						// Action parsen
						if (strActionOrGoto.startsWith("s")){ // ShiftAction
							int targetStateIndex=Integer.parseInt(strActionOrGoto.substring(1));
							parseTable.getActionTableForState(aState).setActionForTerminalSymbol(new ShiftAction(states.get(targetStateIndex)), (TerminalSymbol) aSymbol);
						}
						else if (strActionOrGoto.startsWith("r")){ // ReduceAction
							int productionIndex=Integer.parseInt(strActionOrGoto.substring(1));
							parseTable.getActionTableForState(aState).setActionForTerminalSymbol(new ReduceAction(grammar.getProductionAtIndex(productionIndex)), (TerminalSymbol) aSymbol);
						}
						else if (strActionOrGoto.startsWith("acc")){ // AcceptAction						
							parseTable.getActionTableForState(aState).setActionForTerminalSymbol(new AcceptAction(), (TerminalSymbol) aSymbol);
						}
						else{
							logger.warning("Invalid action for state="+aState+" and symbol="+aSymbol+": "+strActionOrGoto);
							return null;
						}
					}
					else if  (grammar.getAllNonTerminals().contains(aSymbol)){
						// Goto parsen
						int targetStateIndex=Integer.parseInt(strActionOrGoto);
						parseTable.getGotoTableForState(aState).setGotoForNonTerminalSymbol(new Goto(states.get(targetStateIndex)), (NonTerminalSymbol) aSymbol);
					}
					else{
						logger.warning("Found neither Action nor Goto for state="+aState+" and symbol="+aSymbol+": "+strActionOrGoto);
						return null;
					}
				}
			}
			parseTable.setInitialState(states.get(0));
			return parseTable;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to parse ParseTable.",e);
			return null;
		}
	}
	
	
	@Test
	public void testClosure() {
		Set<LR0Item> testItemSet0=new HashSet<LR0Item>();
		testItemSet0.add(new LR0Item(grammar.getProductionAtIndex(0), 0)); // E0 --> · E
				
//		System.out.println(prettyPrintLR0ItemSet(grammar, i0));
		
		
		Set<LR0Item> result=slrParseTableBuilder.closure(testItemSet0);		
		
		String failureMessage=null;
		if (!i0.equals(result)){
			failureMessage="Expected:\n"+prettyPrintLR0ItemSet(grammar, i0);
			if (result!=null){
				failureMessage+="Actual:\n"+prettyPrintLR0ItemSet(grammar, result);
			}
			else{
				failureMessage+="Actual: null";
			}
		}
		
		assertEquals(failureMessage, i0, result);						
	}

	@Test
	public void testGotoSet() {
		Symbol symbol=new TerminalSymbol("+");
		// Vgl. Drachenbuch Beispiel 4.27
		Set<LR0Item> result=slrParseTableBuilder.gotoSet(i1, symbol);
		String failureMessage=null;
		if (!i6.equals(result)){			
			failureMessage="Expected:\n"+prettyPrintLR0ItemSet(grammar, i6);
			if (result!=null){
				failureMessage+="Actual:\n"+prettyPrintLR0ItemSet(grammar, result);
			}
			else{
				failureMessage+="Actual: null";
			}
		}
		assertEquals(failureMessage, i6, result);	
	}

	@Test
	public void testCannonicalCollectionOfLR0Items() {
		List<Set<LR0Item>> result = slrParseTableBuilder.cannonicalCollectionOfLR0Items();
//		for (Set<LR0Item> anItemSet : result) {
//			System.err.println("---");
//			System.err.println(prettyPrintLR0ItemSet(grammar, anItemSet));
//		}
		
		List<Set<LR0Item>> cannonicalCollection= new ArrayList<Set<LR0Item>>();
		cannonicalCollection.add(i0);
		cannonicalCollection.add(i1);
		cannonicalCollection.add(i2);
		cannonicalCollection.add(i3);
		cannonicalCollection.add(i4);
		cannonicalCollection.add(i5);
		cannonicalCollection.add(i6);
		cannonicalCollection.add(i7);
		cannonicalCollection.add(i8);
		cannonicalCollection.add(i9);
		cannonicalCollection.add(i10);
		cannonicalCollection.add(i11);
		
		assertEquals(cannonicalCollection, result);
	}

	
	public static String prettyPrintLR0ItemSet(final Grammar grammar, Set<LR0Item> itemSet){
		SortedSet<LR0Item> sortedSet=new  TreeSet<LR0Item>(new Comparator<LR0Item>() {

			@Override
			public int compare(LR0Item item1, LR0Item item2) {
				// Zunächst nach Index der Production sortieren
				int productionIndex1=grammar.getProductions().indexOf(item1.getProduction());
				int productionIndex2=grammar.getProductions().indexOf(item2.getProduction());
				int c=productionIndex1-productionIndex2;
				if (c!=0){
					return c;
				}
				// Dann nach Index des LR0Item sortieren
				c=item1.getIndex()-item2.getIndex();
				return c;
			}
		});
		sortedSet.addAll(itemSet);
		StringBuffer strBuf= new StringBuffer();
		for (LR0Item lr0Item : sortedSet) {
			strBuf.append(lr0Item);
			strBuf.append("\n");
		}		
		return strBuf.toString();
	}
}

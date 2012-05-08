package de.fuberlin.projectci.test.parseTable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;
import de.fuberlin.projectci.parseTable.LR0Item;
import de.fuberlin.projectci.parseTable.SLRParseTableBuilder;

public class SLRParseTableBuilderTest {
	private Grammar grammar=null;
	private SLRParseTableBuilder slrParseTableBuilder=null;
	private Set<LR0Item> i0=null;
	private Set<LR0Item> i1=null;
	private Set<LR0Item> i6=null;
	
	@Before
	public void setUp() throws Exception {
		// Testdaten aus dem Drachebuch Kapitel 4.6 / S.294ff
		String strGrammar="" +
				"<E0> ::= <E>\n"+
				"<E>  ::= <E> \"+\" <T> | <T>\n"+
				"<T>  ::= <T> \"*\" <F> | <F>\n"+
				"<F>  ::= \"(\" <E> \")\" | \"id\"";

		GrammarReader grammarReader=new GrammarReader();
		
		try {
			grammar=grammarReader.readGrammar(new StringReader(strGrammar));
			
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
		
		i6=new HashSet<LR0Item>();	
		i6.add(new LR0Item(grammar.getProductionAtIndex(1), 2)); // E --> E "+" · T
		i6.add(new LR0Item(grammar.getProductionAtIndex(3), 0)); // T --> · T "*" F
		i6.add(new LR0Item(grammar.getProductionAtIndex(4), 0)); // T --> · F
		i6.add(new LR0Item(grammar.getProductionAtIndex(5), 0)); // F --> · "(" E ")"
		i6.add(new LR0Item(grammar.getProductionAtIndex(6), 0)); // F --> · "id"
//		System.out.println(prettyPrintLR0ItemSet(grammar, i6));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBuildParseTable() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
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

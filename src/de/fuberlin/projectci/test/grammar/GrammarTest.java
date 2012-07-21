package de.fuberlin.projectci.test.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.fuberlin.projectci.grammar.BNFGrammarReader;
import de.fuberlin.projectci.grammar.BNFParsingErrorException;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.GrammarReader;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;

/**
 * Testfälle für die Berechnung der FIRST- und FOLLOW-Mengen.
 *
 */
public class GrammarTest {
	private Grammar grammar=null;
	private Map<NonTerminalSymbol, Set<TerminalSymbol>>nonTerminal2FirstSet=null;
	private Map<NonTerminalSymbol, Set<TerminalSymbol>>nonTerminal2FollowSet=null;
	
	
	@Before
	public void setUp() throws Exception {
		
		// Testdaten aus: http://www.susigottwald.de/Unistaff/CB_Diplompruefung/Zusammenfassung_Uebersetzerbau.pdf (S.31)
		String strGrammar="" +
				"<E> ::= <T> <E1>\n"+
				"<E1>  ::= \"+\" <T> <E1> | \"@\"\n"+
				"<T> ::= <F> <T1>\n"+
				"<T1>  ::= \"*\" <F> <T1> | \"@\"\n"+
				"<F>  ::= \"(\" <E> \")\" | \"id\"";

		GrammarReader grammarReader=new BNFGrammarReader();
		
		try {
			grammar=grammarReader.readGrammar(new StringReader(strGrammar));
			
		} catch (BNFParsingErrorException e) {
			fail(e.getClass()+": "+e.getMessage());
		}
		
		TerminalSymbol ts_id=new TerminalSymbol("id");
		TerminalSymbol ts_add=new TerminalSymbol("+");
		TerminalSymbol ts_mul=new TerminalSymbol("*");		
		TerminalSymbol ts_leftp=new TerminalSymbol("(");
		TerminalSymbol ts_rightp=new TerminalSymbol(")");
		TerminalSymbol ts_eof=Grammar.INPUT_ENDMARKER;
		TerminalSymbol ts_eps=Grammar.EPSILON;
		
		NonTerminalSymbol nts_e=new NonTerminalSymbol("E");
		NonTerminalSymbol nts_e1=new NonTerminalSymbol("E1");
		NonTerminalSymbol nts_t=new NonTerminalSymbol("T");
		NonTerminalSymbol nts_t1=new NonTerminalSymbol("T1");
		NonTerminalSymbol nts_f=new NonTerminalSymbol("F");
		
		nonTerminal2FirstSet=new HashMap<NonTerminalSymbol, Set<TerminalSymbol>>();
		nonTerminal2FollowSet=new HashMap<NonTerminalSymbol, Set<TerminalSymbol>>();
		
		nonTerminal2FirstSet.put(nts_e, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_leftp, ts_id})));
		nonTerminal2FirstSet.put(nts_t, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_leftp, ts_id})));
		nonTerminal2FirstSet.put(nts_f, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_leftp, ts_id})));
		nonTerminal2FirstSet.put(nts_e1, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_add, ts_eps})));
		nonTerminal2FirstSet.put(nts_t1, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_mul, ts_eps})));
		
		nonTerminal2FollowSet.put(nts_e, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_rightp, ts_eof})));
		nonTerminal2FollowSet.put(nts_e1, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_rightp, ts_eof})));		
		nonTerminal2FollowSet.put(nts_t, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_add, ts_rightp, ts_eof})));
		nonTerminal2FollowSet.put(nts_t1, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_add, ts_rightp, ts_eof})));		
		nonTerminal2FollowSet.put(nts_f, new HashSet<TerminalSymbol>(Arrays.asList(new TerminalSymbol[]{ts_add, ts_mul, ts_rightp, ts_eof})));
	}

	@Test
	public void testFirst() {		
		Set<NonTerminalSymbol> nonTerminals = nonTerminal2FirstSet.keySet();		
		for (NonTerminalSymbol aNonTerminalSymbol : nonTerminals) {
			Set<TerminalSymbol> result = grammar.first(aNonTerminalSymbol);
			Set<TerminalSymbol> expected = nonTerminal2FirstSet.get(aNonTerminalSymbol);
			assertEquals("Symbol: "+aNonTerminalSymbol, expected, result);
		}
	}

	@Test
	public void testFollow() {
		Set<NonTerminalSymbol> nonTerminals = nonTerminal2FollowSet.keySet();		
		for (NonTerminalSymbol aNonTerminalSymbol : nonTerminals) {
			Set<TerminalSymbol> result = grammar.follow(aNonTerminalSymbol);
			Set<TerminalSymbol> expected = nonTerminal2FollowSet.get(aNonTerminalSymbol);
			assertEquals("Symbol: "+aNonTerminalSymbol, expected, result);
		}
	}

}

package de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.Grammars;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.Symbol;
import de.fuberlin.bii.utils.Notification;

public class ItemAutomataTest {



	private void testMatchingOfExpressionWithGrammar( String expression, ContextFreeGrammar grammar) throws Exception {
		Notification.enableDebugPrinting();
		
		Lr0ItemAutomata<Symbol> itemAutomata = new Lr0ItemAutomata<Symbol>( grammar);
		System.out.println( "Grammatik = " + grammar + " mit StartSymbol " + grammar.getStartSymbol());
    System.out.println( itemAutomata.toString());
    System.out.println( "---------------");
    System.out.println( "SLR(1) = " + itemAutomata.isSLR1());
    System.out.println( "---------------");
    System.out.println( "FirstSets: " + itemAutomata.getGrammar().getFirstSets());
    System.out.println( "FollowSets: " + itemAutomata.getGrammar().getFollowSets());
    System.out.println( "---------------");
 
  
		List<Symbol> symbols = new ArrayList<Symbol>();
		for ( int i = 0; i < expression.length(); i++) {
			symbols.add( new Symbol( expression.charAt( i)));
		}

    
    System.out.println( "match: " + itemAutomata.match( symbols));
    System.out.println();
    
    assert itemAutomata.isSLR1();
    assert itemAutomata.match( symbols);
	}

	@Test
	public void testExample() throws Exception {
	//	testMatchingOfExpressionWithGrammar( "aa*a+", Grammars.getExampleGrammar());
	}

	
	@Test
	public void testRegex() throws Exception {
		testMatchingOfExpressionWithGrammar( "aa", Grammars.getSimplifiedRegexGrammar());
	}
		
}

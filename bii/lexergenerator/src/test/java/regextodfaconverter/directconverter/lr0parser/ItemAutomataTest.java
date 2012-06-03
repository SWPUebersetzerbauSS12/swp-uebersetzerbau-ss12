package regextodfaconverter.directconverter.lr0parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.GrammarTest;
import regextodfaconverter.directconverter.lr0parser.grammar.Grammars;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionSet;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementArray;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;

public class ItemAutomataTest {



	private void testMatchingOfExpressionWithGrammar( String expression, ContextFreeGrammar grammar) throws Exception {
		Lr0ItemAutomata<Character> itemAutomata = new Lr0ItemAutomata<Character>( grammar);
		System.out.println( "Grammatik = " + grammar + " mit StartSymbol " + grammar.getStartSymbol());
    System.out.println( itemAutomata.toString());
    System.out.println( "---------------");
    System.out.println( "SLR(1) = " + itemAutomata.isSLR1());
    System.out.println( "---------------");
    System.out.println( "FirstSets: " + itemAutomata.getGrammar().getFollowSets());
    System.out.println( "FollowSets: " + itemAutomata.getGrammar().getFirstSets());
    System.out.println( "---------------");
 
    List<Character> charList = new ArrayList<Character>();
    for (Character character : expression.toCharArray()) {
  	  charList.add( character);
    }
    
    System.out.println( "match: " + itemAutomata.match( charList));
    System.out.println();
    
    assert itemAutomata.isSLR1();
    assert itemAutomata.match( charList);
	}

	@Test
	public void testExample() throws Exception {
		testMatchingOfExpressionWithGrammar( "aa*a+", Grammars.getExampleGrammar());
	}

	
	@Test
	public void testRegex() throws Exception {
		testMatchingOfExpressionWithGrammar( "a.a+a+a", Grammars.getRegexGrammar());
	}
		
}

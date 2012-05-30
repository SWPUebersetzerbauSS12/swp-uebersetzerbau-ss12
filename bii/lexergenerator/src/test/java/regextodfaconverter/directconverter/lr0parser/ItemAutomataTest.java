package regextodfaconverter.directconverter.lr0parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.GrammarTest;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionSet;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementArray;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;

public class ItemAutomataTest {

	
	private static ContextFreeGrammar getExampleGrammar() {
		ContextFreeGrammar grammar = new ContextFreeGrammar();
		// we define a simple regex grammar for testing
		Nonterminal S = new Nonterminal( "S");
		Terminal<Character> a = new Terminal<Character>( 'a');
	
		Terminal<Character> opStar = new Terminal<Character>( '*');
		Terminal<Character> opPlus = new Terminal<Character>( '+');
		
		ProductionSet productions = new ProductionSet();
		productions.add( new ProductionRule(S, S, S, opPlus));
		productions.add( new ProductionRule(S, S, S, opStar));
		productions.add( new ProductionRule(S, a));
		
		grammar.addAll( productions);
		
		return grammar;
	}
	
	@Test
	public void testParserTable() throws Exception {
      Lr0ItemAutomata<Character> itemAutomata = new Lr0ItemAutomata<Character>( GrammarTest.getRegexGrammar());//getExampleGrammar());
      System.out.println( itemAutomata.toString());
	}
	
	@Test
	public void testMatching() throws Exception {
      Lr0ItemAutomata<Character> itemAutomata = new Lr0ItemAutomata<Character>( GrammarTest.getRegexGrammar());//getExampleGrammar());
      itemAutomata.toString();
      System.out.println( "---------------");
      System.out.println( itemAutomata.getGrammar().getFollowSets());
      System.out.println( itemAutomata.getGrammar().getFirstSets());
      System.out.println( "---------------");
      
      List<Character> charList = new ArrayList<Character>();
      for (Character character : "a.a+a".toCharArray()) {
    	  charList.add( character);
	  }
       
      System.out.println( "matches " + itemAutomata.match( charList));
	}
	
	
}

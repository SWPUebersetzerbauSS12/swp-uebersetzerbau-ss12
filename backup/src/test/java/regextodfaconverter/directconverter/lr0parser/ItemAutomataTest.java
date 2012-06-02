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

	
	public static ContextFreeGrammar getRegexGrammar() {
		ContextFreeGrammar grammar = new ContextFreeGrammar();
		// we define a simple regex grammar for testing
		Nonterminal R = new Nonterminal( "R");
		Nonterminal S = new Nonterminal( "S");
		Nonterminal T = new Nonterminal( "T");
		Nonterminal U = new Nonterminal( "U");
		Nonterminal V = new Nonterminal( "V");
		Terminal<Character> a = new Terminal<Character>( 'a');
	
		Terminal<Character> leftBracket = new Terminal<Character>( '(');
		Terminal<Character> rightBracket = new Terminal<Character>( ')');
		Terminal<Character> opKleeneClosure = new Terminal<Character>( '*');
		Terminal<Character> opAlternative = new Terminal<Character>( '+');
		Terminal<Character> opConcatenation = new Terminal<Character>( '.');
		
		ProductionSet productions = new ProductionSet();
		productions.add( new ProductionRule(R, R, opAlternative, S));
		productions.add( new ProductionRule(R, S));
		productions.add( new ProductionRule(S, S, opConcatenation, T));
		productions.add( new ProductionRule(S, T));
		productions.add( new ProductionRule(T, U, opKleeneClosure));
		productions.add( new ProductionRule(T, U));
		//productions.add( new ProductionRule(U, R));
		productions.add( new ProductionRule(U, V));
		productions.add( new ProductionRule(U, leftBracket, R, rightBracket));
		productions.add( new ProductionRule(V, a));
		
		grammar.addAll( productions);
		grammar.setStartSymbol( R);
		
		return grammar;
	}
	
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
		testMatchingOfExpressionWithGrammar( "aa*a+", getExampleGrammar());
	}

	
	@Test
	public void testRegex() throws Exception {
		testMatchingOfExpressionWithGrammar( "a.a+a+a", getRegexGrammar());
	}
		
}

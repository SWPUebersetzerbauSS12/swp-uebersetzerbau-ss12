package regextodfaconverter.directconverter.lr0parser.itemset;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.junit.Test;


import regextodfaconverter.MinimalDfa;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElement;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementArray;
import regextodfaconverter.directconverter.lr0parser.grammar.RuleElementSequenz;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.fsm.FiniteStateMachine;
import tokenmatcher.StatePayload;
import tokenmatcher.Token;
import tokenmatcher.Tokenizer;


public class ClosureTest {

	
	
	@Test
	public void testClosuresEquality() throws Exception {

		Item i1 = new Item(new Nonterminal( "S"), new Terminal( "a"));
		Item i2 = new Item(new Nonterminal( "S"), new Terminal( "a"));
		assert i1.equals( i2);
		System.out.println( i1.equals( i2));
		
		RuleElementSequenz elementSequenz = new RuleElementArray();
		elementSequenz.add( new Terminal("a"));
		Item i3 = new Item(new Nonterminal( "S"), elementSequenz);
		assert i2.equals( i3);
		System.out.println( i2.equals( i3));
		
		Item i4 = new Item(new Nonterminal( "S"), elementSequenz, 1);
		assert !(i3.equals( i4));
		System.out.println( !(i3.equals( i4)));
		
		
		Closure c1 = new Closure();
		Closure c2 = new Closure();
		Closure c3 = new Closure();
		
		c1.addAsKernelItem( i1);
		c2.addAsKernelItem( i1);
		c3.addAsKernelItem( i2);
		
		assert c1.equals( c2);
		System.out.println( c1.equals( c2));
		
		assert c2.equals( c3);
		System.out.println( c2.equals( c3));
		
	}

}

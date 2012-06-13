package de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.itemset;

import junit.framework.Assert;

import org.junit.Test;

import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.RuleElementArray;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.RuleElementSequenz;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar.Terminal;


public class ClosureTest {

	
	
	@Test
	public void testClosuresEquality() throws Exception {

		Item i1 = new Item(new Nonterminal( "S"), new Terminal( new Symbol("a")));
		Item i2 = new Item(new Nonterminal( "S"), new Terminal( new Symbol("a")));
		Assert.assertTrue( i1.equals( i2));
		
		RuleElementSequenz elementSequenz = new RuleElementArray();
		elementSequenz.add( new Terminal(new Symbol("a")));
		Item i3 = new Item(new Nonterminal( "S"), elementSequenz);
		Assert.assertTrue( i2.equals( i3));
		
		Item i4 = new Item(new Nonterminal( "S"), elementSequenz, 1);
		Assert.assertFalse( i3.equals( i4));
		
		Closure c1 = new Closure();
		Closure c2 = new Closure();
		Closure c3 = new Closure();
		
		c1.addAsKernelItem( i1);
		c2.addAsKernelItem( i1);
		c3.addAsKernelItem( i2);
		
		Assert.assertTrue( c1.equals( c2));
	
		Assert.assertTrue( c2.equals( c3));
		
	}

}

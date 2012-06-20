package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset;

import junit.framework.Assert;

import org.junit.Test;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElementArray;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElementSequenz;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr0Closure;
import de.fuberlin.bii.utils.Notification;


public class ClosureTest {

	
	
	@Test
	public void testClosuresEquality() throws Exception {

		Notification.enableDebugPrinting();
		
		Item i1 = new Lr0Item(new Nonterminal( "S"), new Terminal( new Symbol("a")));
		Item i2 = new Lr0Item(new Nonterminal( "S"), new Terminal( new Symbol("a")));
		Assert.assertTrue( i1.equals( i2));
		
		RuleElementSequenz elementSequenz = new RuleElementArray();
		elementSequenz.add( new Terminal(new Symbol("a")));
		Item i3 = new Lr0Item(new Nonterminal( "S"), elementSequenz);
		Assert.assertTrue( i2.equals( i3));
		
		Item i4 = new Lr0Item(new Nonterminal( "S"), elementSequenz, 1);
		Assert.assertFalse( i3.equals( i4));
		
		Lr0Closure c1 = new Lr0Closure();
		Lr0Closure c2 = new Lr0Closure();
		Lr0Closure c3 = new Lr0Closure();
		
		c1.addAsKernelItem( i1);
		c2.addAsKernelItem( i1);
		c3.addAsKernelItem( i2);
		
		Assert.assertTrue( c1.equals( c2));
	
		Assert.assertTrue( c2.equals( c3));
		
		
		
	}

}

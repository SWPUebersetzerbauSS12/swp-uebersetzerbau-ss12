/*
 * 
 * Copyright 2012 lexergen.
 * This file is part of lexergen.
 * 
 * lexergen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * lexergen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with lexergen.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * lexergen:
 * A tool to chunk source code into tokens for further processing in a compiler chain.
 * 
 * Projectgroup: bi, bii
 * 
 * Authors: Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */


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

/**
 * Testet {@link Closure}.
 * 
 * @author Johannes Dahlke
 *
 */
@SuppressWarnings("rawtypes")
public class ClosureTest {

	
	
	@SuppressWarnings({ "static-method", "unchecked"})
	@Test
	public void testClosuresEquality() throws Exception {

		Notification.enableDebugPrinting();
		
		Lr0Item i1 = new Lr0Item(new Nonterminal( "S"), new Terminal( new Symbol("a")));
		Lr0Item i2 = new Lr0Item(new Nonterminal( "S"), new Terminal( new Symbol("a")));
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

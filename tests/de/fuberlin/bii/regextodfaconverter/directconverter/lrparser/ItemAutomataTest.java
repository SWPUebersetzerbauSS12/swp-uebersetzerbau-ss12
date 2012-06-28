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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import junit.framework.Assert;

import org.junit.Test;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.Lr0ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Grammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Grammars;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.utils.Notification;

/**
 * 
 * Testet die Item-Automaten {@link Lr0ItemAutomat}, {@link Slr1ItemAutomat} und {@link Lr1ItemAutomat}. 
 * 
 * @author Johannes Dahlke
 *
 */
public class ItemAutomataTest {


	private void testMatchingOfExpressionWithAutomatAndGrammar( ItemAutomat<Symbol> itemAutomata, String expression, ContextFreeGrammar grammar) throws Exception {
		
		List<Symbol> symbols = new ArrayList<Symbol>();
		for ( int i = 0; i < expression.length(); i++) {
			symbols.add( new Symbol( expression.charAt( i)));
		}

    System.out.println( "match: " + itemAutomata.match( symbols));
    
    Assert.assertTrue( itemAutomata.match( symbols));
	}
	
	
	private void printAutomataInfo( ItemAutomat<Symbol> itemAutomata) throws Exception {
    System.out.println( itemAutomata.toString());
    System.out.println( "---------------");
    System.out.println( "isReduceConflictFree = " + itemAutomata.isReduceConflictFree());
    System.out.println( "---------------");
	}
	
	private void testAutomataToReduceConflict( ItemAutomat<Symbol> itemAutomata, boolean expectedResult) throws Exception {
    Assert.assertEquals( expectedResult, itemAutomata.isReduceConflictFree());
	}
		
	private void printGrammarInfo( ContextFreeGrammar grammar) throws Exception {
		System.out.println( "Grammatik = " + grammar + " mit StartSymbol " + grammar.getStartSymbol());
		System.out.println( "---------------");
   	System.out.println( "FirstSets: " + grammar.getFirstSets());
	  System.out.println( "FollowSets: " + grammar.getFollowSets());
	  System.out.println( "---------------");
	}
	

	@Test
	public void testExample() throws Exception {
	//	testMatchingOfExpressionWithGrammar( "aa*a+", Grammars.getExampleGrammar());
	}

	
	@Test
	public void testLr0AutomataWithRegex() throws Exception {
		Notification.enableDebugPrinting();
		
		ContextFreeGrammar grammar = Grammars.getRegexGrammar();
		String expression = "a.a";
		ItemAutomat<Symbol> itemAutomata = new Lr0ItemAutomat<Symbol>( grammar);
		
		printGrammarInfo( grammar);
		printAutomataInfo( itemAutomata);
		
		testAutomataToReduceConflict( itemAutomata, false);
		//testMatchingOfExpressionWithAutomatAndGrammar( itemAutomata, expression, grammar);
	}
	
	@Test
	public void testSlr1AutomataWithRegex() throws Exception {
		Notification.enableDebugPrinting();
		
		ContextFreeGrammar grammar = Grammars.getRegexGrammar();
		String expression = "a.a";
		ItemAutomat<Symbol> itemAutomata = new Slr1ItemAutomat<Symbol>( grammar);
		
		printGrammarInfo( grammar);
		printAutomataInfo( itemAutomata);
		
		testAutomataToReduceConflict( itemAutomata, true);
		testMatchingOfExpressionWithAutomatAndGrammar( itemAutomata, expression, grammar);
	}
	
	@Test
	public void testSlr1AutomataWithSimplifiedRegex() throws Exception {
		Notification.enableDebugPrinting();
		
		ContextFreeGrammar grammar = Grammars.getSimplifiedRegexGrammar();
		String expression = "aa";
		ItemAutomat<Symbol> itemAutomata = new Slr1ItemAutomat<Symbol>( grammar);
		
		printGrammarInfo( grammar);
		printAutomataInfo( itemAutomata);
		
		testAutomataToReduceConflict( itemAutomata, true);
		testMatchingOfExpressionWithAutomatAndGrammar( itemAutomata, expression, grammar);
	}
	
	@Test
	public void testLr1AutomataWithRegex() throws Exception {
		Notification.enableDebugPrinting();
		
		ContextFreeGrammar grammar = Grammars.getRegexGrammar();
		String expression = "a.a";
		ItemAutomat<Symbol> itemAutomata = new Lr1ItemAutomat<Symbol>( grammar);
		
		printGrammarInfo( grammar);
		printAutomataInfo( itemAutomata);
		
		testAutomataToReduceConflict( itemAutomata, true);
		testMatchingOfExpressionWithAutomatAndGrammar( itemAutomata, expression, grammar);
	}
	
	@Test
	public void testLr1AutomataWithSimplifiedRegex() throws Exception {
		Notification.enableDebugPrinting();
		
		ContextFreeGrammar grammar = Grammars.getSimplifiedRegexGrammar();
		String expression = "aa";
		ItemAutomat<Symbol> itemAutomata = new Lr1ItemAutomat<Symbol>( grammar);
		
		printGrammarInfo( grammar);
		printAutomataInfo( itemAutomata);
		
		testAutomataToReduceConflict( itemAutomata, true);
		testMatchingOfExpressionWithAutomatAndGrammar( itemAutomata, expression, grammar);
	}
	
	
	@Test
	public void testLr1AutomataWithSimplifiedOriginalRegex() throws Exception {
		Notification.enableDebugPrinting();
		
		//ContextFreeGrammar grammar = Grammars.getSimplifiedOriginalRegexGrammar();
		ContextFreeGrammar grammar = Grammars.getAnotherOriginalRegexGrammar();
		String expression = "(+a*)*";
		ItemAutomat<Symbol> itemAutomata = new Slr1ItemAutomat<Symbol>( grammar);
		
		printGrammarInfo( grammar);
		printAutomataInfo( itemAutomata);
		
		testAutomataToReduceConflict( itemAutomata, false);
		testMatchingOfExpressionWithAutomatAndGrammar( itemAutomata, expression, grammar);		
	}

		
}

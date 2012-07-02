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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;


/**
 * 
 * Testet {@link Grammar}-Exemplare. Unter anderem Test der First- und Followmengen.   
 * @author Johannes Dahlke
 *
 */
public class GrammarTest {

	public static ContextFreeGrammar getRegexGrammar() {
		ContextFreeGrammar grammar = new ContextFreeGrammar();
		// we define a simple regex grammar for testing
		Nonterminal R = new Nonterminal( "R");
		Nonterminal S = new Nonterminal( "S");
		Nonterminal T = new Nonterminal( "T");
		Nonterminal U = new Nonterminal( "U");
		Nonterminal V = new Nonterminal( "V");
		Terminal<Symbol> a = new Terminal<Symbol>( new Symbol( 'a'));
	
		Terminal<Symbol> leftBracket = new Terminal<Symbol>( new Symbol( '('));
		Terminal<Symbol> rightBracket = new Terminal<Symbol>( new Symbol( ')'));
		Terminal<Symbol> opKleeneClosure = new Terminal<Symbol>(  new Symbol( '*'));
		Terminal<Symbol> opAlternative = new Terminal<Symbol>(  new Symbol( '+'));
		Terminal<Symbol> opConcatenation = new Terminal<Symbol>(  new Symbol( '.'));
		
		ProductionSet productions = new ProductionSet();
		productions.add( new ProductionRule(R, R, opAlternative, S));
		productions.add( new ProductionRule(R, S));
		productions.add( new ProductionRule(S, S, opConcatenation, T));
		productions.add( new ProductionRule(S, T));
		productions.add( new ProductionRule(T, U, opKleeneClosure));
		productions.add( new ProductionRule(T, U));
		productions.add( new ProductionRule(U, R));
		productions.add( new ProductionRule(U, V));
		productions.add( new ProductionRule(U, leftBracket, R, rightBracket));
		productions.add( new ProductionRule(V, a));
		
		grammar.addAll( productions);
		
		return grammar;
	}
	
	@Test
	public void testGrammar() throws Exception {

		ContextFreeGrammar grammar = getRegexGrammar();
		
		Map<Nonterminal,TerminalSet> firstSets = grammar.getFirstSets();
		for ( Nonterminal nonterminal : firstSets.keySet()) {
			System.out.print( nonterminal + " -> ");
			for (Terminal terminal : firstSets.get(nonterminal)) {
				System.out.print( terminal +" ");
			}	
			System.out.println();
		}
		
		System.out.println();
		
		
		Map<Nonterminal,TerminalSet> followSets = grammar.getFollowSets();
		for ( Nonterminal nonterminal : followSets.keySet()) {
			
			
			
			
			System.out.print( nonterminal + " -> ");
			for (Terminal terminal : followSets.get(nonterminal)) {
				System.out.print( terminal +" ");
			}	
			System.out.println();
		}
		
	}

}

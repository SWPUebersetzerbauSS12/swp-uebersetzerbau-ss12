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

package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.ArrayList;


/**
 * 
 * @author Johannes Dahlke
 *
 */
public class Grammars {

	public static ContextFreeGrammar getRegexGrammar() {
		ContextFreeGrammar grammar = new ContextFreeGrammar();
		// we define a simple regex grammar for testing
		Nonterminal R = new Nonterminal( "R");
		Nonterminal S = new Nonterminal( "S");
		Nonterminal T = new Nonterminal( "T");
		Nonterminal U = new Nonterminal( "U");
		Nonterminal V = new Nonterminal( "V");
		ArrayList<Terminal<Character>>  terminals = new ArrayList<Terminal<Character>>(); 
    // a..z
		for ( int c = 'a'; c <= 'z'; c++) {
			terminals.add(new Terminal<Character>( (char) c));
		}
		// A..Z
		for ( int c = 'A'; c <= 'Z'; c++) {
			terminals.add(new Terminal<Character>( (char) c));
		}
	  // 0..1
		for ( int c = '0'; c <= '1'; c++) {
			terminals.add(new Terminal<Character>( (char) c));
		}
		// TODO: Regex Zeicheraum noch unvollständig
			

		
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
		productions.add( new ProductionRule(U, V));
		productions.add( new ProductionRule(U, leftBracket, R, rightBracket));
		for ( Terminal<Character> terminal : terminals) {
			productions.add( new ProductionRule(V, terminal));	
		}
		// TODO: Regex Grammatik noch unvollständig
		
		grammar.addAll( productions);
		grammar.setStartSymbol( R);

		return grammar;
	}
	
	
	public static ContextFreeGrammar getSimplifiedRegexGrammar() {
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
		productions.add( new ProductionRule(U, R));
		productions.add( new ProductionRule(U, V));
		productions.add( new ProductionRule(U, leftBracket, R, rightBracket));
		productions.add( new ProductionRule(V, a));
		
		grammar.addAll( productions);
		grammar.setStartSymbol( R);
		
		return grammar;
	}
	
	public static ContextFreeGrammar getExampleGrammar() {
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
	
	
}

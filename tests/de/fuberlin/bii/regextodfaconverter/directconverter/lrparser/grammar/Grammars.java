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

import java.util.ArrayList;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;


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
		Terminal<Symbol<Character,Object>> a = new Terminal<Symbol<Character,Object>>( new Symbol( 'a'));

		
		Terminal<Symbol> leftBracket = new Terminal<Symbol>( new Symbol('('));
		Terminal<Symbol> rightBracket = new Terminal<Symbol>( new Symbol(')'));
		Terminal<Symbol> opKleeneClosure = new Terminal<Symbol>( new Symbol('*'));
		Terminal<Symbol> opAlternative = new Terminal<Symbol>( new Symbol('+'));
		Terminal<Symbol> opConcatenation = new Terminal<Symbol>( new Symbol('.'));
		
		ProductionSet productions = new ProductionSet();
		productions.add( new ProductionRule(R, R, opAlternative, S));
		productions.add( new ProductionRule(R, S));
		productions.add( new ProductionRule(S, S, opConcatenation, T));
		productions.add( new ProductionRule(S, T));
		productions.add( new ProductionRule(T, U, opKleeneClosure));
		productions.add( new ProductionRule(T, U));
		productions.add( new ProductionRule(U, V));
		productions.add( new ProductionRule(U, leftBracket, R, rightBracket));
		productions.add( new ProductionRule(V, a));
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
		Terminal<Symbol<Character,Object>> a = new Terminal<Symbol<Character,Object>>( new Symbol( 'a'));
	
		Terminal<Symbol<Character,Object>> leftBracket = new Terminal<Symbol<Character,Object>>( new Symbol('('));
		Terminal<Symbol<Character,Object>> rightBracket = new Terminal<Symbol<Character,Object>>( new Symbol(')'));
		Terminal<Symbol<Character,Object>> opKleeneClosure = new Terminal<Symbol<Character,Object>>( new Symbol('*'));
		Terminal<Symbol<Character,Object>> opAlternative = new Terminal<Symbol<Character,Object>>( new Symbol('+'));
		Terminal<Symbol<Character,Object>> opConcatenation = new Terminal<Symbol<Character,Object>>( new Symbol('.'));
		
		ProductionSet productions = new ProductionSet();
		productions.add( new ProductionRule(R, R, opAlternative, S));
		productions.add( new ProductionRule(R, S));
		//productions.add( new ProductionRule(S, S, opConcatenation, T));
		productions.add( new ProductionRule(S, S, T));
		productions.add( new ProductionRule(S, T));
		productions.add( new ProductionRule(T, U, opKleeneClosure));
		productions.add( new ProductionRule(T, U));
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
		Terminal<Symbol<Character,Object>> a = new Terminal<Symbol<Character,Object>>( new Symbol( 'a'));
	
		Terminal<Symbol<Character,Object>> opStar = new Terminal<Symbol<Character,Object>>( new Symbol('*'));
		Terminal<Symbol<Character,Object>> opPlus = new Terminal<Symbol<Character,Object>>( new Symbol('+'));
		
		ProductionSet productions = new ProductionSet();
		productions.add( new ProductionRule(S, S, S, opPlus));
		productions.add( new ProductionRule(S, S, S, opStar));
		productions.add( new ProductionRule(S, a));
		
		grammar.addAll( productions);
		
		return grammar;
	}


	public static ContextFreeGrammar getSimplifiedOriginalRegexGrammar() {
		ContextFreeGrammar grammar = new ContextFreeGrammar();
		// we define a simple regex grammar for testing
		Nonterminal R = new Nonterminal( "R");
		Nonterminal S = new Nonterminal( "S");
		Nonterminal SSlash = new Nonterminal( "S'");
		Nonterminal T = new Nonterminal( "T");
		Nonterminal TSlash = new Nonterminal( "T'");
		Nonterminal U = new Nonterminal( "U");
		Nonterminal USlash = new Nonterminal( "U'");
		Nonterminal V = new Nonterminal( "V");
		Nonterminal VSlash = new Nonterminal( "V'");
		Terminal<Symbol<Character,Object>> a = new Terminal<Symbol<Character,Object>>( new Symbol( 'a'));
		EmptyString epsilon = new EmptyString();
		
	
		Terminal<Symbol<Character,Object>> leftBracket = new Terminal<Symbol<Character,Object>>( new Symbol('('));
		Terminal<Symbol<Character,Object>> rightBracket = new Terminal<Symbol<Character,Object>>( new Symbol(')'));
		Terminal<Symbol<Character,Object>> opKleeneClosure = new Terminal<Symbol<Character,Object>>( new Symbol('*'));
		Terminal<Symbol<Character,Object>> opAlternative = new Terminal<Symbol<Character,Object>>( new Symbol('+'));
		Terminal<Symbol<Character,Object>> opConcatenation = new Terminal<Symbol<Character,Object>>( new Symbol('.'));
		
		ProductionSet productions = new ProductionSet();
		productions.add( new ProductionRule(R, R, opAlternative, S));
		productions.add( new ProductionRule(R, S));
		//productions.add( new ProductionRule(S, S, opConcatenation, T));
		productions.add( new ProductionRule(S, SSlash, T));
		productions.add( new ProductionRule(S, T));
		productions.add( new ProductionRule(SSlash, SSlash, TSlash));
		productions.add( new ProductionRule(SSlash, TSlash));	
		
		productions.add( new ProductionRule(T, U, opKleeneClosure));
		productions.add( new ProductionRule(T, U));
		productions.add( new ProductionRule(U, V));
		productions.add( new ProductionRule(U, leftBracket, R, rightBracket));
		productions.add( new ProductionRule(V, a));
		productions.add( new ProductionRule(V, epsilon));
		
		productions.add( new ProductionRule(TSlash, USlash, opKleeneClosure));
		productions.add( new ProductionRule(TSlash, USlash));
		productions.add( new ProductionRule(USlash, VSlash));
		productions.add( new ProductionRule(USlash, leftBracket, R, rightBracket));
		productions.add( new ProductionRule(VSlash, a));
		
		grammar.addAll( productions);
		grammar.setStartSymbol( R);
		
		return grammar;
	}
	
	
}

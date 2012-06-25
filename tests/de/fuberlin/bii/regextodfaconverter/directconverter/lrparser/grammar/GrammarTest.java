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

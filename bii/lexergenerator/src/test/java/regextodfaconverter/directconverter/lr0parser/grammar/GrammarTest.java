package regextodfaconverter.directconverter.lr0parser.grammar;

import java.util.Map;
import java.util.Set;

import org.junit.Test;


import regextodfaconverter.MinimalDfa;
import regextodfaconverter.fsm.FiniteStateMachine;
import tokenmatcher.StatePayload;
import tokenmatcher.Token;
import tokenmatcher.Tokenizer;


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
		
		Map<Nonterminal,Set<Terminal>> firstSets = grammar.getFirstSets();
		for ( Nonterminal nonterminal : firstSets.keySet()) {
			System.out.print( nonterminal + " -> ");
			for (Terminal terminal : firstSets.get(nonterminal)) {
				System.out.print( terminal +" ");
			}	
			System.out.println();
		}
		
		System.out.println();
		
		
		Map<Nonterminal,Set<Terminal>> followSets = grammar.getFollowSets();
		for ( Nonterminal nonterminal : followSets.keySet()) {
			
			
			
			
			System.out.print( nonterminal + " -> ");
			for (Terminal terminal : followSets.get(nonterminal)) {
				System.out.print( terminal +" ");
			}	
			System.out.println();
		}
		
	}

}

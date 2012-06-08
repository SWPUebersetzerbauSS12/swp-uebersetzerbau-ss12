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


package regextodfaconverter.directconverter.regex.operatortree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Grammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Grammars;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.regex.RegexSpecialChars;
import regextodfaconverter.directconverter.syntaxtree.AbstractSyntaxTree;
import regextodfaconverter.directconverter.syntaxtree.AttributesMap;
import regextodfaconverter.directconverter.syntaxtree.SemanticRule;
import regextodfaconverter.directconverter.syntaxtree.SemanticRules;
import regextodfaconverter.directconverter.syntaxtree.SyntaxDirectedDefinition;
import regextodfaconverter.directconverter.syntaxtree.Tree;
import regextodfaconverter.directconverter.syntaxtree.TreeIterator;
import regextodfaconverter.directconverter.syntaxtree.node.BinaryInnerNode;
import regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNode;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNodeCollection;
import utils.Test;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class RegexOperatorTree implements Tree, AttributizedOperatorTree {

	private AbstractSyntaxTree ast;
	
	private OperatorTreeAttributor operatorTreeAttributor = new OperatorTreeAttributor();
	
	private TreeNode terminatorNode;
	
	public RegexOperatorTree( String regex) throws Exception {
		super();
		ContextFreeGrammar regexGrammar = Grammars.getRegexGrammar();
		SyntaxDirectedDefinition regexSdd = getRegexSdd();
		extendGrammarAndSddWithTerminator( regexGrammar, regexSdd);
	  // extends regex string
		regex += RegexSpecialChars.TERMINATOR;
		ast = new AbstractSyntaxTree( regexGrammar, regexSdd, regex);
		operatorTreeAttributor.attributizeOperatorTree( this);
	}
	
	
	
	public static SyntaxDirectedDefinition getRegexSdd() {
		SyntaxDirectedDefinition result = new SyntaxDirectedDefinition();
		
		// we define a simple regex grammar for testing
		Nonterminal R = new Nonterminal( "R");
		Nonterminal S = new Nonterminal( "S");
		Nonterminal T = new Nonterminal( "T");
		Nonterminal U = new Nonterminal( "U");
		Nonterminal V = new Nonterminal( "V");
	
		final Terminal<Character> leftBracket = new Terminal<Character>( '(');
		final Terminal<Character> rightBracket = new Terminal<Character>( ')');
		final Terminal<Character> opKleeneClosure = new Terminal<Character>( '*');
		final Terminal<Character> opAlternative = new Terminal<Character>( '+');
		final Terminal<Character> opConcatenation = new Terminal<Character>( '.');
		
		// R -> R1 + S
		SemanticRules semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode nodeR = new OperatorNode( OperatorType.ALTERNATIVE);
				TreeNode nodeR1 = (TreeNode) attributesMaps[1].get( "node");
				TreeNode nodeS = (TreeNode) attributesMaps[3].get( "node");
				nodeR.setLeftChildNode( nodeR1);
				nodeR.setRightChildNode( nodeS);
	      attributesMaps[0].put( "node", nodeR);
			}
		});
		result.put( new ProductionRule(R, R, opAlternative, S), semanticRules);
		
	  // R -> S
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeS = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeS);
			}
		});
		result.put( new ProductionRule(R, S), semanticRules);

	  // S -> S1 . T
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode nodeS = new OperatorNode( OperatorType.CONCATENATION);		
				TreeNode nodeS1 = (TreeNode) attributesMaps[1].get( "node");
				TreeNode nodeT = (TreeNode) attributesMaps[3].get( "node");
				nodeS.setLeftChildNode( nodeS1);
				nodeS.setRightChildNode( nodeT);
				attributesMaps[0].put( "node", nodeS);
			}
		});
		result.put( new ProductionRule(S, S, opConcatenation, T), semanticRules);

		// S -> T
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeT = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeT);
			}
		});
		result.put( new ProductionRule(S, T), semanticRules);

		// T -> U*
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				RepetitionRange repetitionRange = new RepetitionRange( 0, Integer.MAX_VALUE);
				OperatorNode nodeT = new OperatorNode( OperatorType.REPETITION, repetitionRange);
				TreeNode nodeU = (TreeNode) attributesMaps[1].get( "node");
				nodeT.setLeftChildNode( nodeU);
				attributesMaps[0].put( "node", nodeT);
			}
		});
		result.put( new ProductionRule(T, U, opKleeneClosure), semanticRules);

		// T -> U
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeU = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeU);
			}
		});
		result.put( new ProductionRule(T, U), semanticRules);

		// U -> V
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeV = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeV);
			}
		});
		result.put( new ProductionRule(U, V), semanticRules);

		// U -> ( R )
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeR = (TreeNode) attributesMaps[2].get( "node");
				attributesMaps[0].put( "node", nodeR);
			}
		});
		result.put( new ProductionRule(U, leftBracket, R, rightBracket), semanticRules);

		// V -> a
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode<Character> nodeTerminal = new TerminalNode( (Character) attributesMaps[1].get( "value"));
				attributesMaps[0].put( "node", nodeTerminal);
			}
		});
		for ( Terminal terminal : Grammars.getRegexGrammar().getTerminals()) {
			result.put( new ProductionRule(V, terminal), semanticRules);
		}  
		return result;
	}
	
	
	
	/**
	 * Erweitert die Grammatik für reguläre Ausdrücke um das Terminatorsymbol.
   *
	 */
	private static void extendGrammarAndSddWithTerminator( Grammar grammar, SyntaxDirectedDefinition sdd) {
		// extends grammar
		Grammar extendedGrammar = grammar;
		Nonterminal embracingNonterminal = new Nonterminal();
		Terminal<Character> terminator = new Terminal<Character>( RegexSpecialChars.TERMINATOR);
		Nonterminal priorStartSymbol = extendedGrammar.getStartSymbol();
		ProductionRule terminatorProductionRule = new ProductionRule( embracingNonterminal, priorStartSymbol, terminator);
		extendedGrammar.addProduction( terminatorProductionRule);
		extendedGrammar.setStartSymbol( embracingNonterminal);
		
		// extends SDD
		final Terminal<Character> opConcatenation = new Terminal<Character>( '.');
		
		// embracingNonterminal -> previousStartSymbol
		SemanticRules semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode embracingNonterminalNode = new OperatorNode( OperatorType.CONCATENATION);
				TreeNode priorStartSymbolNode = (TreeNode) attributesMaps[1].get( "node");
				TreeNode terminatorNode = new TerminalNode( (Character) attributesMaps[2].get( "value"));
				embracingNonterminalNode.setLeftChildNode( priorStartSymbolNode);
				embracingNonterminalNode.setRightChildNode( terminatorNode);
	      attributesMaps[0].put( "node", embracingNonterminalNode);
			}
		});
		sdd.put( terminatorProductionRule, semanticRules);
	
	}



	public Iterator<TreeNode> iterator() {
		return new TreeIterator( this);
	}

	public TreeNode getRoot() {
		return (TreeNode) ast.getRootAttributesMap().get( "node");
	}

	public Grammar getGrammar() {
		return ast.getGrammar();
	}



	public HashMap<TreeNode, TreeNodeCollection> getFirstPositions() {
		return operatorTreeAttributor.getFirstPositions();
	}



	public HashMap<TreeNode, TreeNodeCollection> getFollowPositions() {
		return operatorTreeAttributor.getFollowPositions();
	}



	public HashMap<TreeNode, TreeNodeCollection> getLastPositions() {
		return operatorTreeAttributor.getLastPositions();
	}



	public HashMap<TreeNode, Boolean> getNullables() {
		return operatorTreeAttributor.getNullables();
	}
	
	public Collection<Leaf> getLeafSet() {
		Collection<Leaf> leafSet = new HashSet<Leaf>();
		for ( TreeNode node : this) {
			if ( Test.isAssigned( node) 
					&& node instanceof TerminalNode) {
				leafSet.add( (Leaf) node); 
			}
		}
		return leafSet;
	}



	public TreeNode getTerminatorNode() {
		for ( TreeNode node : this) {
			if ( Test.isAssigned( node) 
					&& node instanceof TerminalNode			  
					&& ((TerminalNode) node).getValue() == RegexSpecialChars.TERMINATOR)
				  return node;
			}
		return null;
	}
}

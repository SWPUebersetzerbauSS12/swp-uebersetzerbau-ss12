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


package de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.Slr1ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.EmptyString;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Grammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.RegexSpecialChars;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.AbstractSyntaxTree;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.AttributesMap;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.SemanticRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.SemanticRules;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.SyntaxDirectedDefinition;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.Tree;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.TreeIterator;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNodeCollection;
import de.fuberlin.bii.utils.Test;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class RegexOperatorTree<StatePayloadType extends Serializable> implements Tree, AttributizedOperatorTree {

	// definition of nonterminals
	private static final Nonterminal NONTERMINAL_R = new Nonterminal( "R");
	private static final Nonterminal NONTERMINAL_S = new Nonterminal( "S");
	private static final Nonterminal NONTERMINAL_SX = new Nonterminal( "SX");
	private static final Nonterminal NONTERMINAL_T = new Nonterminal( "T");
	private static final Nonterminal NONTERMINAL_TX = new Nonterminal( "TX");
	private static final Nonterminal NONTERMINAL_U = new Nonterminal( "U");
	private static final Nonterminal NONTERMINAL_UX = new Nonterminal( "UX");
	private static final Nonterminal NONTERMINAL_V = new Nonterminal( "V");

	private static final Nonterminal START_SYMBOL = NONTERMINAL_R;
	
		
	// definition of terminals
	private static final Terminal<RegularExpressionElement> TERMINAL_TERMINATOR = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexSpecialChars.TERMINATOR));
	private static final Terminal<RegularExpressionElement> TERMINAL_LEFT_BRACKET = new Terminal<RegularExpressionElement>(  new RegularExpressionElement( '('));
	private static final Terminal<RegularExpressionElement> TERMINAL_RIGHT_BRACKET_TERMINAL = new Terminal<RegularExpressionElement>(  new RegularExpressionElement( ')'));
	private static final Terminal<RegularExpressionElement> OPERATOR_KLEENE_CLOSURE = new Terminal<RegularExpressionElement>(  new RegularExpressionElement( '*'));
	private static final Terminal<RegularExpressionElement> OPERATOR_ALTERNATIVE = new Terminal<RegularExpressionElement>(  new RegularExpressionElement( '|'));
	private static final Terminal<RegularExpressionElement> EMPTY_STRING = new EmptyString();
	private static final Terminal<RegularExpressionElement> OPERATOR_CONCATENATION = new Terminal<RegularExpressionElement>(  new RegularExpressionElement( '.'));

	// definitions of productions
	private static final ProductionRule PRODUCTION_REGEX_ALTERNATIVE = new ProductionRule(NONTERMINAL_R, NONTERMINAL_R, OPERATOR_ALTERNATIVE, NONTERMINAL_S);
	private static final ProductionRule PRODUCTION_REGEX_ALTERNATIVE_BYPASS = new ProductionRule(NONTERMINAL_R, NONTERMINAL_S);
	
	//private static final ProductionRule PRODUCTION_REGEX_CONCATENATION = new ProductionRule(NONTERMINAL_S, NONTERMINAL_S, OPERATOR_CONCATENATION, NONTERMINAL_T);
	private static final ProductionRule PRODUCTION_REGEX_CONCATENATION = new ProductionRule(NONTERMINAL_S, NONTERMINAL_SX, NONTERMINAL_T);
	private static final ProductionRule PRODUCTION_REGEX_CONCATENATION_STEADY = new ProductionRule(NONTERMINAL_SX, NONTERMINAL_SX, NONTERMINAL_TX);
	
	private static final ProductionRule PRODUCTION_REGEX_CONCATENATION_BYPASS = new ProductionRule(NONTERMINAL_S, NONTERMINAL_T);
	private static final ProductionRule PRODUCTION_REGEX_CONCATENATION_STEADY_BYPASS = new ProductionRule(NONTERMINAL_SX, NONTERMINAL_TX);
	
	private static final ProductionRule PRODUCTION_REGEX_KLEENE_CLOSURE = new ProductionRule(NONTERMINAL_T, NONTERMINAL_U, OPERATOR_KLEENE_CLOSURE);
	private static final ProductionRule PRODUCTION_REGEX_KLEENE_CLOSURE_STEADY = new ProductionRule(NONTERMINAL_TX, NONTERMINAL_UX, OPERATOR_KLEENE_CLOSURE);
	
	private static final ProductionRule PRODUCTION_REGEX_KLEENE_CLOSURE_BYPASS = new ProductionRule(NONTERMINAL_T, NONTERMINAL_U);
	private static final ProductionRule PRODUCTION_REGEX_KLEENE_CLOSURE_STEADY_BYPASS = new ProductionRule(NONTERMINAL_TX, NONTERMINAL_UX);
	
	private static final ProductionRule PRODUCTION_REGEX_BRACKET = new ProductionRule(NONTERMINAL_U, TERMINAL_LEFT_BRACKET, NONTERMINAL_R, TERMINAL_RIGHT_BRACKET_TERMINAL);
	private static final ProductionRule PRODUCTION_REGEX_BRACKET_STEADY = new ProductionRule(NONTERMINAL_UX, TERMINAL_LEFT_BRACKET, NONTERMINAL_R, TERMINAL_RIGHT_BRACKET_TERMINAL);
	
	private static final ProductionRule PRODUCTION_REGEX_BRACKET_BYPASS = new ProductionRule(NONTERMINAL_U, NONTERMINAL_V);
	private static final ProductionRule PRODUCTION_REGEX_BRACKET_STEADY_BYPASS = new ProductionRule(NONTERMINAL_UX, NONTERMINAL_V);
	private static final ProductionRule PRODUCTION_REGEX_EMPTY_STRING = new ProductionRule(NONTERMINAL_U, EMPTY_STRING);
	
	
	
	private AbstractSyntaxTree ast;
	
	private OperatorTreeAttributor<StatePayloadType> operatorTreeAttributor = new OperatorTreeAttributor<StatePayloadType>();
	
	private TreeNode terminatorNode;
	
	public RegexOperatorTree( RegularExpressionElement<StatePayloadType>[] regularExpression) throws Exception {
		super();
		ContextFreeGrammar regexGrammar = getRegexGrammar();
		SyntaxDirectedDefinition regexSdd = getRegexSdd();
		extendGrammarAndSddWithTerminator( regexGrammar, regexSdd);
	  // extends regex string
		regularExpression = Arrays.copyOf( regularExpression, regularExpression.length +1);
		regularExpression[regularExpression.length -1] = new RegularExpressionElement( RegexSpecialChars.TERMINATOR, null);
		ast = new AbstractSyntaxTree( regexGrammar, regexSdd, regularExpression) {
			@Override
			protected ItemAutomat getNewItemAutomat( Grammar grammar) {
				return new Slr1ItemAutomat<Symbol>( (ContextFreeGrammar) grammar);
			}
		};
		operatorTreeAttributor.attributizeOperatorTree( this);
	}
	
	
	
	public static ContextFreeGrammar getRegexGrammar() {
		ContextFreeGrammar grammar = new ContextFreeGrammar();

		// we define valid chars 
		ArrayList<Terminal<RegularExpressionElement>>  terminals = new ArrayList<Terminal<RegularExpressionElement>>(); 
    // a..z
		for ( int c = 'a'; c <= 'z'; c++) {
			terminals.add(new Terminal<RegularExpressionElement>( new RegularExpressionElement( (char) c)));
		}
		// A..Z
		for ( int c = 'A'; c <= 'Z'; c++) {
			terminals.add(new Terminal<RegularExpressionElement>( new RegularExpressionElement( (char) c)));
		}
	  // 0..9
		for ( int c = '0'; c <= '9'; c++) {
			terminals.add(new Terminal<RegularExpressionElement>( new RegularExpressionElement( (char) c)));
		}
		// add further chars
		char[] furtherChars = { '!', '"', '#', '$' , '%', '&', '\'', '`', '´', '-', '_', ',', ';', '.', ':', '/', '\\', '?', '=', ']', '[', '{', '}', '^', '°', '<', '>', '~', '+'};
		for ( char c : furtherChars) {
			terminals.add(new Terminal<RegularExpressionElement>( new RegularExpressionElement( c)));	
		}	
		

		
		ProductionSet productions = new ProductionSet();
		productions.add( PRODUCTION_REGEX_ALTERNATIVE);
		productions.add( PRODUCTION_REGEX_ALTERNATIVE_BYPASS);
		productions.add( PRODUCTION_REGEX_CONCATENATION);
		productions.add( PRODUCTION_REGEX_CONCATENATION_STEADY);
		productions.add( PRODUCTION_REGEX_CONCATENATION_BYPASS);
		productions.add( PRODUCTION_REGEX_CONCATENATION_STEADY_BYPASS);
		productions.add( PRODUCTION_REGEX_KLEENE_CLOSURE);
		productions.add( PRODUCTION_REGEX_KLEENE_CLOSURE_STEADY);
		productions.add( PRODUCTION_REGEX_KLEENE_CLOSURE_BYPASS);
		productions.add( PRODUCTION_REGEX_KLEENE_CLOSURE_STEADY_BYPASS);
		productions.add( PRODUCTION_REGEX_BRACKET);
		productions.add( PRODUCTION_REGEX_BRACKET_STEADY);
		productions.add( PRODUCTION_REGEX_BRACKET_BYPASS);
		productions.add( PRODUCTION_REGEX_BRACKET_STEADY_BYPASS);
	  productions.add( PRODUCTION_REGEX_EMPTY_STRING);
		for ( Terminal<RegularExpressionElement> terminal : terminals) {
			productions.add( new ProductionRule(NONTERMINAL_V, terminal));	
		}
		// TODO: Regex Grammatik noch unvollständig
		
		grammar.addAll( productions);
		grammar.setStartSymbol( START_SYMBOL);

		return grammar;
	}
	
	
	public static SyntaxDirectedDefinition getRegexSdd() {
		SyntaxDirectedDefinition result = new SyntaxDirectedDefinition();
		
		// R -> R1 | S
		SemanticRules semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode nodeR = new OperatorNode( OperatorType.ALTERNATIVE);
				TreeNode nodeR1 = (TreeNode) attributesMaps[1].get( "node");	
				TreeNode nodeS = (TreeNode) attributesMaps[3].get( "node");
				Object payload = ((Symbol) attributesMaps[2].get( "value")).getPayload();
				tryPassPayloadDownwards( payload, nodeR1, nodeS);
				nodeR.setLeftChildNode( nodeR1);
				nodeR.setRightChildNode( nodeS);
	      attributesMaps[0].put( "node", nodeR);
			}
		});
		result.put( PRODUCTION_REGEX_ALTERNATIVE, semanticRules);
		
	  // R -> S
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeS = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeS);
			}
		});
		result.put( PRODUCTION_REGEX_ALTERNATIVE_BYPASS, semanticRules);

	  // S -> SX T
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode nodeS = new OperatorNode( OperatorType.CONCATENATION);		
				TreeNode nodeS0 = (TreeNode) attributesMaps[1].get( "node");
				TreeNode nodeT = (TreeNode) attributesMaps[2].get( "node");
				//Object payload = ((Symbol) attributesMaps[2].get( "value")).getPayload();
				//tryPassPayloadDownwards( payload, nodeT);
				nodeS.setLeftChildNode( nodeS0);
				nodeS.setRightChildNode( nodeT);
				attributesMaps[0].put( "node", nodeS);
			}
		});
		result.put( PRODUCTION_REGEX_CONCATENATION, semanticRules);
		// alike SX -> SX1 T
		result.put( PRODUCTION_REGEX_CONCATENATION_STEADY, semanticRules);
		
		
		// S -> T
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeT = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeT);
			}
		});
		result.put( PRODUCTION_REGEX_CONCATENATION_BYPASS, semanticRules);
	  // alike SX -> TX
		result.put( PRODUCTION_REGEX_CONCATENATION_STEADY_BYPASS, semanticRules);
	  
		
		// T -> U*
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				RepetitionRange repetitionRange = new RepetitionRange( 0, Integer.MAX_VALUE);
				OperatorNode nodeT = new OperatorNode( OperatorType.REPETITION, repetitionRange);
				TreeNode nodeU = (TreeNode) attributesMaps[1].get( "node");
				Object payload = ((Symbol) attributesMaps[2].get( "value")).getPayload();
				tryPassPayloadDownwards( payload, nodeU);
				nodeT.setLeftChildNode( nodeU);
				attributesMaps[0].put( "node", nodeT);
			}
		});
		result.put( PRODUCTION_REGEX_KLEENE_CLOSURE, semanticRules);
	  // alike TX -> UX*
		result.put( PRODUCTION_REGEX_KLEENE_CLOSURE_STEADY, semanticRules);

		
		// T -> U
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeU = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeU);
			}
		});
		result.put( PRODUCTION_REGEX_KLEENE_CLOSURE_BYPASS, semanticRules);
	  // alike TX -> UX
		result.put( PRODUCTION_REGEX_KLEENE_CLOSURE_STEADY_BYPASS, semanticRules);

		// U -> V
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeV = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeV);
			}
		});
		result.put( PRODUCTION_REGEX_BRACKET_BYPASS, semanticRules);
	  // alike UX -> V
		result.put( PRODUCTION_REGEX_BRACKET_STEADY_BYPASS, semanticRules);


		// U -> ( R )
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeR = (TreeNode) attributesMaps[2].get( "node");
				Object payload = ((Symbol) attributesMaps[3].get( "value")).getPayload();
				tryPassPayloadDownwards( payload, nodeR);
				attributesMaps[0].put( "node", nodeR);
			}
		});
		result.put( PRODUCTION_REGEX_BRACKET, semanticRules);
	  // alike UX -> ( R ) 
		result.put( PRODUCTION_REGEX_BRACKET_STEADY, semanticRules);

	  // U -> \epsilon
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
		  public void apply( AttributesMap... attributesMaps) {
				TreeNode<Symbol> nodeTerminal = new TerminalNode( new RegularExpressionElement( RegexSpecialChars.EMPTY_STRING));
				attributesMaps[0].put( "node", nodeTerminal);
			}
		});
		result.put( PRODUCTION_REGEX_EMPTY_STRING, semanticRules);

		// V -> a
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode<Symbol> nodeTerminal = new TerminalNode( (Symbol) attributesMaps[1].get( "value"));
				attributesMaps[0].put( "node", nodeTerminal);
			}
		});
		for ( Terminal terminal : getRegexGrammar().getTerminals()) {
			result.put( new ProductionRule(NONTERMINAL_V, terminal), semanticRules);
		}  
		return result;
	}
	
	
	
	protected static void tryPassPayloadDownwards( Object payload, TreeNode ... nodes) {
		for ( TreeNode node : nodes) {
			if ( node instanceof OperatorNode) {
				OperatorNode operatorNode = (OperatorNode) node;
				switch ( operatorNode.getOperatorType()) {
					case ALTERNATIVE: 
						tryPassPayloadDownwards( payload, operatorNode.getLeftChildNode(), operatorNode.getRightChildNode());
						break;
					case CONCATENATION: 
						tryPassPayloadDownwards( payload, operatorNode.getRightChildNode());
						break;
					case REPETITION: 
						tryPassPayloadDownwards( payload, operatorNode.getLeftChildNode());
						break;	
				}
			} else if ( node instanceof TerminalNode) {
				TerminalNode terminalNode = (TerminalNode) node;
				Symbol terminalSymbol =  terminalNode.getValue();
				if ( Test.isAssigned( terminalSymbol) 
						&& Test.isUnassigned( terminalSymbol.getPayload()))
					terminalSymbol.setPayload( payload);
			}
		}
	}



	/**
	 * Erweitert die Grammatik für reguläre Ausdrücke um das Terminatorsymbol.
   *
	 */
	private static void extendGrammarAndSddWithTerminator( Grammar grammar, SyntaxDirectedDefinition sdd) {
		// extends grammar
		Grammar extendedGrammar = grammar;
		Nonterminal embracingNonterminal = new Nonterminal();
		Nonterminal priorStartSymbol = extendedGrammar.getStartSymbol();
	  // end rule
		ProductionRule terminatorProductionRule = new ProductionRule( embracingNonterminal, priorStartSymbol, TERMINAL_TERMINATOR);
		extendedGrammar.addProduction( terminatorProductionRule);
		extendedGrammar.setStartSymbol( embracingNonterminal);
		
		
		// embracingNonterminal -> previousStartSymbol
		SemanticRules semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode embracingNonterminalNode = new OperatorNode( OperatorType.CONCATENATION);
				TreeNode priorStartSymbolNode = (TreeNode) attributesMaps[1].get( "node");
				TreeNode<Symbol> terminatorNode = new TerminalNode( (Symbol) attributesMaps[2].get( "value"));
				embracingNonterminalNode.setLeftChildNode( priorStartSymbolNode);
				embracingNonterminalNode.setRightChildNode( terminatorNode);
	      attributesMaps[0].put( "node", embracingNonterminalNode);
				System.out.println( "Q " + ((InnerNode)embracingNonterminalNode).toFullString());
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
					&& Test.isAssigned( node.getValue())
					&& node.getValue() instanceof Symbol 
					&& ((Symbol) node.getValue()).equals( TERMINAL_TERMINATOR.getSymbol()))
			  return node;
			}
		return null;
	}
	
	@Override
	public String toString() {
		return ast.toString();
	}
	
}

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
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.Lr1ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.Slr1ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.EmptyString;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Grammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.RegexCharSet;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.RegexSection;
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
	private static final Nonterminal NONTERMINAL_T = new Nonterminal( "T");
	private static final Nonterminal NONTERMINAL_U = new Nonterminal( "U");
	private static final Nonterminal NONTERMINAL_V = new Nonterminal( "V");
	
	private static final Nonterminal NONTERMINAL_CLASS_SIGNUM = new Nonterminal( "CS");
	private static final Nonterminal NONTERMINAL_CLASS_FIRST_ELEMENT = new Nonterminal( "CF");
	private static final Nonterminal NONTERMINAL_CLASS_ELEMENTS = new Nonterminal( "CE");
	private static final Nonterminal NONTERMINAL_CLASS_RANGE = new Nonterminal( "CR");
	private static final Nonterminal NONTERMINAL_CLASS_FIRST_RANGE_BYPASS = new Nonterminal( "CFP");
	private static final Nonterminal NONTERMINAL_CLASS_ELEMENTS_BYPASS = new Nonterminal( "CEP");
	private static final Nonterminal NONTERMINAL_CV = new Nonterminal( "CV");
	private static final Nonterminal NONTERMINAL_FIRST_CV = new Nonterminal( "CFV");

	private static final Nonterminal START_SYMBOL = NONTERMINAL_R;
	

	// definition of terminals
	private static final Terminal<RegularExpressionElement> TERMINAL_TERMINATOR = new Terminal<RegularExpressionElement>( new RegularExpressionElement(
			RegexCharSet.TERMINATOR));
	private static final Terminal<RegularExpressionElement> BRACKET_LEFT_GROUP = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_GROUP_BEGIN));
	private static final Terminal<RegularExpressionElement> BRACKET_RIGHT_GROUP = new Terminal<RegularExpressionElement>(
			new RegularExpressionElement( RegexCharSet.REGEX_GROUP_END));
	private static final Terminal<RegularExpressionElement> BRACKET_LEFT_CLASS = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_CLASS_BEGIN));
	private static final Terminal<RegularExpressionElement> BRACKET_RIGHT_CLASS = new Terminal<RegularExpressionElement>(
			new RegularExpressionElement( RegexCharSet.REGEX_CLASS_END));
	private static final Terminal<RegularExpressionElement> TERMINAL_MASK = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_MASK));
	private static final Terminal<RegularExpressionElement> QUANTIFIER_KLEENE_CLOSURE = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_KLEENE_CLOSURE));
	private static final Terminal<RegularExpressionElement> QUANTIFIER_POSITIVE_CLOSURE = new Terminal<RegularExpressionElement>(
			new RegularExpressionElement( RegexCharSet.REGEX_POSITIVE_CLOSURE));
	private static final Terminal<RegularExpressionElement> QUANTIFIER_OPTION = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_OPTION));
	private static final Terminal<RegularExpressionElement> BRACKET_LEFT_QUANTIFIER = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_REPETITION_BEGIN));
	private static final Terminal<RegularExpressionElement> BRACKET_RIGHT_QUANTIFIER = new Terminal<RegularExpressionElement>(
			new RegularExpressionElement( RegexCharSet.REGEX_REPETITION_END));
	
	private static final Terminal<RegularExpressionElement> OPERATOR_ALTERNATIVE = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_ALTERNATIVE));
	private static final Terminal<RegularExpressionElement> EMPTY_STRING = new EmptyString();
	//private static final Terminal<RegularExpressionElement> OPERATOR_CONCATENATION = new Terminal<RegularExpressionElement>( new RegularExpressionElement( '.'));
	private static final Terminal<RegularExpressionElement> CLASSIFIER_JOKER = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_JOKER));
	private static final Terminal<RegularExpressionElement> CLASSIFIER_CLASS_SIGNUM = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_CLASS_SIGNUM));
	private static final Terminal<RegularExpressionElement> OPERATOR_RANGE = new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_RANGE));
	
	
	
	
	// definitions of productions
	
	// Priority level 0 (Alternative = highest)
	private static final ProductionRule PRODUCTION_REGEX_ALTERNATIVE = new ProductionRule(NONTERMINAL_R, NONTERMINAL_R, OPERATOR_ALTERNATIVE, NONTERMINAL_S);
	private static final ProductionRule PRODUCTION_REGEX_ALTERNATIVE_BYPASS = new ProductionRule(NONTERMINAL_R, NONTERMINAL_S);
	
  //Priority level 1 (Concatenation)
	private static final ProductionRule PRODUCTION_REGEX_CONCATENATION = new ProductionRule(NONTERMINAL_S, NONTERMINAL_S, NONTERMINAL_T);
	private static final ProductionRule PRODUCTION_REGEX_CONCATENATION_BYPASS = new ProductionRule(NONTERMINAL_S, NONTERMINAL_T);
	private static final ProductionRule PRODUCTION_REGEX_EMPTY_STRING = new ProductionRule(NONTERMINAL_S, EMPTY_STRING);
  //Priority level 2 (Repetition)
	private static final ProductionRule PRODUCTION_REGEX_KLEENE_CLOSURE = new ProductionRule(NONTERMINAL_T, NONTERMINAL_U, QUANTIFIER_KLEENE_CLOSURE);
	private static final ProductionRule PRODUCTION_REGEX_POSITIVE_CLOSURE = new ProductionRule(NONTERMINAL_T, NONTERMINAL_U, QUANTIFIER_POSITIVE_CLOSURE);
	private static final ProductionRule PRODUCTION_REGEX_OPTION = new ProductionRule(NONTERMINAL_T, NONTERMINAL_U, QUANTIFIER_OPTION);
	//private static final ProductionRule PRODUCTION_REGEX_REPETITION = new ProductionRule(NONTERMINAL_T, NONTERMINAL_U, QUANTIFIER_OPTION);
	private static final ProductionRule PRODUCTION_REGEX_REPETITION_BYPASS = new ProductionRule(NONTERMINAL_T, NONTERMINAL_U);

  //Priority level 3 (Enclosure)
	// 3.1: Grouping
	private static final ProductionRule PRODUCTION_REGEX_GROUP = new ProductionRule(NONTERMINAL_U, BRACKET_LEFT_GROUP, NONTERMINAL_R, BRACKET_RIGHT_GROUP);
	// 3.1 Character class definition
	private static final ProductionRule PRODUCTION_REGEX_CLASS = new ProductionRule(NONTERMINAL_U, BRACKET_LEFT_CLASS, NONTERMINAL_CLASS_SIGNUM, NONTERMINAL_CLASS_FIRST_ELEMENT, NONTERMINAL_CLASS_ELEMENTS, BRACKET_RIGHT_CLASS);
	private static final ProductionRule PRODUCTION_REGEX_CLASS_SINGLE = new ProductionRule(NONTERMINAL_U, BRACKET_LEFT_CLASS, NONTERMINAL_CLASS_SIGNUM, NONTERMINAL_CLASS_FIRST_ELEMENT, BRACKET_RIGHT_CLASS);
	// 3.1.1 Invert class definition or not
	private static final ProductionRule PRODUCTION_REGEX_CLASS_SIGNUM_INVERT = new ProductionRule(NONTERMINAL_CLASS_SIGNUM, CLASSIFIER_CLASS_SIGNUM);
	private static final ProductionRule PRODUCTION_REGEX_CLASS_SIGNUM_RIGHT = new ProductionRule(NONTERMINAL_CLASS_SIGNUM, EMPTY_STRING);
	// 3.1.2 at least one character is expected to define a class 
	private static final ProductionRule PRODUCTION_REGEX_CLASS_FIRST_RANGE_ELEMENT = new ProductionRule(NONTERMINAL_CLASS_FIRST_ELEMENT, NONTERMINAL_FIRST_CV, OPERATOR_RANGE, NONTERMINAL_CV);
	private static final ProductionRule PRODUCTION_REGEX_CLASS_FIRST_RANGE_BYPASS = new ProductionRule(NONTERMINAL_CLASS_FIRST_ELEMENT, NONTERMINAL_CLASS_FIRST_RANGE_BYPASS);
	private static final ProductionRule PRODUCTION_REGEX_CLASS_FIRST_SINGLE_ELEMENT = new ProductionRule( NONTERMINAL_CLASS_FIRST_RANGE_BYPASS, NONTERMINAL_FIRST_CV);
	
	private static final ProductionRule PRODUCTION_REGEX_CLASS_RANGE_ELEMENTS = new ProductionRule(NONTERMINAL_CLASS_ELEMENTS, NONTERMINAL_CLASS_ELEMENTS, NONTERMINAL_CLASS_RANGE);
	private static final ProductionRule PRODUCTION_REGEX_CLASS_RANGE_ELEMENTS_FINAL = new ProductionRule(NONTERMINAL_CLASS_ELEMENTS, NONTERMINAL_CLASS_RANGE);
	private static final ProductionRule PRODUCTION_REGEX_CLASS_RANGE_ELEMENTS_BYPASS = new ProductionRule(NONTERMINAL_CLASS_ELEMENTS, NONTERMINAL_CLASS_ELEMENTS_BYPASS);
	private static final ProductionRule PRODUCTION_REGEX_CLASS_BYPASS_ELEMENTS = new ProductionRule(NONTERMINAL_CLASS_ELEMENTS, NONTERMINAL_CLASS_ELEMENTS, NONTERMINAL_CLASS_ELEMENTS_BYPASS);
	private static final ProductionRule PRODUCTION_REGEX_CLASS_RANGE = new ProductionRule(NONTERMINAL_CLASS_RANGE, NONTERMINAL_CV, OPERATOR_RANGE, NONTERMINAL_CV);
	private static final ProductionRule PRODUCTION_REGEX_CLASS_SIGLE_ELEMENT = new ProductionRule(NONTERMINAL_CLASS_ELEMENTS_BYPASS, NONTERMINAL_CV);

	
	// 3.n bypass enclosure
	private static final ProductionRule PRODUCTION_REGEX_BRACKET_BYPASS = new ProductionRule(NONTERMINAL_U, NONTERMINAL_V);
	
	
	
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
		regularExpression[regularExpression.length -1] = new RegularExpressionElement( RegexCharSet.TERMINATOR, null);
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

		
		

		
		
		ProductionSet productions = new ProductionSet();
	  // Priority level 0 (Alternative = highest)
		productions.add( PRODUCTION_REGEX_ALTERNATIVE);
		productions.add( PRODUCTION_REGEX_ALTERNATIVE_BYPASS);
	  // Priority level 1 (Concatenation)
		productions.add( PRODUCTION_REGEX_CONCATENATION);
		productions.add( PRODUCTION_REGEX_CONCATENATION_BYPASS);
		productions.add( PRODUCTION_REGEX_EMPTY_STRING);
	  // Priority level 2 (Repetition)
		productions.add( PRODUCTION_REGEX_KLEENE_CLOSURE);
		productions.add( PRODUCTION_REGEX_POSITIVE_CLOSURE);
		productions.add( PRODUCTION_REGEX_OPTION);
		//productions.add( PRODUCTION_REGEX_REPETITION);
		productions.add( PRODUCTION_REGEX_REPETITION_BYPASS);
	  // Priority level 3 (Enclosure)
		productions.add( PRODUCTION_REGEX_GROUP);
		// 3.1 Character class definition
		productions.add( PRODUCTION_REGEX_CLASS);
		productions.add( PRODUCTION_REGEX_CLASS_SINGLE);
		// 3.1.1 Invert class definition or not
		productions.add( PRODUCTION_REGEX_CLASS_SIGNUM_INVERT);
		productions.add( PRODUCTION_REGEX_CLASS_SIGNUM_RIGHT);
		// 3.1.2 at least one character is expected to define a class 
	  productions.add( PRODUCTION_REGEX_CLASS_FIRST_RANGE_ELEMENT);
	  productions.add( PRODUCTION_REGEX_CLASS_FIRST_RANGE_BYPASS);
		productions.add( PRODUCTION_REGEX_CLASS_FIRST_SINGLE_ELEMENT);
		
		productions.add( PRODUCTION_REGEX_CLASS_RANGE_ELEMENTS);
		productions.add( PRODUCTION_REGEX_CLASS_RANGE_ELEMENTS_FINAL);
		productions.add( PRODUCTION_REGEX_CLASS_RANGE_ELEMENTS_BYPASS);
		productions.add( PRODUCTION_REGEX_CLASS_RANGE);
		productions.add( PRODUCTION_REGEX_CLASS_BYPASS_ELEMENTS);
		productions.add( PRODUCTION_REGEX_CLASS_SIGLE_ELEMENT);
				
		productions.add( PRODUCTION_REGEX_BRACKET_BYPASS);

	  
	  // MAIN Terminals
	  for ( char c : RegexCharSet.getUnguardedCharsOfContext( RegexSection.MAIN)) {
			Terminal<RegularExpressionElement> terminal = new Terminal<RegularExpressionElement>( new RegularExpressionElement( (char) c));
			productions.add( new ProductionRule(NONTERMINAL_V, terminal));				
	  }
		
	  for ( char c : RegexCharSet.getMetaCharsOfContext( RegexSection.MAIN)) {
			Terminal<RegularExpressionElement> metaTerminal = new Terminal<RegularExpressionElement>( new RegularExpressionElement( (char) c));
			productions.add( new ProductionRule(NONTERMINAL_V, TERMINAL_MASK, metaTerminal));	
		}
	   
		
	  // CLASS Terminals
	  for ( char c : RegexCharSet.getUnguardedCharsOfContext( RegexSection.CHARACTER_CLASS)) {
			Terminal<RegularExpressionElement> terminal = new Terminal<RegularExpressionElement>( new RegularExpressionElement( (char) c));
			productions.add( new ProductionRule(NONTERMINAL_CV, terminal));
			productions.add( new ProductionRule(NONTERMINAL_FIRST_CV, terminal));
	  }
	  productions.add( new ProductionRule(NONTERMINAL_CV, new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_RANGE))));
	  productions.add( new ProductionRule(NONTERMINAL_CV, new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_CLASS_SIGNUM))));
	  productions.add( new ProductionRule(NONTERMINAL_CV, TERMINAL_MASK, new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_CLASS_END))));
	  productions.add( new ProductionRule(NONTERMINAL_FIRST_CV, new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_RANGE))));
	  productions.add( new ProductionRule(NONTERMINAL_FIRST_CV, TERMINAL_MASK, new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_CLASS_SIGNUM))));
	  productions.add( new ProductionRule(NONTERMINAL_FIRST_CV, TERMINAL_MASK, new Terminal<RegularExpressionElement>( new RegularExpressionElement( RegexCharSet.REGEX_CLASS_END))));
			  
	  
		// TODO: Regex Grammatik noch unvollständig
		
		grammar.addAll( productions);
		grammar.setStartSymbol( START_SYMBOL);

		return grammar;
	}
	
	
	public static SyntaxDirectedDefinition getRegexSdd() {
		SyntaxDirectedDefinition result = new SyntaxDirectedDefinition();
		
		// ++++++++++++++++++++++++++++++
		// Priority level 0 (Alternative)
		// ++++++++++++++++++++++++++++++
		
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

		
		// ++++++++++++++++++++++++++++++++
		// Priority level 1 (Concatenation)
		// ++++++++++++++++++++++++++++++++
		
	  // S -> S1 T
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode nodeS = new OperatorNode( OperatorType.CONCATENATION);		
				TreeNode nodeS1 = (TreeNode) attributesMaps[1].get( "node");
				TreeNode nodeT = (TreeNode) attributesMaps[2].get( "node");
				nodeS.setLeftChildNode( nodeS1);
				nodeS.setRightChildNode( nodeT);
				attributesMaps[0].put( "node", nodeS);
			}
		});
		result.put( PRODUCTION_REGEX_CONCATENATION, semanticRules);
		
		
		// S -> T
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeT = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeT);
			}
		});
		result.put( PRODUCTION_REGEX_CONCATENATION_BYPASS, semanticRules);
		
		// -----------------------------
		// Empty string
		// -----------------------------
		
	  // S -> \epsilon
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
		  public void apply( AttributesMap... attributesMaps) {
				TreeNode<Symbol> nodeTerminal = new TerminalNode( new RegularExpressionElement( RegexCharSet.EMPTY_STRING));
				attributesMaps[0].put( "node", nodeTerminal);
			}
		});
		result.put( PRODUCTION_REGEX_EMPTY_STRING, semanticRules);

		

		// +++++++++++++++++++++++++++++
		// Priority level 2 (Repetition)
		// +++++++++++++++++++++++++++++
		
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

	  // T -> U+  (positive closure)
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode nodeT = new OperatorNode( OperatorType.CONCATENATION);
				TreeNode nodeU = (TreeNode) attributesMaps[1].get( "node");
				Object payload = ((Symbol) attributesMaps[2].get( "value")).getPayload();
				tryPassPayloadDownwards( payload, nodeU);
				// The first fix repetition 
				nodeT.setLeftChildNode( nodeU);
				// All following optional repetitions
				RepetitionRange repetitionRange = new RepetitionRange( 0, Integer.MAX_VALUE);
				OperatorNode nodeKleeneClosure = new OperatorNode( OperatorType.REPETITION, repetitionRange);
				nodeKleeneClosure.setLeftChildNode( nodeU);
				nodeT.setRightChildNode( nodeKleeneClosure);
				// return new positive closure node
				attributesMaps[0].put( "node", nodeT);
			}
		});
		result.put( PRODUCTION_REGEX_POSITIVE_CLOSURE, semanticRules);		
		
		// T -> U?  (option)
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode nodeT = new OperatorNode( OperatorType.ALTERNATIVE);
				TreeNode nodeU = (TreeNode) attributesMaps[1].get( "node");
				Object payload = ((Symbol) attributesMaps[2].get( "value")).getPayload();
				tryPassPayloadDownwards( payload, nodeU);
				// The possibility to accept the empty string
				nodeT.setLeftChildNode( new TerminalNode( new RegularExpressionElement( RegexCharSet.EMPTY_STRING)));
				// or to accept the expression in U
				nodeT.setRightChildNode( nodeU);
				// return new option node
				attributesMaps[0].put( "node", nodeT);
			}
		});
		result.put( PRODUCTION_REGEX_OPTION, semanticRules);		

			
		// T -> U   (bypass)
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeU = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeU);
			}
		});
		result.put( PRODUCTION_REGEX_REPETITION_BYPASS, semanticRules);
		

		
		
		// +++++++++++++++++++++++++++++
		// Priority level 3 (Enclosure)
		// +++++++++++++++++++++++++++++
		
		// U -> V  (bypass)     
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode nodeV = (TreeNode) attributesMaps[1].get( "node");
				attributesMaps[0].put( "node", nodeV);
			}
		});
		result.put( PRODUCTION_REGEX_BRACKET_BYPASS, semanticRules);
	  

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
		result.put( PRODUCTION_REGEX_GROUP, semanticRules);
	  

		// -----------------------------
		// 3.1  Character class definition
		// -----------------------------

		
	  // U -> [ CS CF CE ] and U -> [ CS CF ]
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				OperatorNode nodeU = new OperatorNode( OperatorType.ALTERNATIVE);
				Boolean buildComplementClass = (Boolean) attributesMaps[2].get( "complement");
				
				// determin the common payload
				Object commonPayload = ((Symbol) attributesMaps[1].get( "value")).getPayload();
				if ( Test.isUnassigned( commonPayload))
					commonPayload = (Object) attributesMaps[2].get( "payload");
				
				
				
				List<Symbol> values = (List<Symbol>) attributesMaps[3].get( "values");

				// in case of U -> [ CS CF CE ]
				if ( attributesMaps.length > 5 &&  Test.isAssigned( attributesMaps[4])) {
					values.addAll( (List<Symbol>) attributesMaps[4].get( "values"));
					if ( Test.isUnassigned( commonPayload))
						commonPayload = ((Symbol) attributesMaps[5].get( "value")).getPayload();
				} else { // in case of U -> [ CS CF ]
					if ( Test.isUnassigned( commonPayload))
						commonPayload = ((Symbol) attributesMaps[4].get( "value")).getPayload();
				}
				
				
				if ( buildComplementClass) {
					// build the complement class by exclusion of all chars mentioned in list values
					// and add the determine common payload to each of them
					List<Symbol> complementValues = new ArrayList<Symbol>();
					for ( char c = RegexCharSet.getFirstAsciiChar(); c <= RegexCharSet.getLastAsciiChar(); c++) {
						RegularExpressionElement complementCharacterCandidate = new RegularExpressionElement( c, commonPayload);
						if ( !values.contains( complementCharacterCandidate))
							complementValues.add( complementCharacterCandidate);
					} 
					values = complementValues;
				} else {
					// otherwise, we use the given values. Finally we add the common payload to all that unassigned 
					for ( Symbol value : values) {
						if ( Test.isUnassigned( value.getPayload()))
							value.setPayload( commonPayload);
					}
				}

				// now convert the list to a chain of alternatives 
				Queue<Symbol> valuesQueue = new ArrayBlockingQueue<Symbol>( values.size());
				valuesQueue.addAll( values);
				TreeNode relRootNode = new TerminalNode( valuesQueue.poll());
				while ( !valuesQueue.isEmpty()) {
					TreeNode leftChild = relRootNode;
					TreeNode rightChild = new TerminalNode( valuesQueue.poll());
					relRootNode = new OperatorNode( OperatorType.ALTERNATIVE);
					((OperatorNode) relRootNode).setLeftChildNode( leftChild);
					((OperatorNode) relRootNode).setRightChildNode( rightChild);
				}
				attributesMaps[0].put( "node", relRootNode);
			}
		});
		result.put( PRODUCTION_REGEX_CLASS, semanticRules);
		result.put( PRODUCTION_REGEX_CLASS_SINGLE, semanticRules);
		
		// CS -> ^
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				Object payload = ((Symbol) attributesMaps[2].get( "value")).getPayload();
				attributesMaps[0].put( "complement", true);
				attributesMaps[0].put( "payload", payload);
			}
		});
		result.put( PRODUCTION_REGEX_CLASS_SIGNUM_INVERT, semanticRules);

		// CS -> \epsilon
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				attributesMaps[0].put( "complement", false);
			}
		});
		result.put( PRODUCTION_REGEX_CLASS_SIGNUM_RIGHT, semanticRules);

		
		

		// CE -> CE CEP
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				List<Symbol> valuesCE = (List<Symbol>) attributesMaps[1].get( "values");
				List<Symbol> valuesCEP = (List<Symbol>) attributesMaps[2].get( "values");
				valuesCE.addAll( valuesCEP);
				attributesMaps[0].put( "values", valuesCE);
			}
		});
		result.put( PRODUCTION_REGEX_CLASS_BYPASS_ELEMENTS, semanticRules);
  	// alike CE -> CE CR
		result.put( PRODUCTION_REGEX_CLASS_RANGE_ELEMENTS, semanticRules);
		
		// CE -> CEP
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				attributesMaps[0].put( "values", attributesMaps[1].get( "values"));
			}
		});
		result.put( PRODUCTION_REGEX_CLASS_RANGE_ELEMENTS_BYPASS, semanticRules);
		// alike  CE -> CR
		result.put( PRODUCTION_REGEX_CLASS_RANGE_ELEMENTS_FINAL, semanticRules);	
	  // CF -> CFP
		result.put( PRODUCTION_REGEX_CLASS_FIRST_RANGE_BYPASS, semanticRules);	
		
		
		
		// CEP -> CV
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				List<Symbol> values = new ArrayList<Symbol>();
				values.add( (Symbol) attributesMaps[1].get( "value"));
				attributesMaps[0].put( "values", values);
			}
		});
		result.put( PRODUCTION_REGEX_CLASS_SIGLE_ELEMENT, semanticRules);
		// alike CFP -> CFV  
		result.put( PRODUCTION_REGEX_CLASS_FIRST_SINGLE_ELEMENT, semanticRules);
		
		
		
		

		
		// CF -> CFV - CFV   whereas holds Ord(V) <= Ord(V1) 
		semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) throws OperatorTreeException {
				List<Symbol> values = new ArrayList<Symbol>();
				RegularExpressionElement valueV = (RegularExpressionElement) attributesMaps[1].get( "value");
				Object commonPayload = ((Symbol) attributesMaps[2].get( "value")).getPayload();
				RegularExpressionElement valueV1 = (RegularExpressionElement) attributesMaps[3].get( "value");
				char valueVChar = (Character) valueV.getValue();
				char valueV1Char = (Character) valueV1.getValue();
				
				for ( char c = valueVChar; c <= valueV1Char; c++) {
					if ( c == valueVChar 
							&& Test.isAssigned( valueV.getPayload()))
						values.add( new RegularExpressionElement( c, valueV.getPayload()));
					else if ( c == valueV1Char 
							&& Test.isAssigned( valueV1.getPayload()))
						values.add( new RegularExpressionElement( c, valueV1.getPayload()));
					else
					  values.add( new RegularExpressionElement( c, commonPayload));
				}
				
				if ( values.isEmpty())
				  throw new OperatorTreeException( "Invalid regular expression. Empty range in character class.");
					
				attributesMaps[0].put( "values", values);
			}
		});
		result.put( PRODUCTION_REGEX_CLASS_FIRST_RANGE_ELEMENT, semanticRules);
		// CR -> CV - CV
		result.put( PRODUCTION_REGEX_CLASS_RANGE, semanticRules);		
		
		

		
		// +++++++++++++++++++++++++++++
		// Level 4 (Characters)
		// +++++++++++++++++++++++++++++
		
	
		// Main Terminals
		
		// V -> a
		SemanticRules semanticRulesOfUnguardedTerminals = new SemanticRules();
		semanticRulesOfUnguardedTerminals.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode<Symbol> nodeTerminal = new TerminalNode( (Symbol) attributesMaps[1].get( "value"));
				attributesMaps[0].put( "node", nodeTerminal);
			}
		});

		// V -> \ a
		SemanticRules semanticRulesOfMetaTerminals = new SemanticRules();
		semanticRulesOfMetaTerminals.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				TreeNode<Symbol> nodeTerminal = new TerminalNode( (Symbol) attributesMaps[2].get( "value"));
				attributesMaps[0].put( "node", nodeTerminal);
			}
		});

    List<Character> unguardedChars = RegexCharSet.getUnguardedCharsOfContext( RegexSection.MAIN);
		for ( Terminal terminal : getRegexGrammar().getTerminals()) {
			if ( unguardedChars.contains( terminal.getSymbol().getValue())) 
				result.put( new ProductionRule(NONTERMINAL_V, terminal), semanticRulesOfUnguardedTerminals);
			else
				result.put( new ProductionRule(NONTERMINAL_V, TERMINAL_MASK, terminal), semanticRulesOfMetaTerminals);
		} 
		
		
		// CLASS Values
		
		SemanticRules semanticRulesOfUnguardedClassValues = new SemanticRules();
		semanticRulesOfUnguardedClassValues.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				attributesMaps[0].put( "value", attributesMaps[1].get( "value"));
			}
		});

		// V -> \ a
		SemanticRules semanticRulesOfMetaClassValues = new SemanticRules();
		semanticRulesOfMetaClassValues.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				attributesMaps[0].put( "value", attributesMaps[2].get( "value"));
			}
		});
		
    List<Character> charset = RegexCharSet.getCompleteDomain();
		for ( Terminal terminal : getRegexGrammar().getTerminals()) {
			Character currentChar = (Character) terminal.getSymbol().getValue();
			if ( charset.contains( currentChar)) {
				if ( currentChar == RegexCharSet.REGEX_RANGE) {
					result.put( new ProductionRule(NONTERMINAL_CV, terminal), semanticRulesOfUnguardedClassValues);
					result.put( new ProductionRule(NONTERMINAL_FIRST_CV, terminal), semanticRulesOfUnguardedClassValues);					
				} else if ( currentChar == RegexCharSet.REGEX_CLASS_SIGNUM) {
					result.put( new ProductionRule(NONTERMINAL_CV, terminal), semanticRulesOfUnguardedClassValues);
					result.put( new ProductionRule(NONTERMINAL_FIRST_CV, TERMINAL_MASK, terminal), semanticRulesOfMetaClassValues);
				} else if ( currentChar == RegexCharSet.REGEX_CLASS_END) {
					result.put( new ProductionRule(NONTERMINAL_CV, TERMINAL_MASK, terminal), semanticRulesOfMetaClassValues);
					result.put( new ProductionRule(NONTERMINAL_FIRST_CV, TERMINAL_MASK, terminal), semanticRulesOfMetaClassValues);					
				} else {
					result.put( new ProductionRule(NONTERMINAL_CV, terminal), semanticRulesOfUnguardedClassValues);
					result.put( new ProductionRule(NONTERMINAL_FIRST_CV, terminal), semanticRulesOfUnguardedClassValues);
				}
			}
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
	      System.out.println( embracingNonterminalNode.toFullString());
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

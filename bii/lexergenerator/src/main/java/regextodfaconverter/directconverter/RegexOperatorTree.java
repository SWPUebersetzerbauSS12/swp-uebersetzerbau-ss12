package regextodfaconverter.directconverter;

import java.util.Iterator;

import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Grammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Grammars;
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.syntaxtree.AbstractSyntaxTree;
import regextodfaconverter.directconverter.syntaxtree.AttributesMap;
import regextodfaconverter.directconverter.syntaxtree.SemanticRule;
import regextodfaconverter.directconverter.syntaxtree.SemanticRules;
import regextodfaconverter.directconverter.syntaxtree.SyntaxDirectedDefinition;
import regextodfaconverter.directconverter.syntaxtree.Tree;
import regextodfaconverter.directconverter.syntaxtree.TreeIterator;
import regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNode;


public class RegexOperatorTree implements Tree {

	private AbstractSyntaxTree ast;
	
	public RegexOperatorTree( String regex) throws Exception {
		super();
		ContextFreeGrammar regexGrammar = Grammars.getRegexGrammar();
		SyntaxDirectedDefinition regexSdd = getRegexSdd();
		extendGrammarAndSddWithTerminator( regexGrammar, regexSdd);
	  // extends regex string
		regex += RegexSpecialChars.TERMINATOR;
		ast = new AbstractSyntaxTree( regexGrammar, regexSdd, regex);
	}
	
	
	
	public static SyntaxDirectedDefinition getRegexSdd() {
		SyntaxDirectedDefinition result = new SyntaxDirectedDefinition();
		
		// we define a simple regex grammar for testing
		Nonterminal R = new Nonterminal( "R");
		Nonterminal S = new Nonterminal( "S");
		Nonterminal T = new Nonterminal( "T");
		Nonterminal U = new Nonterminal( "U");
		Nonterminal V = new Nonterminal( "V");
		Terminal<Character> a = new Terminal<Character>( 'a');
	
		final Terminal<Character> leftBracket = new Terminal<Character>( '(');
		final Terminal<Character> rightBracket = new Terminal<Character>( ')');
		final Terminal<Character> opKleeneClosure = new Terminal<Character>( '*');
		final Terminal<Character> opAlternative = new Terminal<Character>( '+');
		final Terminal<Character> opConcatenation = new Terminal<Character>( '.');
		
		// R -> R1 + S
		SemanticRules semanticRules = new SemanticRules();
		semanticRules.add( new SemanticRule() {	
			public void apply( AttributesMap... attributesMaps) {
				InnerNode<Character> nodeR = new InnerNode<Character>( opAlternative.getSymbol());
				TreeNode nodeR1 = (TreeNode) attributesMaps[1].get( "node");
				TreeNode nodeS = (TreeNode) attributesMaps[3].get( "node");
				nodeR.addChilds( nodeR1, nodeS);
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
				InnerNode<Character> nodeS = new InnerNode<Character>( opConcatenation.getSymbol());
				TreeNode nodeS1 = (TreeNode) attributesMaps[1].get( "node");
				TreeNode nodeT = (TreeNode) attributesMaps[3].get( "node");
				nodeS.addChilds( nodeS1, nodeT);
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
				InnerNode<Character> nodeT = new InnerNode<Character>( opKleeneClosure.getSymbol());
				TreeNode nodeU = (TreeNode) attributesMaps[1].get( "node");
				nodeT.addChilds( nodeU);
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
				InnerNode<Character> nodeTerminal = new InnerNode<Character>( (Character) attributesMaps[1].get( "value"));
				attributesMaps[0].put( "node", nodeTerminal);
			}
		});
		result.put( new ProductionRule(V, a), semanticRules);
		
		return result;
	}
	
	
	
	/**
	 * Erweitert die Grammatik für reguläre Ausdrücke um das Terminatorsymbol.
	 * 
	 * @return
	 */
	private static void extendGrammarAndSddWithTerminator( Grammar grammar, SyntaxDirectedDefinition sdd) {
		// extends grammar
		Grammar extendedGrammar = grammar;
		Nonterminal embracingNonterminal = new Nonterminal( "A");
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
				InnerNode<Character> embracingNonterminalNode = new InnerNode<Character>( opConcatenation.getSymbol());
				TreeNode priorStartSymbolNode = (TreeNode) attributesMaps[1].get( "node");
				TreeNode terminatorNode = new InnerNode<Character>( (Character) attributesMaps[2].get( "value"));
				embracingNonterminalNode.addChilds( priorStartSymbolNode, terminatorNode);
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
	
}

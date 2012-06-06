package regextodfaconverter.directconverter.syntaxtree;

import java.util.Iterator;

import regextodfaconverter.directconverter.lr0parser.ReduceEventHandler;
import regextodfaconverter.directconverter.lr0parser.ShiftEventHandler;
import regextodfaconverter.directconverter.lr0parser.grammar.ContextFreeGrammar;
import regextodfaconverter.directconverter.lr0parser.grammar.Grammar;
import regextodfaconverter.directconverter.lr0parser.grammar.ProductionRule;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import regextodfaconverter.directconverter.syntaxtree.node.NewNodeEventHandler;
import regextodfaconverter.directconverter.syntaxtree.node.TreeNode;
import utils.Test;


public class AbstractSyntaxTree extends ConcreteSyntaxTree {
	
	private SyntaxDirectedTranslation sdtTable;

	public AbstractSyntaxTree( ContextFreeGrammar grammar, SyntaxDirectedTranslation sdtTable, String expression)
			throws SyntaxTreeException {
    super( grammar, expression);
    this.sdtTable = sdtTable;
	}
	
	
	public AbstractSyntaxTree( ContextFreeGrammar grammar, SyntaxDirectedTranslation sdtTable, String expression, NewNodeEventHandler newNodeEventHandler)
			throws SyntaxTreeException {
    super( grammar, expression, newNodeEventHandler);
    this.sdtTable = sdtTable;
	}
	
	
	protected ReduceEventHandler getReduceEventHandler() {
		return new ReduceEventHandler() {

			public Object handle( Object sender, ProductionRule reduceRule) throws Exception {
			
				// create the map contains attributes of this node
				AttributesMap thisAttributesMap = new AttributesMap();

				// create new inner node
				InnerNode<AttributesMap> newInnerNode = new InnerNode<AttributesMap>( thisAttributesMap);
				// newInnerNode.setPrintHandler( getNodePrintHandler());

				// get the semantic rules defined for given rule to reduce
			  SemanticRules semanticRules = sdtTable.get( reduceRule);
				
			  
			  // add childs to the new inner node
				int countOfReducedElements = reduceRule.getRightRuleSide().size();
				// By the way, assemble the attributes of rule elements of right rule side representing by the nodes
			  AttributesMap[] nodeAttributesMaps = new AttributesMap[countOfReducedElements+1];
				nodeAttributesMaps[0] = thisAttributesMap;
			  for ( int i = 0; i < countOfReducedElements; i++) {
			  	// get child from stack
					TreeNode childNode = getNodeStack().pop();
					// insert child into parent node
					newInnerNode.insertChild( childNode, 0);
					// add attributes from end to front
					nodeAttributesMaps[countOfReducedElements-i] = (AttributesMap) childNode.getValue();
				}
			  
			  // apply all defined semantic rules
				if (Test.isAssigned( semanticRules)) {
				  for ( SemanticRule semanticRule : semanticRules) {
					  semanticRule.apply( nodeAttributesMaps);
				  }
				}
				
				
				if ( Test.isAssigned( onNewNodeEvent))
					onNewNodeEvent.doOnEvent( this, newInnerNode);

				// push the inner node onto stack
				getNodeStack().push( newInnerNode);

				return null;
			}
		};
	}
	
	
	@Override
	protected ShiftEventHandler getShiftEventHandler() {
		return new ShiftEventHandler() {

			public Object handle( Object sender, Terminal shiftedTerminal) throws Exception {
			  
				// create the map contains attributes of this node
				AttributesMap thisAttributesMap = new AttributesMap();
				// create attribute value to pass the terminal
				thisAttributesMap.put( "value", shiftedTerminal.getSymbol());
        
				// create new leaf node
				Leaf<AttributesMap> newLeaf = new Leaf<AttributesMap>( thisAttributesMap);
				
				if ( Test.isAssigned( onNewNodeEvent))
					onNewNodeEvent.doOnEvent( this, newLeaf);

				getNodeStack().push( newLeaf);
				return null;
			}
		};
	}
	
	
		

}

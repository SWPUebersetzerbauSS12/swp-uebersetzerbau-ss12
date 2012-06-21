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


package de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.ItemAutomat;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.ReduceEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.ShiftEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ContextFreeGrammar;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.EmptyString;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.InnerNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.Leaf;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.NewNodeEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.NumberedTreeNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.ScalableInnerNode;
import de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node.TreeNode;
import de.fuberlin.bii.utils.Test;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class AbstractSyntaxTree<ExpressionElement extends Symbol> extends ConcreteSyntaxTree<ExpressionElement>  {
	
	private SyntaxDirectedDefinition sddTable;
	
	private AttributesMap rootAttributesMap;

	public AbstractSyntaxTree( ContextFreeGrammar grammar, SyntaxDirectedDefinition sddTable, ExpressionElement[] expression)
			throws Exception {
    this( grammar, sddTable, expression, null);
	}
	
	
	public AbstractSyntaxTree( ContextFreeGrammar grammar, final SyntaxDirectedDefinition sddTable, ExpressionElement[] expression, NewNodeEventHandler newNodeEventHandler)
			throws Exception {
    super( grammar, expression, newNodeEventHandler, false);
		this.sddTable = sddTable;
    this.buildTree();
	}
	
	
	protected ReduceEventHandler getReduceEventHandler() {
		return new ReduceEventHandler() {

			public Object handle( Object sender, ProductionRule reduceRule, int sequenceNumber) throws Exception {
			
				updateStackBySequenceNumber( sequenceNumber);
				
				// create the map contains attributes of this node
				AttributesMap thisAttributesMap = new AttributesMap();
				rootAttributesMap = thisAttributesMap;;

				// create new inner node
				InnerNode<AttributesMap> newInnerNode = new ScalableInnerNode<AttributesMap>( thisAttributesMap);
				// newInnerNode.setPrintHandler( getNodePrintHandler());

				// get the semantic rules defined for given rule to reduce
			  SemanticRules semanticRules = sddTable.get( reduceRule);
				
			  
			  // add childs to the new inner node
				int countOfReducedElements = reduceRule.getRightRuleSide().size();
				// By the way, assemble the attributes of rule elements of right rule side representing by the nodes
			  AttributesMap[] nodeAttributesMaps = new AttributesMap[countOfReducedElements+1];
				nodeAttributesMaps[0] = thisAttributesMap;
			 	for ( int i = countOfReducedElements; i > 0; i--) {
			  	if ( reduceRule.getRightRuleSide().get( i-1) instanceof EmptyString)
			  		continue;
					// get child from stack
					TreeNode childNode = getNodeStack().pop().getTreeNode();
					// insert child into parent node
					newInnerNode.insertChild( childNode, 0);
					// add attributes from end to front
					nodeAttributesMaps[i] = (AttributesMap) childNode.getValue();
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
				NumberedTreeNode newNumberedNode = new NumberedTreeNode( newInnerNode, sequenceNumber);
				getNodeStack().push( newNumberedNode);

				snapshotCurrentStackWithSequenceNumber( sequenceNumber);
				return null;
			}
		};
	}
	



	@Override
	protected ShiftEventHandler getShiftEventHandler() {
		return new ShiftEventHandler() {

			public Object handle( Object sender, Terminal shiftedTerminal, int sequenceNumber) throws Exception {
			  
				updateStackBySequenceNumber( sequenceNumber);
				
				// create the map contains attributes of this node
				AttributesMap thisAttributesMap = new AttributesMap();
				// create attribute value to pass the terminal
				thisAttributesMap.put( "value", shiftedTerminal.getSymbol());
        		
				// create new leaf node
				Leaf<AttributesMap> newLeaf = new Leaf<AttributesMap>( thisAttributesMap);
				
				if ( Test.isAssigned( onNewNodeEvent))
					onNewNodeEvent.doOnEvent( this, newLeaf);

				NumberedTreeNode newNumberedLeaf = new NumberedTreeNode( newLeaf, sequenceNumber);
				getNodeStack().push( newNumberedLeaf);
				
				snapshotCurrentStackWithSequenceNumber( sequenceNumber);
				return null;
			}
		};
	}
	
	
	
	public AttributesMap getRootAttributesMap() {
		return rootAttributesMap;
	}
		

}

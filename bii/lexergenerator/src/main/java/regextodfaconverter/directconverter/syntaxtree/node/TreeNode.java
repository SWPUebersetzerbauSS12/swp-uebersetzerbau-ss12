package regextodfaconverter.directconverter.syntaxtree.node;

import utils.Test;


public class TreeNode<Value> implements Cloneable {

	protected Value value;
	protected InnerNode parentNode = null;
	
	public TreeNode( Value value) {
		super();
	  this.value = value;
	}
	
	public TreeNode( TreeNode parentNode, Value value) {
		super();
	  this.value = value;
	}
	
	public Value getValue() {
		return value;
	}
	
	public InnerNode getParentNode() {
		return parentNode;
	}
	
	public void setParentNode( InnerNode newParentNode) {
		if ( Test.isAssigned(  this.parentNode))
			this.parentNode.removeChild( this);
		if ( Test.isAssigned( newParentNode))
			newParentNode.addChild( this);
	}
	
	public boolean isRootNode() {
		return Test.isUnassigned( parentNode);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		TreeNode<Value> clonedTreeNode = (TreeNode<Value>) super.clone();
		clonedTreeNode.parentNode = this.parentNode;
		clonedTreeNode.value = this.value;
		return clonedTreeNode;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
}

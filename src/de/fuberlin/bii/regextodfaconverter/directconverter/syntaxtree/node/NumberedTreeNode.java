package de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node;


/**
 * Struktur kapselt einen TreeNode und weist diesem eine Nummer zu. 
 * @author workstation
 *
 * @param <Value>
 */
public class NumberedTreeNode<Value> {

	private TreeNode<Value> treeNode;
	private int number;
	
	
	public NumberedTreeNode( TreeNode<Value> treeNode, int number) {
		super();
		this.treeNode = treeNode;
		this.number = number;
	}
	
	
	public int getNumber() {
		return number;
	}
	
	
	public TreeNode<Value> getTreeNode() {
		return treeNode;
	}
	
}

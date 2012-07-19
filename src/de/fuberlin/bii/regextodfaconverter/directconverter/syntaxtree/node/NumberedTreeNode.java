package de.fuberlin.bii.regextodfaconverter.directconverter.syntaxtree.node;


/**
 * Struktur kapselt einen TreeNode und weist diesem eine Nummer zu. 
 * @author Johannes Dahlke
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
	
	/**
	 * Gibt die Nummer zurück.
	 * @return
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Liefert den Knoten.
	 * @return
	 */
	public TreeNode<Value> getTreeNode() {
		return treeNode;
	}
	
	
	@Override
	public String toString() {
		return "[" + number + ": " + treeNode + "]";
	}
}

package de.fuberlin.projectcii.ParserGenerator.src;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.commons.parser.ISymbol;
import de.fuberlin.commons.parser.ISyntaxTree;


/**
 * 
 * Implementation of the SyntaxTree, composed of SyntaxTree-Nodes
 * 
 */
public class SyntaxTree implements ISyntaxTree {
	
	//All children of the current Node
	private List<ISyntaxTree> children = new LinkedList<ISyntaxTree>();
	//The parent of the current Node (NULL if root)
	private SyntaxTree parent;
	//Token for the current Node
	private IToken token = null;
	//Symbol of the current Node
	private ISymbol symbol;
	// Attribute 
	private Map<String, Object> attributes = new HashMap<String,Object>();
	
	/**
	 * Default Constructor
	 */
	public SyntaxTree() {
	}
	
	/**
	 * Sets the Token and the Symbol
	 * 
	 * @param token
	 * @param symbol
	 */
	public SyntaxTree(IToken token, ISymbol symbol) {
		this.symbol = symbol;
		this.token = token;
	}

	/**
	 * Sets the parent
	 * @param tree The parent Node of the current Node
	 */
	@Override
	public void setParent(ISyntaxTree tree) {
		this.parent = (SyntaxTree)tree;
	}


	/**
	 * Getter for parent Node
	 * @return parent Node
	 */
	@Override
	public ISyntaxTree getParent() {
		return this.parent;
	}

	/**
	 * Adds a new child to the current node
	 * 
	 * @param tree Child of the current node
	 */
	@Override
	public void addChild(ISyntaxTree tree) {
		this.children.add((SyntaxTree)tree);
	}

	/**
	 * @return Returns the number of childs this Node has
	 */
	@Override
	public int getChildrenCount() {
		return this.children.size();
	}

	/**
	 * @return Returns child at a given position
	 */
	@Override
	public ISyntaxTree getChild(int i) {
		return children.get(i);
	}

	/**
	 * @return Returns list of children with this name
	 */
	@Override
	public List<ISyntaxTree> getChildrenByName(String name) {

			List<ISyntaxTree> childrenByName = new LinkedList<ISyntaxTree>();
			
			for (ISyntaxTree child: children){
				SyntaxTree currNode = (SyntaxTree)child;
				if (currNode.getSymbol().getName().equals(name)){
					childrenByName.add(child);
				}
			}
		
		return childrenByName;
	}

	@Override
	public boolean setAttribute(String name, Object value) {
		attributes.put(name, value);
		return true;
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public boolean addAttribute(String name) {
		attributes.put(name, null);
		return true;
	}

	@Override
	public List<ISyntaxTree> getChildren() {

		return this.children;
	}
	
	@Override
	public ISyntaxTree removeChild(int i) {
		return children.remove(i);
	}

	/**
	 * shows the Tree in readable (horizontal) form
	 */
	@Override
	public void printTree() {
	    if (Settings.getXMLTREE()){
	        Printer.parsetreeToXML(this);
	    }
//		System.out.println("-------");
//		printChild(this,0,true);
	}

	/**
	 * Gets token of this node, value is null if node is not a leaf.
	 */
	@Override
	public IToken getToken() {
		return this.token;
	}
	
	
	//---------------------
	// End of Interface Methodes
	
	/**
	 * Returns Symbol of this node.
	 */
	public ISymbol getSymbol() {
		return this.symbol;
	}
	
	/**
	 * Sets Symbol of this node.
	 * @param symbol
	 */
	public void setSymbol(ISymbol symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * Compresses the SyntaxTree by eliminating all single-child-nodes
	 */
	public void CompressSyntaxTree(){
		
		boolean newChilds = true;
		
		while (newChilds){
			newChilds = false;
			Vector<ISyntaxTree> singleChilds = new Vector<ISyntaxTree>();
			
			for (ISyntaxTree child:children){
				if (child.getChildren().size() == 1){
					singleChilds.add(child);
					newChilds = true;
				}
			}
			for (ISyntaxTree child:singleChilds){
				child.getChild(0).setParent(this);
				children.set(children.indexOf(child), child.getChild(0));
			}
		}
		for (ISyntaxTree child:children){
			((SyntaxTree) child).CompressSyntaxTree();
		}
	}
	
	/**
	 * Sets Token for this node, used at leafs to access tokens in semantic analysis.
	 * @param token
	 */
    public void setToken(IToken token){
        this.token = token;
    }
	
}

package de.fuberlin.projectci.lrparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.fuberlin.projectci.extern.IAttribute;
import de.fuberlin.projectci.extern.ISyntaxTree;
import de.fuberlin.projectci.grammar.Symbol;

/**
 * Repräsentiert einen Syntaxbaum-Knoten 
 *
 */
public class SyntaxTreeNode implements ISyntaxTree{

	// Das [[Non]Terminal]Symbol
	private Symbol symbol;
	// Attribute 
	private Map<String, String> attributeName2Value = new HashMap<String, String>();
	// cildren als LinkedList, um insertTree effizient zu implementieren zu können
	private List<ISyntaxTree> children=new LinkedList<ISyntaxTree>();
	
	public SyntaxTreeNode(Symbol symbol) {
		this.symbol = symbol;
	}

	// **************************************************************************** 
	// * Implementierung von ISyntaxTree
	// ****************************************************************************
	
	@Override
	public void addTree(ISyntaxTree tree) {
		children.add(tree);		
	}
	
	@Override
	public String getName() {
		return symbol.getName();
	}

	@Override
	public int getChildrenCount() {
		return children.size();
	}

	@Override
	public ISyntaxTree getChild(int i) {
		return children.get(i);
	}

	@Override
	public List<ISyntaxTree> getChildrenByName(String name) {
		List<ISyntaxTree> result=new ArrayList<ISyntaxTree>();
		for (ISyntaxTree aChildTree : children) {
			if (aChildTree.getName().equals(name)){
				result.add(aChildTree);
			}
		}
		return result;
	}
	
	// TODO Attribut-Handling implementieren --> Fehler in ISyntaxTree (IAttribute fehlt/ addAttribute macht keinen Sinn)
	
	@Override
	public IAttribute getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setAttribute(String name, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAttribute(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public static class Attribute implements IAttribute{
		private String name;
		private String value;
	}
	
	// **************************************************************************** 
	// * Erweiterungen
	// ****************************************************************************

	/**
	 * Fügt einen SyntaxTree als erstes Kind hinzu.
	 * @param tree
	 */
	void insertTree(ISyntaxTree tree) {
		children.add(0, tree);		
	}
	
	/**
	 * Gibt eine (einfache um 90 Grad gedrehte) menschenlesbare Konsolenausgabe des Teilbaums zurück.
	 * TODO Zum Debuggen wäre eine Ausgabe als HTML, XML oder Bilddatei wünschenswert
	 */
	@Override
	public String toString() {
		StringBuffer strBuf= new StringBuffer();
		toString(strBuf, 0);
		return strBuf.toString();
	}
	
	/**
	 * Rekursive Implementierung von toString
	 * @param level
	 * @return
	 */
	private void toString(StringBuffer strBuf, int level) {		
		for (int i = 1; i < level; i++) {
			strBuf.append("    ");
		}
		if (level>0){
			strBuf.append("--> ");			
		}
		strBuf.append(symbol);
		for (ISyntaxTree aChildNode : children) {
			strBuf.append("\n");
			((SyntaxTreeNode)aChildNode).toString(strBuf, level+1);			
		}
	}
	
	/**
	 * 2 Knoten sind gleich, wenn sie das gleiche Symbol und die gleichen Kinder haben.
	 * TODO Attribute berücksichtigen --> ... und wenn sie die gleichen Attribute mit den gleichen Werten haben.
	 * TODO hashCode implementieren - oder Vergleiche als Comparators implementieren. 
	 */
	@Override
	public boolean equals(Object other) {
		if (other==null || !(other instanceof SyntaxTreeNode)){
			return false;
		}
		SyntaxTreeNode otherTree=(SyntaxTreeNode) other;
		if (!this.symbol.equals(otherTree.symbol)){
			return false;
		}
		if (this.children.size()!=otherTree.children.size()){
			return false;
		}
		for (int i = 0; i < children.size(); i++) {
			ISyntaxTree thisChild=children.get(i);
			ISyntaxTree otherChild=otherTree.children.get(i);
			if (!thisChild.equals(otherChild)){
				return false;
			}			
		}
		return true;
	}
	
	/**
	 * Reduziert den Syntaxbaum auf einen Abstrakten Syntaxbaum durch rekursives Hochziehen aller Einzelkinder.
	 */
	void reduceToAbstractSyntaxTree(){
		for (int i = 0; i < getChildrenCount(); i++) {
			SyntaxTreeNode aChildTree=(SyntaxTreeNode) getChild(i);
			aChildTree.reduceToAbstractSyntaxTree(); // Bottom-Up
			if (aChildTree.getChildrenCount()==1){
				// Ersetze childTree durch dessen erstes (und einziges) Kind.
				aChildTree=(SyntaxTreeNode) aChildTree.getChild(0);
				children.set(i, aChildTree);
			}			
		}				
	}
}

package de.fuberlin.projectci.lrparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.projectci.extern.IAttribute;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;

/**
 * Repräsentiert einen 	Knoten eines Syntaxbaums 
 *
 */
public class SyntaxTreeNode implements ISyntaxTree{
	private static Logger logger = LogFactory.getLogger(SyntaxTreeNode.class);
	// Das [[Non]Terminal]Symbol
	private Symbol symbol;
	
	private IToken token;
	// Attribute 
	private Map<String, Object> attributeName2Value = new HashMap<String, Object>();
	// cildren als LinkedList, um insertTree effizient zu implementieren zu können
	private List<ISyntaxTree> children=new LinkedList<ISyntaxTree>();
	
	
	
	public SyntaxTreeNode(Symbol symbol) {
		this.symbol = symbol;
	}

	public SyntaxTreeNode(IToken token, TerminalSymbol symbol) {
		this.symbol = symbol;
		this.token=token;
	}
	// **************************************************************************** 
	// * Implementierung von ISyntaxTree
	// ****************************************************************************
	
	@Override
	public void addChild(ISyntaxTree tree) {
		children.add(tree);		
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
			if (aChildTree.getToken() != null && name.equals(aChildTree.getToken().getText())){
				result.add(aChildTree);
			}
		}
		return result;
	}
	
	// TODO Attribut-Handling implementieren --> Fehler in ISyntaxTree (IAttribute fehlt/ addAttribute macht keinen Sinn)
	
	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return attributeName2Value.get(name);
	}

	@Override
	public boolean addAttribute(String name) {
		// TODO Auto-generated method stub
		attributeName2Value.put(name, null);
		return true;
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
	
	void removeChildNode(ISyntaxTree childNode){
		children.remove(childNode);
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
		if (token!=null){
			strBuf.append(token);
		}
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
		// Erstmal alle ε-Knoten entfernen
		for (ISyntaxTree anEmptyChildNode : getChildrenByName(Grammar.EMPTY_STRING)) {
			removeChildNode(anEmptyChildNode);
		}
		for (int i = 0; i < getChildrenCount(); i++) {
			SyntaxTreeNode aChildTree=(SyntaxTreeNode) getChild(i);
			aChildTree.reduceToAbstractSyntaxTree(); // Bottom-Up
			if (aChildTree.getChildrenCount()==1){
				// Ersetze childTree durch dessen erstes (und einziges) Kind.
				aChildTree=(SyntaxTreeNode) aChildTree.getChild(0);
				children.set(i, aChildTree);
			}			
//			TODO Der Parsebaum enthält noch Nichtterminal-Blätter, die entfernt werden können.
//			Der erste Ansatz funktioniert aber nicht...
//			if (aChildTree.getChildrenCount()==0 && aChildTree.symbol instanceof NonTerminalSymbol){
//				removeChildNode(aChildTree);
//				continue;
//			}
		}				
	}

	@Override
	public void setParent(ISyntaxTree tree) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ISyntaxTree getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISyntaxTree removeChild(int i) {
		return children.remove(i);
	}

	@Override
	public boolean setAttribute(String name, Object value) {
		attributeName2Value.put(name, value);
		return true;
	}

	@Override
	public IToken getToken() {
		return token;
	}

	@Override
	public List<ISyntaxTree> getChildren() {
		return children;
	}

	@Override
	public void printTree() {
		System.out.println(toString());
		
	}
}

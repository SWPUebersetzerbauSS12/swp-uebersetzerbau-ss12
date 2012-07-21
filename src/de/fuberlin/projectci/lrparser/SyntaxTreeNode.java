package de.fuberlin.projectci.lrparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.commons.parser.IParser;
import de.fuberlin.commons.parser.ISymbol;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.commons.util.LogFactory;
import de.fuberlin.projectci.grammar.Grammar;
import de.fuberlin.projectci.grammar.Symbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;

/**
 * Repräsentiert den Knoten eines Syntaxbaums und realisiert {@link ISyntaxTree}
 *
 */
public class SyntaxTreeNode implements ISyntaxTree{
	private static Logger logger = LogFactory.getLogger(SyntaxTreeNode.class);
	// Das [[Non]Terminal]Symbol
	private Symbol symbol;
	
	private IToken token;
	// Attribute 
	private Map<String, Object> attributeName2Value = new HashMap<String, Object>();
	
	private ISyntaxTree parent=null;
	// cildren als LinkedList, um insertTree effizient zu implementieren zu können
	private List<ISyntaxTree> children=new LinkedList<ISyntaxTree>();
	
	
	/**
	 * Erzeugt einen inneren Knoten für das übergebene (Nichtterminal-)Symbol.
	 * @param symbol (Nichtterminal-)Symbol
	 */
	public SyntaxTreeNode(Symbol symbol) {
		this.symbol = symbol;
	}

	/**
	 * Erzeugt ein Blatt im SyntaxTree für ein Terminalsymbol mit zugehörigen Eingabetoken.
	 * @param token Eingabetoken
	 * @param symbol Terminalsymbol
	 */ 
	public SyntaxTreeNode(IToken token, TerminalSymbol symbol) {
		this.symbol = symbol;
		this.token=token;
	}
	// **************************************************************************** 
	// * Implementierung von ISyntaxTree
	// ****************************************************************************
	
	@Override
	public void addChild(ISyntaxTree tree) {
		if (tree.getParent()!=null && ! this.equals(tree.getParent())){
			throw new IllegalStateException("You don't have to add a child with another parent node");
		}
		if (children.contains(tree)){
			logger.warning("Refused to re-add an existing child node.");
			return;
		}
		children.add(tree);		
		tree.setParent(this);
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
			if (aChildTree.getToken() != null){
				if (name.equals(aChildTree.getToken().getText())){
					result.add(aChildTree);
				}
			}
		}
		return result;
	}
	
	
	@Override
	public boolean setAttribute(String name, Object value) {
		attributeName2Value.put(name, value);
		return true;
	}

	
	@Override
	public Object getAttribute(String name) {
		return attributeName2Value.get(name);
	}

	@Override
	public boolean addAttribute(String name) {
		attributeName2Value.put(name, null);
		return true;
	}
	
	
	@Override
	public void setParent(ISyntaxTree tree) {
		if (this.getParent()!=null && !this.getParent().equals(tree)){
			throw new IllegalStateException("You don't have to set another parent node!");
		}
		tree.addChild(this);
		
	}

	@Override
	public ISyntaxTree getParent() {
		return parent;
	}

	@Override
	public ISyntaxTree removeChild(int i) {
		return children.remove(i);
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

	@Override
	public ISymbol getSymbol() {
		return this.symbol;
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
	 * Entfernt einen Kindknoten.
	 * @param childNode
	 */
	void removeChildNode(ISyntaxTree childNode){
		children.remove(childNode);
	}

	@Override
	public String toString() {
		return toXML();
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
	public void reduceToAbstractSyntaxTree(){
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

	/**
	 * Entfernt alle Epsilon-Knoten aus dem Parsebaum.
	 */
	public void removeAllEpsilonNodes(){
		// Erstmal alle ε-Knoten entfernen
		for (ISyntaxTree anEmptyChildNode : getChildrenByName(Grammar.EMPTY_STRING)) {
			removeChildNode(anEmptyChildNode);
		}
		for (int i = 0; i < getChildrenCount(); i++) {
			SyntaxTreeNode aChildTree=(SyntaxTreeNode) getChild(i);
			aChildTree.removeAllEpsilonNodes();
		}				
	}
	
	
	/**
	 * Erzeugt eine XML-Repräsentation des Parsebaums.
	 * @return
	 */
	public String toXML(){
		StringBuffer strBuf= new StringBuffer();
		strBuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		toXML(strBuf, 0);
		return strBuf.toString();
	}
	
	private void toXML(StringBuffer strBuf,  int level){
		for (int i = 0; i < level; i++) {
			strBuf.append("  ");
		}
		strBuf.append("<node symbol=\"");
		strBuf.append(escape(symbol.getName()));
		strBuf.append("\"");
		if (token!=null){
			strBuf.append(" type=\"");
			strBuf.append(token.getType());
			strBuf.append("\"");
			if (token.getAttribute()!=null){
				strBuf.append(" value=\"");
				strBuf.append(escape(token.getAttribute().toString()));
				strBuf.append("\"");
			}
		}
		if (children.size()>0){
			strBuf.append(">\n");
			for (ISyntaxTree aChildNode : children) {
				((SyntaxTreeNode)aChildNode).toXML(strBuf, level+1);
			}
			for (int i = 0; i < level; i++) {
				strBuf.append("  ");
			}
			strBuf.append("</node>\n");
		}
		else{
			strBuf.append("/>\n");
		}				
	}
	
	/**
	 * Ersetzt Zeichen, die nicht in einem XML-Attribut erlaubt sind durch die zugehörige HTML-Entity.
	 * @param s
	 * @return
	 */
	private String escape( String s){
		if (s.contains("<")){
			s=s.replaceAll("<", "&lt;");
		}
		if (s.contains(">")){
			s=s.replaceAll("<", "&gt;");
		}
		if (s.contains("\"")){
			s=s.replaceAll("\"", "&quot;");
		}
		return s;
	}
}

package de.fuberlin.projecta.parser;

import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Tree implements ISyntaxTree {

	private final Symbol symbol;

	private final ArrayList<ISyntaxTree> children = new ArrayList<ISyntaxTree>();
	private final HashMap<String, Object> attributes = new HashMap<String, Object>();
	private IToken token;
	private ISyntaxTree parent = null;

	/**
	 * Creates a new empty node for non-terminals.
	 * 
	 * @param symbol
	 */
	public Tree(Symbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public void addChild(ISyntaxTree child) {
		if (child.getParent() == this)
			return;

		if (children.contains(child))
			return;

		children.add(child);
		child.setParent(this);
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
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public List<ISyntaxTree> getChildrenByName(String name) {
		List<ISyntaxTree> res = new LinkedList<ISyntaxTree>();
		for (ISyntaxTree child : getChildren()) {
			if (child.getSymbol().getName().equals(name))
				res.add(child);
		}
		return res;
	}

	@Override
	public boolean setAttribute(String key, Object value) {
		if (!attributes.containsKey(key)) {
			System.out.println("Warning, attribute not declared: " + key);
			return false;
		}
		attributes.put(key, value);
		return true;
	}

	public boolean addAttribute(String key) {
		if (attributes.containsKey(key)) {
			System.out.println("Warning: attribute already declared: " + key);
			return false;
		}
		attributes.put(key, null); // declare
		return true;
	}

	@Override
	public void setParent(ISyntaxTree parent) {
		if (getParent() == parent) {
			return;
		}

		if (parent != null && parent.getParent() == this) {
			System.out.println("Warning: Cyclic link detected.");
			return;
		}

		if (parent != null) {
			parent.addChild(this);
		}
		else {
			// remove this instance from old parent
			removeChild(this.parent, this);
		}
		this.parent = parent;
	}

	public void printTree() {
		printTree(0);
	}

	protected void printTree(int depth) {
		System.out.print(StringUtils.repeat(' ', depth));
		if (getSymbol() != null)
			System.out.println(getSymbol());
		else if (getToken() != null) {
			System.out.println(toString() + " (Token: " + getToken() + ")");
		} else {
			System.out.println(toString());
		}

		for (int i = 0; i < getChildrenCount(); ++i) {
			Tree tree = (Tree) getChild(i);
			if (tree != null)
				tree.printTree(depth + 2);
		}
	}

	@Override
	public List<ISyntaxTree> getChildren() {
		return children;
	}


	private static ISyntaxTree removeChild(ISyntaxTree tree, ISyntaxTree child) {
		final int index = tree.getChildren().indexOf(child);
		if (index < 0)
			return null;

		return tree.removeChild(index);
	}

	@Override
	public ISyntaxTree removeChild(int i) {
		ISyntaxTree child = children.remove(i);
		child.setParent(null);
		return child;
	}

	@Override
	public Symbol getSymbol() {
		return symbol;
	}

	public void setToken(IToken token) {
		this.token = token;
	}

	@Override
	public IToken getToken() {
		return token;
	}

	@Override
	public ISyntaxTree getParent() {
		return parent;
	}

}

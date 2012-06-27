package de.fuberlin.projecta.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.fuberlin.commons.lexer.IToken;
import de.fuberlin.commons.parser.ISyntaxTree;
import de.fuberlin.projecta.analysis.ast.nodes.AbstractSyntaxTree;
import de.fuberlin.projecta.analysis.ast.nodes.BasicType;
import de.fuberlin.projecta.analysis.ast.nodes.BinaryOp;
import de.fuberlin.projecta.analysis.ast.nodes.Id;
import de.fuberlin.projecta.analysis.ast.nodes.IntLiteral;
import de.fuberlin.projecta.analysis.ast.nodes.UnaryOp;
import de.fuberlin.projecta.utils.StringUtils;

public class Tree implements ISyntaxTree {

	private final Symbol symbol;

	private IToken token; // TODO: USE THIS!

	private final ArrayList<ISyntaxTree> children = new ArrayList<ISyntaxTree>();
	private HashMap<String, Object> attributes;

	ISyntaxTree parent = null;

	/**
	 * Creates a new empty node for non-terminals.
	 * 
	 * @param name
	 */
	public Tree(Symbol symbol) {
		this.symbol = symbol;
		attributes = new HashMap<String, Object>();
	}

	public void addChild(ISyntaxTree tree) {
		if (tree.getParent() == this)
			return;

		children.add(tree);
		tree.setParent(this);
	}

	public int getChildrenCount() {
		return children.size();
	}

	public ISyntaxTree getChild(int i) {
		return children.get(i);
	}

	public Object getAttribute(String name) {

		for (Entry<String, Object> attr : attributes.entrySet()) {
			if (attr.getKey().equals(name)) {
				return attr.getValue();
			}
		}
		return null;
	}

	public List<ISyntaxTree> getChildrenByName(String name) {
		return null; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	public boolean setAttribute(String name, Object value) {
		for (Entry<String, Object> attr : attributes.entrySet()) {
			if (attr.getKey().equals(name)) {
				attr.setValue(value);
				return true;
			}
		}
		return false;
	}

	public boolean addAttribute(String name) {
		if (getAttribute(name) == null) {
			attributes.put(name, null);
			return true;
		}
		return false;
	}

	public void setParent(ISyntaxTree tree) {
		if (tree.getParent() == this) {
			System.out.println("Warning: Cyclic link detected.");
			return;
		}

		if (getParent() == tree) {
			return;
		}

		this.parent = tree;
		tree.addChild(this);
	}

	public void printTree() {
		printTree(0);
	}

	protected void printTree(int depth) {
		//remove prefix from class name
		String className = this.getClass().getName().replaceAll("^.*\\.", "");		
		if (getSymbol() != null)
			System.out.println(StringUtils.repeat(' ', depth)
					+ getSymbol());
		else {
			if (this instanceof BinaryOp) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ className + ":"
						+ ((BinaryOp) this).getOp());
			} else if (this instanceof IntLiteral) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ className + ":"
						+ ((IntLiteral) this).getValue());
			} else if (this instanceof Id) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ className + ":"
						+ ((Id) this).getValue());
			} else if (this instanceof UnaryOp) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ className + ":"
						+ ((UnaryOp) this).getOp());
			} else if (this instanceof BasicType) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ className + ":"
						+ ((BasicType) this).getType());
			} else {
				System.out.println(StringUtils.repeat(' ', depth)
						+ className);
			}
			if (((AbstractSyntaxTree) this).getTable() != null) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ ((AbstractSyntaxTree) this).getTable());
			}
		}

		for (int i = 0; i < getChildrenCount(); ++i) {
			Tree tree = (Tree) getChild(i);
			if (tree != null)
				tree.printTree(depth + 2);
		}
	}

	public List<ISyntaxTree> getChildren() {
		return children;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Review. Is this enough?
		if (object instanceof ISyntaxTree) {
			ISyntaxTree tree = ((ISyntaxTree)object);

			String id1 = this.getSymbol().getName();
			String id2 = ((ISyntaxTree)object).getSymbol().getName();
			return (id1.equals(id2) && this.getChildren().equals(tree.getChildren()));
		}
		return false;
	}

	@Override
	public ISyntaxTree removeChild(int i) {
		ISyntaxTree child = children.remove(i);
		child.setParent(child);
		return child;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public IToken getToken() {
		return token;
	}

	public ISyntaxTree getParent() {
		return parent;
	}
}

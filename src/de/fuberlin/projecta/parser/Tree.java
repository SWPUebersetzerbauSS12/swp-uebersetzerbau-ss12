package de.fuberlin.projecta.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.fuberlin.projecta.analysis.ast.nodes.AbstractSyntaxTree;
import de.fuberlin.projecta.analysis.ast.nodes.BasicType;
import de.fuberlin.projecta.analysis.ast.nodes.BinaryOp;
import de.fuberlin.projecta.analysis.ast.nodes.Id;
import de.fuberlin.projecta.analysis.ast.nodes.IntLiteral;
import de.fuberlin.projecta.analysis.ast.nodes.UnaryOp;
import de.fuberlin.projecta.lexer.IToken;
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
		if (getSymbol() != null)
			System.out.println(StringUtils.repeat(' ', depth) + "Name: "
					+ getSymbol() + this.getClass().getName());
		else {
			if (this instanceof BinaryOp) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ this.getClass().getName() + ":"
						+ ((BinaryOp) this).getOp());
			} else if (this instanceof IntLiteral) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ this.getClass().getName() + ":"
						+ ((IntLiteral) this).getValue());
			} else if (this instanceof Id) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ this.getClass().getName() + ":"
						+ ((Id) this).getValue());
			} else if (this instanceof UnaryOp) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ this.getClass().getName() + ":"
						+ ((UnaryOp) this).getOp());
			} else if (this instanceof BasicType) {
				System.out.println(StringUtils.repeat(' ', depth)
						+ this.getClass().getName() + ":"
						+ ((BasicType) this).getType());
			} else {
				System.out.println(StringUtils.repeat(' ', depth)
						+ this.getClass().getName());
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
		if (object instanceof ISyntaxTree) {
			if (this.getSymbol().isNonTerminal()
					&& ((ISyntaxTree) object).getSymbol().isNonTerminal()) {
				if (this.getSymbol().asNonTerminal() == ((ISyntaxTree) object)
						.getSymbol().asNonTerminal()) {
					if (this.getChildren().equals(
							((ISyntaxTree) object).getChildren())) {
						return true;
					}
				}
			} else if (this.getSymbol().isTerminal()
					&& ((ISyntaxTree) object).getSymbol().isTerminal()) {
				if (this.getSymbol().asNonTerminal() == ((ISyntaxTree) object)
						.getSymbol().asNonTerminal()) {
					if (this.getChildren().equals(
							((ISyntaxTree) object).getChildren())) {
						return true;
					}
				}
			} else if (this.getSymbol().isReservedTerminal()
					&& ((ISyntaxTree) object).getSymbol().isReservedTerminal()) {
				if (this.getSymbol().asNonTerminal() == ((ISyntaxTree) object)
						.getSymbol().asNonTerminal()) {
					if (this.getChildren().equals(
							((ISyntaxTree) object).getChildren())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public ISyntaxTree removeChild(int i) {
		return children.remove(i);
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

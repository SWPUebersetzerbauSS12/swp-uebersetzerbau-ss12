package parser.nodes;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import parser.Attribute;
import parser.ISyntaxTree;
import semantic.analysis.SymbolTableStack;
import utils.StringUtils;

public abstract class Tree implements ISyntaxTree {

	public enum DefaultAttribute {
		TokenValue
	}

	private final ArrayList<ISyntaxTree> children = new ArrayList<ISyntaxTree>();
	private final String name;
	private final ArrayList<Attribute> attributes;

	@Getter
	ISyntaxTree parent = null;

	/**
	 * Creates a new empty node for non-terminals.
	 * 
	 * @param name
	 */
	public Tree(String name) {
		this.name = name;
		attributes = new ArrayList<Attribute>();
	}

	@Override
	public void addChild(ISyntaxTree tree) {
		if (tree.getParent() == this)
			return;

		children.add(tree);
		tree.setParent(this);
	}

	@Override
	public String getName() {
		return name;
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
	public Attribute getAttribute(String name) {
		for (Attribute attr : attributes) {
			if (attr.getName().equals(name)) {
				return attr;
			}
		}
		return null;
	}

	@Override
	public List<ISyntaxTree> getChildrenByName(String name) {
		return null; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	@Override
	public boolean setAttribute(String name, Object value) {
		for (Attribute attr : attributes) {
			if (attr.getName().equals(name)) {
				attr.setValue(value);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addAttribute(String name) {
		if (getAttribute(name) == null) {
			attributes.add(new Attribute(name));
			return true;
		}
		return false;
	}

	@Override
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
		System.out.println(StringUtils.repeat(' ', depth) + "Name: "
				+ getName());

		for (int i = 0; i < getChildrenCount(); ++i) {
			Tree tree = (Tree) getChild(i);
			if (tree != null)
				tree.printTree(depth + 2);
		}
	}

	@Override
	public abstract void run(SymbolTableStack tables);

}

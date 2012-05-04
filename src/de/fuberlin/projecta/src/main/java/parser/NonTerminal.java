package parser;

import java.util.ArrayList;
import java.util.List;

public abstract class NonTerminal implements ISyntaxTree {

	private ArrayList<ISyntaxTree> children = new ArrayList<ISyntaxTree>();
	private String name;
	private ArrayList<Attribute> attributes;

	/**
	 * Creates a new empty node for non-terminals.
	 *
	 * @param name
	 */
	public NonTerminal(String name) {
		this.name = name;
		attributes = new ArrayList<Attribute>();
	}

	public void addTree(ISyntaxTree tree) {
		children.add(tree);
	}

	public String getName() {
		return name;
	}

	public int getChildrenCount() {
		return children.size();
	}

	public ISyntaxTree getChild(int i) {
		return children.get(i);
	}

	public Attribute getAttribute(String name) {
		for (Attribute attr : attributes) {
			if (attr.getName().equals(name)) {
				return attr;
			}
		}
		return null;
	}

	public List<ISyntaxTree> getChildrenByName(String name) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public boolean setAttribute(String name, String value) {
		for (Attribute attr : attributes) {
			if (attr.getName().equals(name)) {
				attr.setValue(value);
				return true;
			}
		}
		return false;
	}

	public boolean addAttribute(String name) {
		if (getAttribute(name) == null) {
			attributes.add(new Attribute(name));
			return true;
		}
		return false;
	}
	
	public abstract void run();

}

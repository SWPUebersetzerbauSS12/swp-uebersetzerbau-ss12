package parser;

import java.util.ArrayList;

public class Terminal implements ISyntaxTree {

	private String name;
	private ArrayList<Attribute> attributes;

	public Terminal(String name) {
		this.name = name;
		attributes = new ArrayList<Attribute>();
	}

	/**
	 * Should a leaf really implement this method?
	 */
	public void addTree(ISyntaxTree tree) {
		throw new UnsupportedOperationException("Can't extend a leaf!");
	}

	public String getName() {
		return name;
	}

	public int getChildrenCount() {
		return 0;
	}

	public ISyntaxTree getChildren(int i) {
		return null;
	}

	public Attribute getAttribute(String name) {
		for (Attribute attr : attributes) {
			if (attr.getName().equals(name)) {
				return attr;
			}
		}
		return null;
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

}

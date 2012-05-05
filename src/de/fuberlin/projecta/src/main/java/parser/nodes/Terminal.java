package parser.nodes;

import java.util.ArrayList;
import java.util.List;

import parser.Attribute;
import parser.ISyntaxTree;
import semantic.analysis.SymbolTableStack;

public abstract class Terminal implements ISyntaxTree {

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

	public ISyntaxTree getChild(int i) {
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

	public List<ISyntaxTree> getChildrenByName(String name) {
		return null; // To change body of implemented methods use File |
						// Settings | File Templates.
	}

	/**
	 * Updates the attribute indicated by name with the entry value.
	 * 
	 * @return true if update was successful, false otherwise
	 */
	public boolean setAttribute(String name, String value) {
		for (Attribute attr : attributes) {
			if (attr.getName().equals(name)) {
				attr.setValue(value);
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a new attribute to the list of this nodes attributes.
	 */
	public boolean addAttribute(String name) {
		if (getAttribute(name) == null) {
			attributes.add(new Attribute(name));
			return true;
		}
		return false;
	}

	public abstract void run(SymbolTableStack tables);

}

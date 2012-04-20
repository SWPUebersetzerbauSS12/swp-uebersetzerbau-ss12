package parser;

import java.util.ArrayList;

public class NonTerminal implements SyntaxTree {
	
	private ArrayList<SyntaxTree> children = new ArrayList<SyntaxTree>();
	private String name;
	private ArrayList<Attribute> attributes;
	
	/**
	 * Creates a new empty node for non-terminals.
	 * @param name
	 */
	public NonTerminal(String name){
		this.name = name;
		attributes = new ArrayList<Attribute>();
	}	

	public void addTree(SyntaxTree tree) {
		children.add(tree);
	}

	public String getName() {
		return name;
	}

	public int getChildrenCount() {
		return children.size();
	}

	public SyntaxTree getChildren(int i) {
		return children.get(i);
	}

	public void removeTree(int i) {
		children.remove(i);
	}

	public Attribute getAttribute(String name) {
		for(Attribute attr : attributes){
			if(attr.getName().equals(name)){
				return attr;
			}
		}
		return null;
	}

	public boolean setAttribute(String name, String value) {
		for(Attribute attr : attributes){
			if(attr.getName().equals(name)){
				attr.setValue(value);
				return true;
			}
		}
		return false;
	}

	public boolean addAttribute(String name) {
		if(getAttribute(name) == null){
			attributes.add(new Attribute(name));
			return true;
		} 
		return false;
	}
	
}

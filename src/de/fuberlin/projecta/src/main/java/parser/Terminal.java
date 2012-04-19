package parser;

import java.util.ArrayList;

public class Terminal implements SyntaxTree {
	
	private String name;
	private ArrayList<Attribute> attributes;
	
	public Terminal(String name){
		this.name = name;
		attributes = new ArrayList<Attribute>();
	}

	/**
	 * Should a leaf really implement this method?
	 */
	@Override
	public void addTree(SyntaxTree tree) {
		throw new UnsupportedOperationException("Can't extend a leaf!");
	}

	/**
	 * Should a leaf really implement this method?
	 */
	@Override
	public void removeTree(int i) {
		throw new UnsupportedOperationException("Can't remove a subtree from a leaf!");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getChildrenCount() {
		return 0;
	}

	@Override
	public SyntaxTree getChildren(int i) {
		return null;
	}

	@Override
	public Attribute getAttribute(String name) {
		for(Attribute attr : attributes){
			if(attr.getName().equals(name)){
				return attr;
			}
		}
		return null;
	}

	@Override
	public boolean setAttribute(String name, String value) {
		for(Attribute attr : attributes){
			if(attr.getName().equals(name)){
				attr.setValue(value);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addAttribute(String name) {
		if(getAttribute(name) == null){
			attributes.add(new Attribute(name));
			return true;
		} 
		return false;
	}

}

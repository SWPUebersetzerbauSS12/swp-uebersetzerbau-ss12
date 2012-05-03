package de.fuberlin.projectci.lrparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fuberlin.projectci.extern.IAttribute;
import de.fuberlin.projectci.extern.ISyntaxTree;
import de.fuberlin.projectci.grammar.Symbol;

public class SyntaxTree implements ISyntaxTree{
	private List<ISyntaxTree> children=new ArrayList<ISyntaxTree>();
	private Symbol symbol;
	private Map<String, String> attributeMap = new HashMap<String, String>();
	

	public SyntaxTree(Symbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public void addTree(ISyntaxTree tree) {
		children.add(tree);
		
	}

	@Override
	public String getName() {
		return symbol.getName().toString();
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
	public IAttribute getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ISyntaxTree> getChildrenByName(String name) {
		List<ISyntaxTree> result=new ArrayList<ISyntaxTree>();
		for (ISyntaxTree aChildTree : children) {
			if (aChildTree.getName().equals(name)){
				result.add(aChildTree);
			}
		}
		return result;
	}

	@Override
	public boolean setAttribute(String name, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAttribute(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public static class Attribute implements IAttribute{
		private String name;
		private String value;
	}
}

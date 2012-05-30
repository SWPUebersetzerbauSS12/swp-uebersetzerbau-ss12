import java.util.List;
import java.util.Vector;

import Extern.ISyntaxTree;


public class SyntaxTree implements ISyntaxTree {
	
	private Vector<SyntaxTree> children;
	private SyntaxTree parent;
	private String symbol;
	
	public SyntaxTree(){
		children = new Vector<SyntaxTree>();
	}

	@Override
	public void setParent(ISyntaxTree tree) {
		this.parent = (SyntaxTree)tree;
	}

	@Override
	public ISyntaxTree getParent() {
		return this.parent;
	}

	@Override
	public void addChild(ISyntaxTree tree) {
		this.children.add((SyntaxTree)tree);
	}

	@Override
	public int getChildrenCount() {
		return this.children.size();
	}

	@Override
	public ISyntaxTree getChild(int i) {
		return children.elementAt(i);
	}

	@Override
	public List<ISyntaxTree> getChildrenByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setAttribute(String name, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addAttribute(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ISyntaxTree> getChildren() {

		return (List)this.children;
	}

	@Override
	public void printTree() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSymbol() {
		return this.symbol;
	}

}

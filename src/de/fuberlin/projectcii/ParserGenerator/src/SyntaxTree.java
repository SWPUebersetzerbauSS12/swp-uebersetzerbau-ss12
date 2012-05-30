import java.util.LinkedList;
import java.util.List;

import Extern.ISyntaxTree;


public class SyntaxTree implements ISyntaxTree {
	
	private List<ISyntaxTree> children = new LinkedList<ISyntaxTree>();
	private SyntaxTree parent;
	private String symbol;

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
		return children.get(i);
	}

	@Override
	public List<ISyntaxTree> getChildrenByName(String name) {

			List<ISyntaxTree> childrenByName = new LinkedList<ISyntaxTree>();
			
			for (ISyntaxTree child: children){
				if (child.getSymbol().equals(name)){
					childrenByName.add(child);
				}
			}
		
		return childrenByName;
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

		return this.children;
	}

	@Override
	public void printTree() {
		System.out.println("-------");
		printChild2(this,0,true);
	}

	@Override
	public String getSymbol() {
		return this.symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol=symbol;
	}
	
	//
	
	private void printChild(ISyntaxTree node,int level){
				
		System.out.print(node.getSymbol()+level+" --> ");
		level++;
		for (ISyntaxTree child: node.getChildren()){
			System.out.print(child.getSymbol()+level+"  ");
		}
		System.out.println();
		System.out.println();
		for (ISyntaxTree child: node.getChildren()){
			if (child.getChildren().size() > 0){
				printChild(child,level);
			}
		}
	}
	
	private void printChild2(ISyntaxTree node,int level,boolean first){
		
		if (!first){
			for(int i=0;i<level;i++){
				System.out.print("\t");
			}
		}
		System.out.print(node.getSymbol()+"\t");
		first = true;
		for (ISyntaxTree child: node.getChildren()){
			if (child.getChildren().size() > 0){
				printChild2(child,level+1,first);
				first = false;
			}
			else{
				System.out.print(child.getSymbol());
				System.out.println("");
				first = false;
			}
		}
	}

}

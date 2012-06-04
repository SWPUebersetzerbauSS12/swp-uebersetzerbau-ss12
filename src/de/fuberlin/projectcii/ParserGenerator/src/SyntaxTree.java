import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import Extern.ISyntaxTree;


/**
 * 
 * Implementation of the SyntaxTree, composed of SyntaxTree-Nodes
 * 
 * @author Patrick Schlott
 *
 */
public class SyntaxTree implements ISyntaxTree {
	
	private List<ISyntaxTree> children = new LinkedList<ISyntaxTree>();
	private SyntaxTree parent;
	//TODO change to IToken
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

	/**
	 * Returns list of children with this name
	 */
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

	/**
	 * shows the Tree in readable (horizontal) form
	 */
	@Override
	public void printTree() {
		System.out.println("-------");
		printChild(this,0,true);
	}

	@Override
	public String getSymbol() {
		return this.symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol=symbol;
	}
	
	
	//---------------------
	// End of Interface Methodes
	
	private void printChild(ISyntaxTree node,int level,boolean first){
		
		if (!first){
			for(int i=0;i<level;i++){
				System.out.print("\t");
			}
		}
		System.out.print(node.getSymbol()+"\t");
		first = true;
		for (ISyntaxTree child: node.getChildren()){
			if (child.getChildren().size() > 0){
				printChild(child,level+1,first);
				first = false;
			}
			else{
				if (!first){
					for(int i=0;i<level+1;i++){
						System.out.print("\t");
					}
				}
				System.out.print(child.getSymbol());
				System.out.println("");
				first = false;
			}
		}
	}
	
	/**
	 * 
	 * Compresses the SyntaxTree by eliminating all single-child-nodes
	 * 
	 * @author Patrick Schlott
	 *
	 */
	
	public void CompressSyntaxTree(){
		
		boolean newChilds = true;
		
		while (newChilds){
			newChilds = false;
			Vector<ISyntaxTree> singleChilds = new Vector<ISyntaxTree>();
			
			for (ISyntaxTree child:children){
				if (child.getChildren().size() == 1){
					singleChilds.add(child);
					newChilds = true;
				}
			}
			for (ISyntaxTree child:singleChilds){
				child.getChild(0).setParent(this);
				children.set(children.indexOf(child), child.getChild(0));
			}
		}
		for (ISyntaxTree child:children){
			((SyntaxTree) child).CompressSyntaxTree();
		}
	}

}

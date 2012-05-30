package de.fuberlin.optimierung;

import java.util.LinkedList;

public class LLVM_Leaf implements ILLVM_Node{
	String operand = "";
	LinkedList<ILLVM_Node> parents = new LinkedList<ILLVM_Node>();
	
	public LLVM_Leaf(String op, ILLVM_Node par){
		operand = op;
		addParent(par);
	}

	public boolean addParent(ILLVM_Node par) {
		if (!hasParentCommand(par)){
			parents.add(par);
			return true;
		}
		return false;
	}

	public boolean hasParentCommand(ILLVM_Node par) {
		return parents.contains(par);
	}
	
	public boolean removeParent(ILLVM_Node par){
		return 	parents.remove(par);
	}
	
	public boolean isLastParent(){
		return (parents.size() == 1);
	}

	@Override
	public LinkedList<ILLVM_Node> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILLVM_Command getCommand() {
		// TODO Auto-generated method stub
		return null;
	}
}



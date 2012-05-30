package de.fuberlin.optimierung;

import java.util.HashMap;
import java.util.LinkedList;

public class LLVM_Node implements ILLVM_Node{
	protected ILLVM_Command command = null;
	protected LinkedList<ILLVM_Node> parents = new LinkedList<ILLVM_Node>();
	protected LinkedList<ILLVM_Node> children = new LinkedList<ILLVM_Node>();
	
	public LLVM_Node(ILLVM_Command command, HashMap<String, ILLVM_Node> hash){
		this.command = command;
		if (command.getTarget() != null){
			hash.put(command.getTarget().getName(), this);
			System.out.println("new LEAF " + command.getTarget().getName());
		}
		for (LLVM_Parameter operand : command.getOperands()){
			if (!hash.containsKey(operand.getName())){
				LLVM_Leaf leaf = new LLVM_Leaf(operand.getName(), this);
				children.add(leaf);
				hash.put(operand.getName(), leaf);
				System.out.println("new LEAF " + operand.getName());
			}
			else{
				ILLVM_Node node = hash.get(operand.getName());
				node.addParent(this);
				children.add(node);
				System.out.println("existing Child " + operand.getName());
			}
		}
	}

	public LinkedList<ILLVM_Node> getChildren() {
		return children;
	}

	public ILLVM_Command getCommand() {
		return command;
	}
	
	public boolean addParent (ILLVM_Node parent){
		return this.parents.add(parent);
	}
}

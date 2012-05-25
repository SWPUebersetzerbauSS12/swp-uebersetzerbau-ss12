package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Parameter;

public abstract class LLVM_GenericCommand implements ILLVM_Command{
	
	protected ILLVM_Block block;

	protected ILLVM_Command predecessor = null;
	protected ILLVM_Command successor = null;
	
	protected LLVM_Operation operation = null;
	protected LLVM_Parameter target = null;
	protected LinkedList<LLVM_Parameter> operands = new LinkedList<LLVM_Parameter>();;
	
	protected String comment = "";
	
	public LLVM_GenericCommand(LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		// Setze die Zeiger
		this.predecessor = predecessor;
		this.operation = operation;
		this.comment = comment;
		
		// Setze den zugehoerigen Basisblock
		this.setBlock(block);
		
		if(!this.isFirstCommand()) {
			this.predecessor.setSuccessor(this);		
		}
	}
	
	public void deleteCommand() {
		System.out.println("del " + this.toString());

		if (this.isSingleCommand()){
			this.successor = null;
			this.predecessor = null;
		} else if(this.isFirstCommand()) {	// Loesche erstes Element
			this.successor.setPredecessor(null);
			this.getBlock().setFirstCommand(this.successor);
		} else if(this.isLastCommand()) {	// Loesche letztes Element
			this.predecessor.setSuccessor(null);
			this.getBlock().setLastCommand(this.predecessor);
		} else{
			this.predecessor.setSuccessor(this.successor);
			this.successor.setPredecessor(this.predecessor);
		}
	}
	
	public String getComment(){
		if (comment == ""){
			return "\n";
		}else{
			return "; " + comment + "\n";
		}
	}
	
	public boolean isFirstCommand() {
		return (this.predecessor == null);
	}
	
	public boolean isLastCommand() {
		return (this.successor == null);
	}
	
	public boolean isSingleCommand() {
		return (this.isFirstCommand() && this.isLastCommand());
	}
	
	public ILLVM_Command getPredecessor() {
		return this.predecessor;
	}
	public ILLVM_Command getSuccessor() {
		return this.successor;
	}
	public LinkedList<LLVM_Parameter> getOperands() {
		return operands;
	}
	public LLVM_Operation getOperation() {
		return operation;
	}
	public LLVM_Parameter getTarget() {
		return target;
	}
	public ILLVM_Block getBlock() {
		return block;
	}
	
	public void setPredecessor(ILLVM_Command c) {
		this.predecessor = c;
	}
	public void setSuccessor(ILLVM_Command c) {
		this.successor = c;
	}
	public void setOperation(LLVM_Operation operation) {
		this.operation = operation;
	}
	public void setOperands(LinkedList<LLVM_Parameter> operands) {
		this.operands = operands;
	}
	public void setTarget(LLVM_Parameter target) {
		this.target = target;
	}
	public void setBlock(ILLVM_Block block) {
		this.block = block;
	}
}

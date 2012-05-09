package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVMBlock;
import de.fuberlin.optimierung.ILLVMCommand;
import de.fuberlin.optimierung.LLVMOperation;
import de.fuberlin.optimierung.LLVMParameter;

public abstract class LLVM_GenericCommand implements ILLVMCommand{
	
	protected ILLVMBlock block;

	protected ILLVMCommand predecessor = null;
	protected ILLVMCommand successor = null;
	
	protected LLVMOperation operation;
	protected LLVMParameter target;
	protected LinkedList<LLVMParameter> operands;
	
	protected String comment = "";
	
	public LLVM_GenericCommand(LLVMOperation operation, ILLVMCommand predecessor, ILLVMBlock block, String comment){
		// Setze die Zeiger
		this.predecessor = predecessor;
		this.operation = operation;
		this.comment = comment;
		
		if(!this.isFirstCommand()) {
			this.predecessor.setSuccessor(this);		
		}

		// Setze den zugehoerigen Basisblock
		this.setBlock(block);
	}
	
	public void deleteCommand() {
		
		if(this.isFirstCommand()) {	// Loesche erstes Element
			this.successor.setPredecessor(null);
			this.getBlock().setFirstCommand(this.successor);
		}
		if(this.isLastCommand()) {	// Loesche letztes Element
			this.predecessor.setSuccessor(null);
			this.getBlock().setLastCommand(this.predecessor);
		}
 
		this.predecessor.setSuccessor(this.successor);
		this.successor.setPredecessor(this.predecessor);
	}
	
	public String getComment(){
		if (comment == ""){
			return "\n";
		}else{
			return " ; " + comment + "\n";
		}
	}
	
	public boolean isFirstCommand() {
		return (this.predecessor == null);
	}
	
	public boolean isLastCommand() {
		return (this.successor == null);
	}
	
	public boolean isEmpty() {
		return (this.isFirstCommand() && this.isLastCommand());
	}
	
	public ILLVMCommand getPredecessor() {
		return this.predecessor;
	}
	public ILLVMCommand getSuccessor() {
		return this.successor;
	}
	public LinkedList<LLVMParameter> getOperands() {
		return operands;
	}
	public LLVMOperation getOperation() {
		return operation;
	}
	public LLVMParameter getTarget() {
		return target;
	}
	public ILLVMBlock getBlock() {
		return block;
	}
	
	public void setPredecessor(ILLVMCommand c) {
		this.predecessor = c;
	}
	public void setSuccessor(ILLVMCommand c) {
		this.successor = c;
	}
	public void setOperation(LLVMOperation operation) {
		this.operation = operation;
	}
	public void setOperands(LinkedList<LLVMParameter> operands) {
		this.operands = operands;
	}
	public void setTarget(LLVMParameter target) {
		this.target = target;
	}
	public void setBlock(ILLVMBlock block) {
		this.block = block;
	}
}

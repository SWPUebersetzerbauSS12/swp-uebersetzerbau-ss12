package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVMBlock;
import de.fuberlin.optimierung.ILLVMCommand;
import de.fuberlin.optimierung.LLVMOperation;
import de.fuberlin.optimierung.LLVMParameter;

public class LLVM_GenericCommand implements ILLVMCommand{
	
	protected ILLVMBlock block;

	protected ILLVMCommand predecessor = null;
	protected ILLVMCommand successor = null;
	
	protected LLVMOperation operation;
	protected LLVMParameter target;
	protected LinkedList<LLVMParameter> operands;
	
	protected String[] cmd;
	protected int commandEnd;
	
	public LLVM_GenericCommand(String[] cmd, LLVMOperation operation , ILLVMCommand predecessor, ILLVMBlock block){
		// Setze die Zeiger
		this.predecessor = predecessor;
		this.operation = operation;
		this.cmd = cmd;
		
		if(!this.isFirstCommand())
		{
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
	
	public String getCmd(){
		String str = "";
		for(int i = 0; i < cmd.length; i++){
			if(i < cmd.length-1){
				str += cmd[i] + " ";
			}else{
				str += cmd[i];
			}
		}
		return str;
	}
	
	public void changeParam(int position, String param){
		if(position < cmd.length){
			cmd[position] = param;
		}else{
			System.err.println(cmd[0] +" has only "+ (cmd.length -1) +" params");
		}
	}
	
	protected boolean isFirstCommand() {
		return (this.predecessor == null);
	}
	
	private boolean isLastCommand() {
		return (this.successor == null);
	}

	private boolean isEmpty() {
		return (this.isFirstCommand() && this.isLastCommand());
	}
	
	public ILLVMCommand getPredecessor() {
		return this.predecessor;
	}

	public ILLVMCommand getSuccessor() {
		return this.successor;
	}

	public void setPredecessor(ILLVMCommand c) {
		this.predecessor = c;
	}

	public void setSuccessor(ILLVMCommand c) {
		this.successor = c;
	}
	
	public LLVMParameter getTarget() {
		return target;
	}

	public void setOperation(LLVMOperation operation) {
		this.operation = operation;
	}
	
	public LLVMOperation getOperation() {
		return operation;
	}

	public LinkedList<LLVMParameter> getOperands() {
		return operands;
	}

	public void setOperands(LinkedList<LLVMParameter> operands) {
		this.operands = operands;
	}

	public void setTarget(LLVMParameter target) {
		this.target = target;
	}

	public ILLVMBlock getBlock() {
		return block;
	}

	public void setBlock(ILLVMBlock block) {
		this.block = block;
	}
	
}

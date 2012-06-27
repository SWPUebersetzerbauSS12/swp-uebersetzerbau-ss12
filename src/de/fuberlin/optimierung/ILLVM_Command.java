package de.fuberlin.optimierung;

import java.util.LinkedList;

public interface ILLVM_Command {
	
	public void deleteCommand(String source);
	public void replaceCommand(ILLVM_Command c);
	
	public LinkedList<LLVM_Parameter> getOperands();
	public void setOperands(LinkedList<LLVM_Parameter> operands);
	
	public ILLVM_Command getPredecessor();
	public void setPredecessor(ILLVM_Command c);
	public ILLVM_Command getSuccessor();
	public void setSuccessor(ILLVM_Command c);

	public void setOperation(LLVM_Operation operation);
	public LLVM_Operation getOperation();
	
	public void setTarget(LLVM_Parameter target);
	public LLVM_Parameter getTarget();
	
	public void setBlock(ILLVM_Block block);
	public ILLVM_Block getBlock();
	
	public boolean isFirstCommand();
	public boolean isLastCommand();
	public boolean isSingleCommand();
	
	public String toString();
}

package de.fuberlin.optimierung;

import java.util.LinkedList;

public interface ILLVMCommand {
	
	public void deleteCommand();
	
	public LinkedList<LLVMParameter> getOperands();
	public void setOperands(LinkedList<LLVMParameter> operands);
	
	public ILLVMCommand getPredecessor();
	public void setPredecessor(ILLVMCommand c);
	public ILLVMCommand getSuccessor();
	public void setSuccessor(ILLVMCommand c);

	public void setOperation(LLVMOperation operation);
	public LLVMOperation getOperation();
	
	public void setTarget(LLVMParameter target);
	public LLVMParameter getTarget();
	
	public boolean isFirstCommand();
	public boolean isLastCommand();
	public boolean isEmpty();
	
	public String toString();
}

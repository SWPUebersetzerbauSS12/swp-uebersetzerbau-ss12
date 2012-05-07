package de.fuberlin.optimierung;

public interface ILLVMCommand {
	
	public void deleteCommand();
	public String getCmd();
	public void changeParam(int position, String param);
	
	public ILLVMCommand getPredecessor();
	public ILLVMCommand getSuccessor();
	public void setPredecessor(ILLVMCommand c);
	public void setSuccessor(ILLVMCommand c);

	public LLVMOperation getOperation();
	public LLVMParameter getTarget();
}

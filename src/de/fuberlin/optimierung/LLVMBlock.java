package de.fuberlin.optimization.LLVMBlock;

import de.fuberlin.optimization.LLVMCommand;

class LLVMBlock implements ILLVMBlock{
	
	LLVMCommand<List> commands;
	String label;
	
	public LLVMBlock(){
		commands = new LLVMCommand<List>();
	}
	
	public LLVMBlock(String label){
		this.label = label;
		commands = new LLVMCommand<List>();
	}
	
	public void addLabel(String label){
		this.label = label;
	}
	
	public void addCmdLine(String cmd){
		LLVMCommand cmdLine = new LLVMCommand(cmd);
		commands.add(cmdLine);
	}
}
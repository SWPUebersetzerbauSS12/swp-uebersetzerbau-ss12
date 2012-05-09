package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVMBlock;
import de.fuberlin.optimierung.ILLVMCommand;
import de.fuberlin.optimierung.LLVMOperation;
import de.fuberlin.optimierung.LLVMParameter;

/*
 * Syntax:

  ret <type> <value>       ; Return a value from a non-void function
  ret void                 ; Return from void function

 */

public class LLVM_ReturnCommand extends LLVM_GenericCommand{
	
	public LLVM_ReturnCommand(String[] cmd, LLVMOperation operation, ILLVMCommand predecessor, ILLVMBlock block, String comment){
		super(operation, predecessor, block, comment);
		// Init operands
		
		if (cmd.length == 2){
			target = new LLVMParameter(cmd[1], cmd[1]);
		}else if (cmd.length == 3){
			target = new LLVMParameter(cmd[2], cmd[1]);
		}
		
		System.out.println("Operation generiert: ");
		System.out.println(this.toString());
	}
	
	public String toString() {
		String cmd_output = "ret ";
		
		switch(operation){
			case RET :
				cmd_output += target.getTypeString() + " ";
				break;
			case RET_CODE :
				cmd_output += target.getTypeString() + " ";
				cmd_output += target.getName() + " ";
				break;
			default:
				return "";
		}
		
		cmd_output += getComment();
		
		return cmd_output;
	}
}

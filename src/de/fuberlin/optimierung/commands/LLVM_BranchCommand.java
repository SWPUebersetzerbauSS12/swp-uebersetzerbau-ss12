package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVMBlock;
import de.fuberlin.optimierung.ILLVMCommand;
import de.fuberlin.optimierung.LLVMOperation;
import de.fuberlin.optimierung.LLVMParameter;

/*
 * Syntax:

  br i1 <cond>, label <iftrue>, label <iffalse>
  br label <dest>          ; Unconditional branch

 */

public class LLVM_BranchCommand extends LLVM_GenericCommand{
	
	public LLVM_BranchCommand(String[] cmd, LLVMOperation operation, ILLVMCommand predecessor, ILLVMBlock block, String comment){
		super(operation, predecessor, block, comment);
		// Init operands
		operands = new LinkedList<LLVMParameter>();
		
		if (cmd.length == 3){
			operands.add(new LLVMParameter(cmd[2], cmd[1]));
		}else if (cmd.length > 3){
			operands.add(new LLVMParameter(cmd[2], cmd[1]));
			operands.add(new LLVMParameter(cmd[4], cmd[3]));
			operands.add(new LLVMParameter(cmd[6], cmd[5]));
		}
		
		System.out.println("Operation generiert: ");
		System.out.println(this.toString());
	}
	
	public String toString() {
		String cmd_output = "br ";
		
		switch(operation){
			case BR :
				cmd_output += operands.get(0).getTypeString() + " ";
				cmd_output += operands.get(0).getName() + " ";
				break;
			case BR_CON :
				cmd_output += operands.get(0).getTypeString() + " ";
				cmd_output += operands.get(0).getName() + ", ";
				cmd_output += operands.get(1).getTypeString() + " ";
				cmd_output += operands.get(1).getName() + ", ";
				cmd_output += operands.get(2).getTypeString() + " ";
				cmd_output += operands.get(2).getName() + " ";
				break;
			default:
				return "";
		}
		
		cmd_output += getComment();
		
		return cmd_output;
	}
}

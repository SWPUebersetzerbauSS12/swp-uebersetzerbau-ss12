package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Syntax:

  br i1 <cond>, label <iftrue>, label <iffalse>
  br label <dest>          ; Unconditional branch

 */

public class LLVM_BranchCommand extends LLVM_GenericCommand{
	
	public LLVM_BranchCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		
		if (cmd.length == 3){
			// unconditional Branch
			// <dest> label
			operands.add(new LLVM_Parameter(cmd[2], cmd[1]));
		}else if (cmd.length > 3){
			// conditional Branch
			// <cond> i1
			operands.add(new LLVM_Parameter(cmd[2], cmd[1]));
			// <iftrue> label
			operands.add(new LLVM_Parameter(cmd[4], cmd[3]));
			// <iffalse> label
			operands.add(new LLVM_Parameter(cmd[6], cmd[5]));
		}
		
		System.out.println("Operation generiert: " + this.toString());
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
		
		cmd_output += " " + getComment();
		
		return cmd_output;
	}
}

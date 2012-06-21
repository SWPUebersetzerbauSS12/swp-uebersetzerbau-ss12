package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Syntax:

  br i1 <cond>, label <iftrue>, label <iffalse>
  br label <dest>          ; Unconditional branch

 */

public class LLVM_BranchCommand extends LLVM_GenericCommand{
	
	public LLVM_BranchCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		
		if (cmdLine.contains("i1")){
			setOperation(LLVM_Operation.BR_CON);
		}else{
			setOperation(LLVM_Operation.BR);
		}
		
		String[] cmd = cmdLine.split(" ");
		if (this.operation == LLVM_Operation.BR){
			// unconditional Branch
			// <dest> label
			operands.add(new LLVM_Parameter(cmd[2], cmd[1]));
		}else{
			// conditional Branch
			// <cond> i1
			operands.add(new LLVM_Parameter(cmd[2], cmd[1]));
			// <iftrue> label
			operands.add(new LLVM_Parameter(cmd[4], cmd[3]));
			// <iffalse> label
			operands.add(new LLVM_Parameter(cmd[6], cmd[5]));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
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

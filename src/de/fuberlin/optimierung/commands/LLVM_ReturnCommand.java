package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Syntax:

  ret <type> <value>       ; Return a value from a non-void function
  ret void                 ; Return from void function

 */

public class LLVM_ReturnCommand extends LLVM_GenericCommand{
	
	public LLVM_ReturnCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		
		if (cmdLine.contains("void")){
			setOperation(LLVM_Operation.RET);
		}else{
			setOperation(LLVM_Operation.RET_CODE);
		}
		
		String[] cmd = cmdLine.split(" ");
		if (cmd.length == 2){
			// ohne Return-Code 
			operands.add(new LLVM_Parameter(cmd[1], cmd[1]));
		}else if (cmd.length == 3){
			// mit Return-Code
			operands.add(new LLVM_Parameter(cmd[2], cmd[1]));
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_output = "ret ";
		
		switch(operation){
			case RET :
				cmd_output += operands.get(0).getTypeString() + " ";
				break;
			case RET_CODE :
				cmd_output += operands.get(0).getTypeString() + " ";
				cmd_output += operands.get(0).getName() + " ";
				break;
			default:
				return "";
		}
		
		cmd_output += " " + getComment();
		
		return cmd_output;
	}
}

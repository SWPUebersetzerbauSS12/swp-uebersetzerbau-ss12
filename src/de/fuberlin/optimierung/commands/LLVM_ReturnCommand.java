package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Syntax:

  ret <type> <value>       ; Return a value from a non-void function
  ret void                 ; Return from void function

 */

public class LLVM_ReturnCommand extends LLVM_GenericCommand{
	
	public LLVM_ReturnCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		// Init operands
		
		if (cmd.length == 2){
			target = new LLVM_Parameter(cmd[1], cmd[1]);
		}else if (cmd.length == 3){
			target = new LLVM_Parameter(cmd[2], cmd[1]);
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

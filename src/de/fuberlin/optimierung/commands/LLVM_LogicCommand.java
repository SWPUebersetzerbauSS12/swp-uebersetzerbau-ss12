package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Syntax:

  <result> = and <ty> <op1>, <op2>   ; yields {ty}:result

 */

public class LLVM_LogicCommand extends LLVM_GenericCommand{
	
	public LLVM_LogicCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		
		// <result> <ty>
		target = new LLVM_Parameter(cmd[0], cmd[3]);
		// <op1> <ty>
		operands.add(new LLVM_Parameter(cmd[4], cmd[3]));
		// <op2> <ty>
		operands.add(new LLVM_Parameter(cmd[5], cmd[3]));
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_output = target.getName()+" = ";
		
		switch(operation){
			case AND :
				cmd_output +="and ";
				break;
			case OR :
				cmd_output +="or ";
				break;
			case XOR :
				cmd_output +="xor ";
				break;
			default:
				return "";
		}
		
		cmd_output += operands.get(0).getTypeString()+" ";
		cmd_output += operands.get(0).getName()+", ";
		cmd_output += operands.get(1).getName();
		
		cmd_output += " " + getComment();
		
		return cmd_output;
	}
}

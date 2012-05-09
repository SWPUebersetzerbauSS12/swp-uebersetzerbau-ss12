package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVMBlock;
import de.fuberlin.optimierung.ILLVMCommand;
import de.fuberlin.optimierung.LLVMOperation;
import de.fuberlin.optimierung.LLVMParameter;

/*
 * Syntax:

  <result> = and <ty> <op1>, <op2>   ; yields {ty}:result

 */

public class LLVM_LogicCommand extends LLVM_GenericCommand{
	
	public LLVM_LogicCommand(String[] cmd, LLVMOperation operation, ILLVMCommand predecessor, ILLVMBlock block){
		super(operation, predecessor, block);
		// Init operands
		operands = new LinkedList<LLVMParameter>();
		
		target = new LLVMParameter(cmd[0], cmd[3]);
		operands.add(new LLVMParameter(cmd[4], cmd[3]));
		operands.add(new LLVMParameter(cmd[5], cmd[3]));
		
		System.out.println("Operation generiert: ");
		System.out.println(this.toString());
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
		
		cmd_output += tail+"\n";

		return cmd_output;
	}
}

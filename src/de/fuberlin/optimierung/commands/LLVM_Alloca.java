package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVMBlock;
import de.fuberlin.optimierung.ILLVMCommand;
import de.fuberlin.optimierung.LLVMOperation;
import de.fuberlin.optimierung.LLVMParameter;

/*
 * Syntax:

  <result> = alloca <type>[, <ty> <NumElements>][, align <alignment>]     ; yields {type*}:result
 */

public class LLVM_Alloca extends LLVM_GenericCommand{
	
	public LLVM_Alloca(String[] cmd, LLVMOperation operation, ILLVMCommand predecessor, ILLVMBlock block, String comment){
		super(operation, predecessor, block, comment);
		// Init operands
		operands = new LinkedList<LLVMParameter>();
		
		target = new LLVMParameter(cmd[0], cmd[3]);
		
		for (int j = 4; (j + 1 < cmd.length); j = j + 2){
			operands.add(new LLVMParameter(cmd[j+1], cmd[j]));
		}
		
		System.out.println("Operation generiert: ");
		System.out.println(this.toString());
	}
	
	public String toString() {
		String cmd_output = target.getName()+" = ";
		
		switch(operation){
			case ALLOCA :
				cmd_output +="alloca ";
				break;
			default:
				return "";
		}
		
		cmd_output += target.getTypeString();
		
		for (int i = 0; i < operands.size(); i++){
			cmd_output += ", " + operands.get(i).getTypeString() + " ";
			cmd_output += operands.get(i).getName();
		}
		
		cmd_output += getComment();

		return cmd_output;
	}
}

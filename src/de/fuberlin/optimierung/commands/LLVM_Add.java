package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVMBlock;
import de.fuberlin.optimierung.ILLVMCommand;
import de.fuberlin.optimierung.LLVMOperation;
import de.fuberlin.optimierung.LLVMParameter;

/*
 * Syntax:

  <result> = add <ty> <op1>, <op2>          ; yields {ty}:result
  <result> = add nuw <ty> <op1>, <op2>      ; yields {ty}:result
  <result> = add nsw <ty> <op1>, <op2>      ; yields {ty}:result
  <result> = add nuw nsw <ty> <op1>, <op2>  ; yields {ty}:result
 */

public class LLVM_Add extends LLVM_GenericCommand{
	
	@SuppressWarnings("unused")
	private boolean has_nuw = false;
	@SuppressWarnings("unused")
	private boolean has_nsw = false;
	
	public LLVM_Add(String[] cmd, ILLVMCommand predecessor, ILLVMBlock block){
		super(cmd, LLVMOperation.ADD, predecessor, block);
		// Init operands
		operands = new LinkedList<LLVMParameter>();
		
		int i = -1;
		for (int j = 0; j < cmd.length; j++){
			if (cmd[j].contains(",")){
				i = j;
			}		
		}
		this.commandEnd = i+1;
		
		switch(i){
			case 5 :
				if(this.cmd[3].equals("nuw"))
					has_nuw = true;
				else
					has_nsw = true;
				target = new LLVMParameter(this.cmd[0], this.cmd[4]);
				operands.add(new LLVMParameter(this.cmd[5], this.cmd[4]));
				operands.add(new LLVMParameter(this.cmd[6], this.cmd[4]));
				break;
			case 6 :
				has_nuw = true;
				has_nsw = true;
				target = new LLVMParameter(this.cmd[0], this.cmd[5]);
				operands.add(new LLVMParameter(this.cmd[6], this.cmd[5]));
				operands.add(new LLVMParameter(this.cmd[7], this.cmd[5]));
				break;
			default:
				target = new LLVMParameter(this.cmd[0], this.cmd[3]);
				operands.add(new LLVMParameter(this.cmd[4], this.cmd[3]));
				operands.add(new LLVMParameter(this.cmd[5], this.cmd[3]));
				break;
		}
		
		System.out.println("Add generiert: " + getCmd());
		System.out.println("Target: " + this.target.getName());
		System.out.println("Operandtyp: " + operands.get(0).getTypeString());
		System.out.println("Operand: " + operands.get(0).getName());
		System.out.println("Operand: " + operands.get(1).getName()+"\n");
	}
}

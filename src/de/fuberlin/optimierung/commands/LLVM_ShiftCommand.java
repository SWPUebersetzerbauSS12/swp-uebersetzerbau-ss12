package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Syntax: sample "shl" (shifted to left)

  <result> = shl <ty> <op1>, <op2>          ; yields {ty}:result
  <result> = shl nuw <ty> <op1>, <op2>      ; yields {ty}:result
  <result> = shl nsw <ty> <op1>, <op2>      ; yields {ty}:result
  <result> = shl nuw nsw <ty> <op1>, <op2>  ; yields {ty}:result
  
  sample "lshr" (logical shift right) but also "ashr" (arithmetic shift right)  
  <result> = lshr <ty> <op1>, <op2>         ; yields {ty}:result
  <result> = lshr exact <ty> <op1>, <op2>   ; yields {ty}:result
 */

public class LLVM_ShiftCommand extends LLVM_GenericCommand{

	private boolean has_nuw = false;
	private boolean has_nsw = false;
	
	public LLVM_ShiftCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block) {
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.SHL);
		
		String[] cmd = cmdLine.split(" ");
		// Kommaposition ermitteln
		int i = -1;
		for (int j = 0; j < cmd.length; j++){
			if (cmd[j].contains(",")){
				i = j;
			}		
		}
		// for target and operands
		// <result> <ty>
		// <op1> <ty>
		// <op2> <ty>
		switch(i){
			case 5 :
				// Fall 2 oder Fall 3
				if(cmd[3].equals("nuw"))
					has_nuw = true;
				else
					has_nsw = true;
				target = new LLVM_Parameter(cmd[0], cmd[4]);
				operands.add(new LLVM_Parameter(cmd[5], cmd[4]));
				operands.add(new LLVM_Parameter(cmd[6], cmd[4]));
				break;
			case 6 :
				// Fall 4
				has_nuw = true;
				has_nsw = true;
				target = new LLVM_Parameter(cmd[0], cmd[5]);
				operands.add(new LLVM_Parameter(cmd[6], cmd[5]));
				operands.add(new LLVM_Parameter(cmd[7], cmd[5]));
				break;
			default:
				// Fall 1
				target = new LLVM_Parameter(cmd[0], cmd[3]);
				operands.add(new LLVM_Parameter(cmd[4], cmd[3]));
				operands.add(new LLVM_Parameter(cmd[5], cmd[3]));
				break;
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}

	public String toString() {
		String cmd_output = target.getName()+" = ";
		
		switch(operation) {
		case SHL :
			cmd_output = "shl ";
		case LSHR :
			cmd_output = "lshr ";
		case ASHR: 
			cmd_output = "ashr ";
		}
		
		cmd_output += has_nuw==true?"unw ":"";
		cmd_output += has_nsw==true?"nsw ":"";
		cmd_output += operands.get(0).getTypeString()+" ";
		cmd_output += operands.get(0).getName()+", ";
		cmd_output += operands.get(1).getName();
		
		cmd_output += " " + getComment();
		
		return cmd_output;
	}
}

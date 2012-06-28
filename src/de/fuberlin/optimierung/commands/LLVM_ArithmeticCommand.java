package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Syntax: sample "add" but also for "sub", "mul", "div"

  <result> = add <ty> <op1>, <op2>          ; yields {ty}:result
  <result> = add nuw <ty> <op1>, <op2>      ; yields {ty}:result
  <result> = add nsw <ty> <op1>, <op2>      ; yields {ty}:result
  <result> = add nuw nsw <ty> <op1>, <op2>  ; yields {ty}:result
 */

public class LLVM_ArithmeticCommand extends LLVM_GenericCommand{
	
	private boolean has_nuw = false;
	private boolean has_nsw = false;
	
	public LLVM_ArithmeticCommand(){
		super();
	}
	
	public LLVM_ArithmeticCommand(String cmdLine, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(operation);
		// Kommentar entfernen
		if (cmdLine.contains(";")) cmdLine = cmdLine.substring(0, cmdLine.indexOf(";"));
		
		String[] cmd = command.split("[ \t]");
		// Kommaposition ermitteln
		int i = -1;
		for (int j = 0; j < cmd.length; j++){
			if (cmd[j].contains(",")){
				i = j;
			}		
		}
		
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
		
		switch(operation){
			case ADD :
				cmd_output +="add ";
				break;
			case SUB :
				cmd_output +="sub ";
				break;
			case MUL :
				cmd_output +="mul ";
				break;
			case DIV :
				cmd_output +="div ";
				break;
			case UREM :
				cmd_output +="urem ";
				break;
			case SREM :
				cmd_output +="srem ";
				break;
			default:
				return "";
		}
		
		cmd_output += has_nuw==true?"nuw ":"";
		cmd_output += has_nsw==true?"nsw ":"";
		cmd_output += operands.get(0).getTypeString()+" ";
		cmd_output += operands.get(0).getName()+", ";
		cmd_output += operands.get(1).getName();
		
		cmd_output += " " + getComment();
		
		return cmd_output;
	}
}

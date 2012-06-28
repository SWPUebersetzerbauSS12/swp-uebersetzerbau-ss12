package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Syntax:

  <result> = and <ty> <op1>, <op2>   ; yields {ty}:result

 */

public class LLVM_LogicCommand extends LLVM_GenericCommand{
	
	public LLVM_LogicCommand(String cmdLine, LLVM_Operation operation, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(operation);
		// Kommentar entfernen
		if (cmdLine.contains(";")) cmdLine = cmdLine.substring(0, cmdLine.indexOf(";"));
		
		String[] cmd = command.split("[ \t]");
		
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

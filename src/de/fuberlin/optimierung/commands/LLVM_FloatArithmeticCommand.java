package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Syntax: sample "fadd" but also for "fsub", "fmul", "fdiv"
 * 
 * <result> = fadd <ty> <op1>, <op2>   ; yields {ty}:result
 */

public class LLVM_FloatArithmeticCommand extends LLVM_GenericCommand{

	public LLVM_FloatArithmeticCommand(String cmdLine, LLVM_Operation operation, LLVM_GenericCommand predecessor, LLVM_Block block) {
		super(predecessor, block, cmdLine);
		setOperation(operation);
		
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		String result = parseReadResult(cmd);
		parseReadValue(cmd); // Operation löschen

		String ty = parseReadType(cmd);
		
		String op1 = parseReadValue(cmd);
		parseEraseString(cmd, ",");
		String op2 = parseReadValue(cmd);
		
		target = new LLVM_Parameter(result, ty);
		operands.add(new LLVM_Parameter(op1, ty));
		operands.add(new LLVM_Parameter(op2, ty));
	
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		
String cmd_output = target.getName()+" = ";
		
		switch(operation){
			case FADD :
				cmd_output +="fadd ";
				break;
			case FSUB :
				cmd_output +="fsub ";
				break;
			case FMUL :
				cmd_output +="fmul ";
				break;
			case FDIV :
				cmd_output +="fdiv ";
				break;
			case FREM :
				cmd_output +="frem ";
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

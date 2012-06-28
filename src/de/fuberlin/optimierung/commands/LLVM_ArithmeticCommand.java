package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

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
	
	public LLVM_ArithmeticCommand(String cmdLine, LLVM_Operation operation, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(operation);
		
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		String result = parseReadResult(cmd);
		parseReadValue(cmd); // Operation löschen

		has_nuw = parseOptionalString(cmd, "nuw");
		has_nsw = parseOptionalString(cmd, "nsw");
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

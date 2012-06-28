package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Syntax:
  <result> = shl <ty> <op1>, <op2>           ; yields {ty}:result
  <result> = shl nuw <ty> <op1>, <op2>       ; yields {ty}:result
  <result> = shl nsw <ty> <op1>, <op2>       ; yields {ty}:result
  <result> = shl nuw nsw <ty> <op1>, <op2>   ; yields {ty}:result
  <result> = lshr <ty> <op1>, <op2>         ; yields {ty}:result
  <result> = lshr exact <ty> <op1>, <op2>   ; yields {ty}:result
  <result> = ashr <ty> <op1>, <op2>         ; yields {ty}:result
  <result> = ashr exact <ty> <op1>, <op2>   ; yields {ty}:result
  <result> = and <ty> <op1>, <op2>   ; yields {ty}:result
  <result> = or <ty> <op1>, <op2>   ; yields {ty}:result
  <result> = xor <ty> <op1>, <op2>   ; yields {ty}:result
 */

public class LLVM_BitwiseBinaryCommand extends LLVM_GenericCommand{
	private boolean has_nuw = false;
	private boolean has_nsw = false;
	private boolean has_exact = false;
	
	public LLVM_BitwiseBinaryCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(operation);
		
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		String result = parseReadResult(cmd);
		
		String cond = parseReadValue(cmd);
		
		// cond festlegen
		if (cond.startsWith("shl")){
			setOperation(LLVM_Operation.SHL);
		}else if (cond.startsWith("lshr")){
			setOperation(LLVM_Operation.LSHR);
		}else if (cond.startsWith("ashr")){
			setOperation(LLVM_Operation.ASHR);
		}else if (cond.startsWith("and")){
			setOperation(LLVM_Operation.AND);
		}else if (cond.startsWith("or")){
			setOperation(LLVM_Operation.OR);
		}else if (cond.startsWith("xor")){
			setOperation(LLVM_Operation.XOR);
		}
		
		has_nuw = parseOptionalString(cmd, "nuw");
		has_nsw = parseOptionalString(cmd, "nsw");
		has_exact = parseOptionalString(cmd, "exact");
		
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
			case AND :
				cmd_output +="and ";
				break;
			case OR :
				cmd_output +="or ";
				break;
			case XOR :
				cmd_output +="xor ";
				break;
			case SHL :
				cmd_output +="shl ";
				break;
			case ASHR :
				cmd_output +="ashr ";
				break;
			case LSHR :
				cmd_output +="lshr ";
				break;
			default:
				return "";
		}
		
		cmd_output += has_nuw==true?"nuw ":"";
		cmd_output += has_nsw==true?"nsw ":"";
		cmd_output += has_exact==true?"exact ":"";
		
		cmd_output += operands.get(0).getTypeString()+" ";
		cmd_output += operands.get(0).getName()+", ";
		cmd_output += operands.get(1).getName();
		
		cmd_output += " " + getComment();
		
		return cmd_output;
	}
}

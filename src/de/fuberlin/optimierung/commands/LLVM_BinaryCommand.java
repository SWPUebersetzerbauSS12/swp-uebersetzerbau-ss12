package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Syntax:
 *   sample "add" but also for "sub", "mul", "div"
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

public class LLVM_BinaryCommand extends LLVM_GenericCommand{
	private boolean has_nuw = false;
	private boolean has_nsw = false;
	private boolean has_exact = false;
	
	public LLVM_BinaryCommand(){
	}
	
	public LLVM_BinaryCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
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
		}else if (cond.startsWith("add")){
			setOperation(LLVM_Operation.ADD);
		}else if (cond.startsWith("fadd")){
			setOperation(LLVM_Operation.FADD);
		}else if (cond.startsWith("sub")){
			setOperation(LLVM_Operation.SUB);
		}else if (cond.startsWith("fsub")){
			setOperation(LLVM_Operation.FSUB);
		}else if (cond.startsWith("mul")){
			setOperation(LLVM_Operation.MUL);
		}else if (cond.startsWith("fmul")){
			setOperation(LLVM_Operation.FMUL);
		}else if (cond.startsWith("udiv")){
			setOperation(LLVM_Operation.UDIV);
		}else if (cond.startsWith("sdiv")){
			setOperation(LLVM_Operation.SDIV);
		}else if (cond.startsWith("fdiv")){
			setOperation(LLVM_Operation.FDIV);
		}else if (cond.startsWith("urem")){
			setOperation(LLVM_Operation.UREM);
		}else if (cond.startsWith("srem")){
			setOperation(LLVM_Operation.SREM);
		}else if (cond.startsWith("frem")){
			setOperation(LLVM_Operation.FREM);
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
			case ADD :
				cmd_output +="add ";
				break;
			case FADD :
				cmd_output +="fadd ";
				break;
			case SUB :
				cmd_output +="sub ";
				break;
			case FSUB :
				cmd_output +="fsub ";
				break;
			case MUL :
				cmd_output +="mul ";
				break;
			case FMUL :
				cmd_output +="fmul ";
				break;
			case UDIV :
				cmd_output +="udiv ";
				break;
			case SDIV :
				cmd_output +="sdiv ";
				break;
			case FDIV :
				cmd_output +="fdiv ";
				break;
			case UREM :
				cmd_output +="urem ";
				break;
			case SREM :
				cmd_output +="srem ";
				break;
			case FREM :
				cmd_output +="frem ";
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

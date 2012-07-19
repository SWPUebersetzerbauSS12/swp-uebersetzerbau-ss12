package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.*;

/*
 * Syntax:

  <result> = icmp <cond> <ty> <op1>, <op2>   ; yields {i1} or {<N x i1>}:result
  <result> = fcmp <cond> <ty> <op1>, <op2>     ; yields {i1} or {<N x i1>}:result
 */

public class LLVM_XcmpCommand extends LLVM_GenericCommand{
	
	public LLVM_XcmpCommand(String cmdLine, LLVM_GenericCommand predecessor, LLVM_Block block){
		super(predecessor, block, cmdLine);
		
		StringBuilder cmd = new StringBuilder(cmdLine);
		parseEraseComment(cmd);
		String result = parseReadResult(cmd);
		String command = parseReadValue(cmd);
		target = new LLVM_Parameter(result, "i1");
		
		String cond = parseReadValue(cmd);
		
		// cond festlegen
		if (cond.startsWith("eq")){
			setOperation(LLVM_Operation.ICMP_EQ);
		}else if (cond.startsWith("ne")){
			setOperation(LLVM_Operation.ICMP_NE);
		}else if (cond.startsWith("ugt")){
			if (command.equals("icmp")){
				setOperation(LLVM_Operation.ICMP_UGT);
			}else if (command.equals("fcmp")){
				setOperation(LLVM_Operation.FCMP_UGT);
			}
		}else if (cond.startsWith("uge")){
			if (command.equals("icmp")){
				setOperation(LLVM_Operation.ICMP_UGE);
			}else if (command.equals("fcmp")){
				setOperation(LLVM_Operation.FCMP_UGE);
			}
		}else if (cond.startsWith("ult")){
			if (command.equals("icmp")){
				setOperation(LLVM_Operation.ICMP_ULT);
			}else if (command.equals("fcmp")){
				setOperation(LLVM_Operation.FCMP_ULT);
			}
		}else if (cond.startsWith("ule")){
			if (command.equals("icmp")){
				setOperation(LLVM_Operation.ICMP_ULE);
			}else if (command.equals("fcmp")){
				setOperation(LLVM_Operation.FCMP_ULE);
			}
		}else if (cond.startsWith("sgt")){
			setOperation(LLVM_Operation.ICMP_SGT);
		}else if (cond.startsWith("sge")){
			setOperation(LLVM_Operation.ICMP_SGE);
		}else if (cond.startsWith("slt")){
			setOperation(LLVM_Operation.ICMP_SLT);
		}else if (cond.startsWith("sle")){
			setOperation(LLVM_Operation.ICMP_SLE);
		}else if (cond.startsWith("false")){
			setOperation(LLVM_Operation.FCMP_FALSE);
		}else if (cond.startsWith("oeq")){
			setOperation(LLVM_Operation.FCMP_OEQ);
		}else if (cond.startsWith("ogt")){
			setOperation(LLVM_Operation.FCMP_OGT);
		}else if (cond.startsWith("oge")){
			setOperation(LLVM_Operation.FCMP_OGE);
		}else if (cond.startsWith("olt")){
			setOperation(LLVM_Operation.FCMP_OLT);
		}else if (cond.startsWith("ole")){
			setOperation(LLVM_Operation.FCMP_OLE);
		}else if (cond.startsWith("one")){
			setOperation(LLVM_Operation.FCMP_ONE);
		}else if (cond.startsWith("ord")){
			setOperation(LLVM_Operation.FCMP_ORD);
		}else if (cond.startsWith("ueq")){
			setOperation(LLVM_Operation.FCMP_UEQ);			
		}else if (cond.startsWith("une")){
			setOperation(LLVM_Operation.FCMP_UNE);
		}else if (cond.startsWith("uno")){
			setOperation(LLVM_Operation.FCMP_UNO);
		}else if (cond.startsWith("true")){
			setOperation(LLVM_Operation.FCMP_TRUE);
		}
		
		String ty = parseReadType(cmd);
		String op1 = parseReadValue(cmd);
		parseEraseString(cmd, ",");
		String op2 = parseReadValue(cmd);
		
		operands.add(new LLVM_Parameter(op1, ty));
		operands.add(new LLVM_Parameter(op2, ty));
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " +  this.toString());
	}
	
	public String toString() {
		if (target == null || operands == null || operands.size() < 2) return null;
		
		String cmd_output = target.getName()+" = ";
		cmd_output += "icmp ";
		
		switch(operation){
			case ICMP_EQ :
				cmd_output +="eq ";
				break;
			case ICMP_NE :
				cmd_output +="ne ";
				break;
			case ICMP_UGT :
				cmd_output +="ugt ";
				break;
			case ICMP_UGE :
				cmd_output +="uge ";
				break;
			case ICMP_ULT :
				cmd_output +="ult ";
				break;
			case ICMP_ULE :
				cmd_output +="ule ";
				break;
			case ICMP_SGT :
				cmd_output +="sgt ";
				break;
			case ICMP_SGE :
				cmd_output +="sge ";
				break;
			case ICMP_SLT :
				cmd_output +="slt ";
				break;
			case ICMP_SLE :
				cmd_output +="sle ";
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

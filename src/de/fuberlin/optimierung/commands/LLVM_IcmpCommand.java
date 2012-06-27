package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Syntax:

  <result> = icmp <cond> <ty> <op1>, <op2>   ; yields {i1} or {<N x i1>}:result

 */

public class LLVM_IcmpCommand extends LLVM_GenericCommand{
	
	public LLVM_IcmpCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		// Kommentar entfernen
		if (cmdLine.contains(";")) cmdLine = cmdLine.substring(0, cmdLine.indexOf(";"));
		
		// result einlesen
		String result = cmdLine.substring(0, cmdLine.indexOf("=")).trim();
		cmdLine = cmdLine.substring(cmdLine.indexOf("icmp ") + 4).trim();
		
		target = new LLVM_Parameter(result, "i1");
		
		// cond festlegen
		if (cmdLine.startsWith("eq")){
			setOperation(LLVM_Operation.ICMP_EQ);
		}else if (cmdLine.startsWith("ne")){
			setOperation(LLVM_Operation.ICMP_NE);
		}else if (cmdLine.startsWith("ugt")){
			setOperation(LLVM_Operation.ICMP_UGT);
		}else if (cmdLine.startsWith("uge")){
			setOperation(LLVM_Operation.ICMP_UGE);
		}else if (cmdLine.startsWith("ult")){
			setOperation(LLVM_Operation.ICMP_ULT);
		}else if (cmdLine.startsWith("ule")){
			setOperation(LLVM_Operation.ICMP_ULE);
		}else if (cmdLine.startsWith("sgt")){
			setOperation(LLVM_Operation.ICMP_SGT);
		}else if (cmdLine.startsWith("sge")){
			setOperation(LLVM_Operation.ICMP_SGE);
		}else if (cmdLine.startsWith("slt")){
			setOperation(LLVM_Operation.ICMP_SLT);
		}else if (cmdLine.startsWith("sle")){
			setOperation(LLVM_Operation.ICMP_SLE);
		}
		// cond entfernen
		cmdLine = cmdLine.substring(cmdLine.indexOf(" ")).trim();
		
		// ty einlesen
		int count = getComplexStructEnd(cmdLine);
		String ty = cmdLine.substring(0, cmdLine.indexOf(" ", count));
		cmdLine = cmdLine.substring(cmdLine.indexOf(" ", count)).trim();
		
		// ops einlesen
		String op1 = cmdLine.substring(0, cmdLine.indexOf(",")).trim();
		String op2 = cmdLine.substring(cmdLine.indexOf(",")+1).trim();
		
		operands.add(new LLVM_Parameter(op1, ty));
		operands.add(new LLVM_Parameter(op2, ty));
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " +  this.toString());
	}
	
	public String toString() {
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

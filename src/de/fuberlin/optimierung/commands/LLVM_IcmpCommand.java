package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
 * Syntax:

  <result> = icmp <cond> <ty> <op1>, <op2>   ; yields {i1} or {<N x i1>}:result

 */

public class LLVM_IcmpCommand extends LLVM_GenericCommand{
	
	public LLVM_IcmpCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		
		// <result> i1
		target = new LLVM_Parameter(cmd[0], "i1");
		// <op1> <ty>
		operands.add(new LLVM_Parameter(cmd[5], cmd[4]));
		// <op2> <ty>
		operands.add(new LLVM_Parameter(cmd[6], cmd[4]));
		
		System.out.println("Operation generiert: " +  this.toString());
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

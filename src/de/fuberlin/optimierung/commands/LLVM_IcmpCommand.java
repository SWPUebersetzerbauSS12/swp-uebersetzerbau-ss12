package de.fuberlin.optimierung.commands;

import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVMBlock;
import de.fuberlin.optimierung.ILLVMCommand;
import de.fuberlin.optimierung.LLVMOperation;
import de.fuberlin.optimierung.LLVMParameter;

/*
 * Syntax:

  <result> = icmp <cond> <ty> <op1>, <op2>   ; yields {i1} or {<N x i1>}:result

 */

public class LLVM_IcmpCommand extends LLVM_GenericCommand{
	
	public LLVM_IcmpCommand(String[] cmd, LLVMOperation operation, ILLVMCommand predecessor, ILLVMBlock block, String comment){
		super(operation, predecessor, block, comment);
		// Init operands
		operands = new LinkedList<LLVMParameter>();
		
		target = new LLVMParameter(cmd[0], cmd[4]);
		operands.add(new LLVMParameter(cmd[5], cmd[4]));
		operands.add(new LLVMParameter(cmd[6], cmd[4]));
		
		System.out.println("Operation generiert: ");
		System.out.println(this.toString());
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
		
		cmd_output += getComment();
		
		return cmd_output;
	}
}

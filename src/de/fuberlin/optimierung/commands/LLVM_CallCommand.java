package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
* Syntax:
  
  <result> = [tail] call [cconv] [ret attrs] <ty> [<fnty>*] <fnptrval>(<function args>) [fn attrs]
*/

public class LLVM_CallCommand extends LLVM_GenericCommand{
	private boolean tail = false;
	private String cconv = "";
	private String reta = "";
	private String rest = "";
	
	public LLVM_CallCommand(String[] cmd, LLVM_Operation operation, ILLVM_Command predecessor, ILLVM_Block block, String comment){
		super(operation, predecessor, block, comment);
		
		// tail?
		if (cmd[2].trim().equals("tail")){
			tail = true;
		}
		
		int start = 3;
		int end = 0;
		start = (tail) ? start + 1 : start;
		
		//cconv angegeben?
		if (cmd.length >= start && (cmd[start].trim().equals("ccc") ||
			cmd[start].trim().equals("fastcc") ||
			cmd[start].trim().equals("coldcc")	||
			cmd[start].trim().equals("cc"))){
			if (cmd[start].trim().equals("cc")){
				cconv = cmd[start] + " " + cmd[start+1];
				start = start + 2;
			}else{
				cconv = cmd[start];
				start = start + 1;
			}
		}
		
		//ret attrs angegeben?
		if (cmd.length >= start && (cmd[start].trim().equals("zeroext") ||
			cmd[start].trim().equals("signext") ||
			cmd[start].trim().equals("inreg"))){
			reta = cmd[start];
			start = start + 1;
		}
		
		// <result> <ty>
		target = new LLVM_Parameter(cmd[0], cmd[start]);
		start = start + 1;
		/*end = start;
		
		while(cmd.length >= end && (!cmd[end].trim().startsWith("@"))){
			end = end + 1; 
		}
		
		if (start != end){
			for (int i = start; i <= end; i++){
				fnty += cmd[i].trim() + " ";
			}
		}
		start = end;
		*/
		
		for (int i = start; i < cmd.length; i++){
			rest += cmd[i] + " ";
		}
		rest.trim();
		
		System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_out = target.getName() + " = ";
		
		if (tail) cmd_out += "tail ";
		cmd_out += "call ";
		if (cconv != "") cmd_out += cconv + " ";
		if (reta != "") cmd_out += reta + " ";
		
		cmd_out += target.getTypeString() + " ";
		cmd_out += rest;
		
		cmd_out += " " + getComment();
		
		return cmd_out;
	}
}
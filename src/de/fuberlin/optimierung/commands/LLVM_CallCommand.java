package de.fuberlin.optimierung.commands;

import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

/*
* Syntax:
  
  <result> = [tail] call [cconv] [ret attrs] <ty> [<fnty>*] <fnptrval>(<function args>) [fn attrs]
*/

public class LLVM_CallCommand extends LLVM_GenericCommand{
	private boolean tail = false;
	private String cconv = "";
	private String reta = "";
	private String fnty = "";
	private String fnptrval = "";
	private String fnattrs = "";
	
	public LLVM_CallCommand(String cmdLine, ILLVM_Command predecessor, ILLVM_Block block){
		super(predecessor, block, cmdLine);
		setOperation(LLVM_Operation.CALL);
		
		String[] cmd = cmdLine.split(" ");
		// tail?
		if (cmd[2].trim().equals("tail")){
			tail = true;
		}
		
		int start = 3;
		int end = 0;
		start = (tail) ? start + 1 : start;
		
		//cconv angegeben?
		if (cmd.length > start && (cmd[start].trim().equals("ccc") ||
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
		while (cmd.length > start && (cmd[start].trim().compareTo("zeroext") == 0 ||
			cmd[start].trim().compareTo("signext") == 0 ||
			cmd[start].trim().compareTo("inreg") == 0)){
			reta += cmd[start] + " ";
			start = start + 1;
		}
		
		// <result> <ty>
		target = new LLVM_Parameter(cmd[0], cmd[start]);
		start = start + 1;
		end = start;
		
		while(cmd.length > end && (!cmd[end].trim().startsWith("@"))){
			end = end + 1; 
		}
		
		if (start != end){
			for (int i = start; i < end; i++){
				fnty += cmd[i].trim() + " ";
			}
		}
		start = end;
		
		fnptrval = cmd[start].substring(0, cmd[start].indexOf('('));
		
		String rest = cmd[start].substring(cmd[start].indexOf('(') + 1, cmd[start].length());
		for (int i = start+1; i < cmd.length && !cmd[i].contains(")"); i++){
			rest += " " + cmd[i];
			start = i;
		}
		start = start + 1;
		rest += " " + cmd[start].substring(0, cmd[start].indexOf(')'));
		
		String[] str = rest.split(",");
		
		for(String pair : str){
			
			String[] single = pair.trim().split(" ");
			if (single.length == 2){
				operands.add(new LLVM_Parameter(single[1].trim(), single[0].trim()));
			}
			else System.out.println("LLVM_Parameter doesn't match:" + pair);
		}
		
		start = start + 1;
		//fn attrs angegeben?
		while (cmd.length > start && (cmd[start].trim().compareTo("noreturn") == 0 ||
			cmd[start].trim().compareTo("nounwind") == 0 ||
			cmd[start].trim().compareTo("readonly") == 0 ||
			cmd[start].trim().compareTo("readnone") == 0)){
			fnattrs += cmd[start] + " ";
			start = start + 1;
		}
		
		if (LLVM_Optimization.DEBUG) System.out.println("Operation generiert: " + this.toString());
	}
	
	public String toString() {
		String cmd_out = target.getName() + " = ";
		
		if (tail) cmd_out += "tail ";
		cmd_out += "call ";
		if (cconv != "") cmd_out += cconv + " ";
		if (reta != "") cmd_out += reta + " ";
		
		cmd_out += target.getTypeString() + " ";
		
		if (fnty != "") cmd_out += fnty + " ";
		if (fnptrval != "") cmd_out += fnptrval;
		
		if (operands.size() > 0){
			cmd_out += "(";
			for (int i = 0; i < operands.size(); i++){
				cmd_out += operands.get(i).getTypeString() + " " + operands.get(i).getName();
				if (i+1 < operands.size()) cmd_out += ", "; 
			}
			cmd_out += ") ";
		}
		
		if (fnattrs != "") cmd_out += fnattrs + " ";
		
		cmd_out += " " + getComment();
		
		return cmd_out;
	}
}
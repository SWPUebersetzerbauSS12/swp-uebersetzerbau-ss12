package de.fuberlin.optimierung.commands;

import java.util.ArrayList;
import java.util.LinkedList;
import de.fuberlin.optimierung.ILLVM_Block;
import de.fuberlin.optimierung.ILLVM_Command;
import de.fuberlin.optimierung.LLVM_Operation;
import de.fuberlin.optimierung.LLVM_Optimization;
import de.fuberlin.optimierung.LLVM_Parameter;

public abstract class LLVM_GenericCommand implements ILLVM_Command{
	
	public enum parseTypes{
		array, struct, vector, i, f, label
	}
	
	protected ILLVM_Block block;

	protected ILLVM_Command predecessor = null;
	protected ILLVM_Command successor = null;
	
	protected LLVM_Operation operation = null;
	protected LLVM_Parameter target = null;
	protected LinkedList<LLVM_Parameter> operands = new LinkedList<LLVM_Parameter>();;
	
	protected String comment = "";
	protected String command = "";
	
	public LLVM_GenericCommand(ILLVM_Command predecessor, ILLVM_Block block, String cmdLine){
		// Setze die Zeiger
		this.predecessor = predecessor;
		
		String[] com = cmdLine.trim().split(";");
		
		if (com.length > 1){
			for (int i = 1; i < com.length; i++){
				this.comment += com[i]; 
			}
		}
		
		this.command = com[0];
		
		// Setze den zugehoerigen Basisblock
		this.setBlock(block);
		
		if(!this.isFirstCommand()) {
			this.predecessor.setSuccessor(this);		
		}
	}
	
	public LLVM_GenericCommand(){
		
	}
	
	public void deleteCommand(String source) {
		if (LLVM_Optimization.DEBUG) System.out.println("del in block " + this.block.getLabel() + " by " + source + " command " + this.toString());

		if (this.isSingleCommand()){
			this.successor = null;
			this.predecessor = null;
		} else if(this.isFirstCommand()) {	// Loesche erstes Element
			this.successor.setPredecessor(null);
			this.getBlock().setFirstCommand(this.successor);
		} else if(this.isLastCommand()) {	// Loesche letztes Element
			this.predecessor.setSuccessor(null);
			this.getBlock().setLastCommand(this.predecessor);
		} else{
			this.predecessor.setSuccessor(this.successor);
			this.successor.setPredecessor(this.predecessor);
		}
	}
	
	public void replaceCommand(ILLVM_Command c) {
		c.setPredecessor(this.predecessor);
		c.setSuccessor(this.successor);
		if (this.isSingleCommand()){
			
		} else if(this.isFirstCommand()) {	// Loesche erstes Element
			this.successor.setPredecessor(c);
			this.getBlock().setFirstCommand(c);
		} else if(this.isLastCommand()) {	// Loesche letztes Element
			this.predecessor.setSuccessor(c);
			this.getBlock().setLastCommand(c);
		} else{
			this.predecessor.setSuccessor(c);
			this.successor.setPredecessor(c);
		}
	}
	
	public String getComment(){
		if (comment == ""){
			return "\n";
		}else{
			return "; " + comment + "\n";
		}
	}
	
	public String getCommand(){
		if (command == ""){
			return "\n";
		}else{
			return command + "\n";
		}
	}
	
	public static int getComplexStructEnd (String cmdLine){
		int count = 0;
		if (cmdLine.startsWith("[")){
			// Arrayende finden
			for (int i = 0; i < cmdLine.length(); i++){
				String str = cmdLine.substring(i, i+1);
				if (str.contains("[")) count++;
				if (str.contains("]")) count--;
				if (count == 0){
					// Arrayende bei count
					count = i;
					break;
				}
			}
		}
		
		if (cmdLine.startsWith("{")){
			// Structende finden
			for (int i = 0; i < cmdLine.length(); i++){
				String str = cmdLine.substring(i, i+1);
				if (str.contains("{")) count++;
				if (str.contains("}")) count--;
				if (count == 0){
					// Structende bei count
					count = i;
					break;
				}
			}
		}
		return count;
	}
	
	public static LLVM_Parameter readArrayListToLLVM_Parameter(ArrayList<String> input, parseTypes type, boolean opt){
		if (type == parseTypes.array){
			if (!input.get(0).contains("[")) return null;
			else{
				// Arrayende finden
				int count = 0;
				for (int i = 0; i < input.size(); i++){
					String str = input.get(i);
					if (str.contains("[")) count++;
					if (str.contains("]")) count--;
					if (count == 0){
						// Arrayende bei count
						count = i;
						break;
					}
				}
				// Arraylist zu String
				String str = "";
				for (int i = 0; i <= count; i++){
					str += input.get(i) + " ";
				}
				for (int i = 0; i <= count; i++){
					input.remove(0);
				}
				// name, array
				LLVM_Parameter tmp = new LLVM_Parameter(input.get(0), str); 
				input.remove(0);
				return tmp;
			}
		}
		return null;		
	}
	
	public boolean isFirstCommand() {
		return (this.predecessor == null);
	}
	
	public boolean isLastCommand() {
		return (this.successor == null);
	}
	
	public boolean isSingleCommand() {
		return (this.isFirstCommand() && this.isLastCommand());
	}
	
	public ILLVM_Command getPredecessor() {
		return this.predecessor;
	}
	public ILLVM_Command getSuccessor() {
		return this.successor;
	}
	public LinkedList<LLVM_Parameter> getOperands() {
		return operands;
	}
	public LLVM_Operation getOperation() {
		return operation;
	}
	public LLVM_Parameter getTarget() {
		return target;
	}
	public ILLVM_Block getBlock() {
		return block;
	}
	
	public void setPredecessor(ILLVM_Command c) {
		this.predecessor = c;
	}
	public void setSuccessor(ILLVM_Command c) {
		this.successor = c;
	}
	public void setOperation(LLVM_Operation operation) {
		this.operation = operation;
	}
	public void setOperands(LinkedList<LLVM_Parameter> operands) {
		this.operands = operands;
	}
	public void setTarget(LLVM_Parameter target) {
		this.target = target;
	}
	public void setBlock(ILLVM_Block block) {
		this.block = block;
	}
}

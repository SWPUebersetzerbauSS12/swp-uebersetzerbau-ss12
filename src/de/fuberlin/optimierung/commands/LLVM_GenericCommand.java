package de.fuberlin.optimierung.commands;

import java.util.*;
import de.fuberlin.optimierung.*;

public abstract class LLVM_GenericCommand{
	
	public enum parseTypes{
		array, struct, vector, i, f, label
	}
	
	protected LLVM_Block block;

	protected LLVM_GenericCommand predecessor = null;
	protected LLVM_GenericCommand successor = null;
	
	protected LLVM_Operation operation = null;
	protected LLVM_Parameter target = null;
	protected LinkedList<LLVM_Parameter> operands = new LinkedList<LLVM_Parameter>();;
	
	protected String comment = "";
	protected String command = "";
	
	public LLVM_GenericCommand(LLVM_GenericCommand predecessor, LLVM_Block block, String cmdLine){
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
	
	public void replaceCommand(LLVM_GenericCommand c) {
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
	
	protected static String parseReadType (StringBuilder cmd){
		int count = 0;
		String cmdLine = cmd.toString();
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
		
		if (cmdLine.startsWith("(")){
			// Structende finden
			for (int i = 0; i < cmdLine.length(); i++){
				String str = cmdLine.substring(i, i+1);
				if (str.contains("(")) count++;
				if (str.contains(")")) count--;
				if (count == 0){
					// Structende bei count
					count = i;
					break;
				}
			}
		}
		
		// finde Komma, Space, ( nach Type
		int commaindex = cmdLine.indexOf(',', count);
		int spaceindex = cmdLine.indexOf(' ', count);
		int bracketindex = cmdLine.indexOf('(', count);
		int end = 0;
		
		// nehme Minimum
		if (commaindex >= 0 && spaceindex >= 0 && bracketindex >=0){
			if (commaindex < spaceindex && commaindex < bracketindex) end = commaindex;
			if (spaceindex < commaindex && spaceindex < bracketindex) end = spaceindex;
			if (bracketindex < spaceindex && bracketindex < commaindex) end = bracketindex;
		}else if (commaindex >= 0 && spaceindex >= 0){
			end = (commaindex < spaceindex)?commaindex:spaceindex;
		}else if (commaindex >= 0 && bracketindex >= 0){
			end = (commaindex < bracketindex)?commaindex:bracketindex;
		}else if (spaceindex >= 0 && bracketindex >= 0){
			end = (bracketindex < spaceindex)?bracketindex:spaceindex;
		}else if (commaindex >= 0){
			end = commaindex;
		}else if (spaceindex >= 0){
			end = spaceindex;
		}else if (bracketindex >= 0){
			end = bracketindex;
		}
		
		if (end == 0){
			end = cmdLine.length();
		}
		
		// ty einlesen
		String type = cmdLine.substring(0, end).trim();
		cmdLine = cmdLine.substring(type.length()).trim();
		
		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
		return type;
	}
	
	protected static String parseReadResult (StringBuilder cmd){
		String cmdLine = cmd.toString();
		// result einlesen
		String result = cmdLine.substring(0, cmdLine.indexOf("=")).trim();
		cmdLine = cmdLine.substring(cmdLine.indexOf("=")+1).trim();
		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
		return result;
	}
	
	protected static boolean parseEraseString (StringBuilder cmd, String erase){
		boolean erased = false;
		String cmdLine = cmd.toString();
		
		if (cmdLine.indexOf(erase) != -1){
			cmdLine = cmdLine.substring(cmdLine.indexOf(erase)+erase.length()).trim();
			erased = true;
		}
		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
		return erased;
	}
	
	protected static boolean parseOptionalString (StringBuilder cmd, String opt){
		boolean opt_found = false;
		String cmdLine = cmd.toString();
		
		// opt einlesen
		if (cmdLine.startsWith(opt)){
			opt_found = true;
			cmdLine = cmdLine.substring(cmdLine.indexOf(opt) + opt.length()).trim();
		}
		
		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
		return opt_found;
	}
	
	protected static String parseReadPointer (StringBuilder cmd){
		String cmdLine = cmd.toString();
		String pointer = "";
		
		// opt pointer einlesen
		if (cmdLine.contains("* ")){
			pointer = cmdLine.substring(0, cmdLine.indexOf("* ")+2).trim();
			cmdLine = cmdLine.substring(pointer.length()).trim();
		}
		
		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
		return pointer;
	}
	
	protected static String parseOptionalListSingle (StringBuilder cmd, String[] opt){
		String cmdLine = cmd.toString();
		String foundOptString = "";

		// noch opt vorhanden?
		for (String optional : opt){
			if (cmdLine.startsWith(optional)){
				foundOptString = optional;
			}
		}
		// opt einlesen
		if (foundOptString != ""){
			int index = cmdLine.indexOf(" ", foundOptString.length());
			if (index >= 0){
				foundOptString = cmdLine.substring(0, index).trim();
			}else{
				index = cmdLine.length();
				foundOptString = cmdLine.substring(0, index).trim();
			}
			cmdLine = cmdLine.substring(foundOptString.length()).trim();
		}
	
		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
		return foundOptString.trim();
	}
	
	protected static String parseOptionalList (StringBuilder cmd, String[] opt){
		String cmdLine = cmd.toString();
		String finalOptString = "";
		String foundOptString;
		
		do{
			foundOptString = "";
			// noch opt vorhanden?
			for (String optional : opt){
				if (cmdLine.startsWith(optional)){
					foundOptString = optional;
				}
			}
//			// opt einlesen
//			if (foundOptString != ""){
//				finalOptString += cmdLine.substring(0, cmdLine.indexOf(" ", foundOptString.length())).trim() + " ";
//				cmdLine = cmdLine.substring(cmdLine.indexOf(cmdLine.indexOf(" ", foundOptString.length()))).trim();
//			}
			
			// opt einlesen
			if (foundOptString != ""){
				int index = cmdLine.indexOf(" ", foundOptString.length());
				if (index >= 0){
					finalOptString += cmdLine.substring(0, index).trim() + " ";
				}else{
					index = cmdLine.length();
					finalOptString += cmdLine.substring(0, index).trim();
				}
				cmdLine = cmdLine.substring(index).trim();
			}
			
		}while(foundOptString != "");
		
		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
		return finalOptString.trim();
	}
	
	protected static void parseEraseComment (StringBuilder cmd){
		String cmdLine = cmd.toString();
		// Kommentar entfernen
		if (cmdLine.contains(";")) cmdLine = cmdLine.substring(0, cmdLine.indexOf(";"));

		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
	}
	
	protected static String parseStringUntil (StringBuilder cmd, String until){
		String cmdLine = cmd.toString();
		String value = "";
		
		int index = cmdLine.indexOf(until);
		if (index >= 0) {
			value = cmdLine.substring(0, index).trim();
			cmdLine = cmdLine.substring(value.length()).trim();
		}
		
		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
		return value;
	}
	
	protected static String parseReadValue (StringBuilder cmd){
		String cmdLine = cmd.toString();
		String value = "";
		int count = 0;
		
		// falls value ein String 
		if (cmdLine.startsWith("c\"")){
			// Stringende finden
			for (int i = 0; i < cmdLine.length(); i++){
				if (cmdLine.charAt(i) == '{') count++;
				if (cmdLine.charAt(i) == '}') count--;
				if (count == 0){
					// Stringende bei count
					count = i;
					break;
				}
			}
		}
		// finde Komma, Space, ( nach Value
		int commaindex = cmdLine.indexOf(',', count);
		int spaceindex = cmdLine.indexOf(' ', count);
		int bracketindex = cmdLine.indexOf('(', count);
		int end = 0;
		
		// nehme Minimum
		if (commaindex >= 0 && spaceindex >= 0 && bracketindex >=0){
			if (commaindex < spaceindex && commaindex < bracketindex) end = commaindex;
			if (spaceindex < commaindex && spaceindex < bracketindex) end = spaceindex;
			if (bracketindex < spaceindex && bracketindex < commaindex) end = bracketindex;
		}else if (commaindex >= 0 && spaceindex >= 0){
			end = (commaindex < spaceindex)?commaindex:spaceindex;
		}else if (commaindex >= 0 && bracketindex >= 0){
			end = (commaindex < bracketindex)?commaindex:bracketindex;
		}else if (spaceindex >= 0 && bracketindex >= 0){
			end = (bracketindex < spaceindex)?bracketindex:spaceindex;
		}else if (commaindex >= 0){
			end = commaindex;
		}else if (spaceindex >= 0){
			end = spaceindex;
		}else if (bracketindex >= 0){
			end = bracketindex;
		}
		
		if (end == 0){
			end = cmdLine.length();
		}
		value = cmdLine.substring(0, end).trim();
		cmdLine = cmdLine.substring(end).trim();

		cmd.delete(0, cmd.length());
		cmd.append(cmdLine);
		return value;
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
	
	public LLVM_GenericCommand getPredecessor() {
		return this.predecessor;
	}
	public LLVM_GenericCommand getSuccessor() {
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
	public LLVM_Block getBlock() {
		return block;
	}
	
	public void setPredecessor(LLVM_GenericCommand c) {
		this.predecessor = c;
	}
	public void setSuccessor(LLVM_GenericCommand c) {
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
	public void setBlock(LLVM_Block block) {
		this.block = block;
	}
}

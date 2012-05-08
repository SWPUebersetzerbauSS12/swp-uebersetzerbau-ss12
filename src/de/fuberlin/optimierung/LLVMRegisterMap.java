package de.fuberlin.optimierung;

import java.util.HashMap;
import java.util.LinkedList;

public class LLVMRegisterMap {

	private HashMap<String,ILLVMCommand> definitionMap = new HashMap<String,ILLVMCommand>();
	private HashMap<String,LinkedList<ILLVMCommand>> useMap = new HashMap<String,LinkedList<ILLVMCommand>>();
	
	public ILLVMCommand getDefinition(String registerName) {
		return this.definitionMap.get(registerName);
	}
	
	public LinkedList<ILLVMCommand> getUses(String registerName) {
		return this.useMap.get(registerName);
	}
	
	/**
	 * Fuege Register des gegebenen Befehls in Hashmaps ein
	 * @param c Einzufuegender Befehl
	 */
	public void addCommand(ILLVMCommand c) {
		
		// Fuege Definition ein
		LLVMParameter target = c.getTarget();
		if(target!=null && target.getType()==LLVMParameterType.REGISTER)
			this.definitionMap.put(target.getName(), c);
		
		// Fuege Verwendungen ein
		LinkedList<LLVMParameter> operands = c.getOperands();
		LinkedList<ILLVMCommand> uses;
		for(LLVMParameter op : operands) {	// Gehe Operanden durch
			
			if(op.getType()==LLVMParameterType.REGISTER) {	// Ist Operand ein Register?
			
				// Fuege Befehl in useMap ein
				uses = this.getUses(op.getName());
				if(uses==null) {
					uses = new LinkedList<ILLVMCommand>();
				}
				uses.add(c);
				this.useMap.put(op.getName(), uses);
				
			}
		}
	}
	
	/**
	 * Loesche Register des gegebenen Befehls aus Hashmap
	 * @param c Zu loeschender Befehl
	 */
	public void deleteCommand(ILLVMCommand c) {
		
		// Loesche Definition
		LLVMParameter target = c.getTarget();
		if(target!=null && target.getType()==LLVMParameterType.REGISTER) {
			this.definitionMap.remove(target.getName());
		}
		
		// Loesche Verwendungen
		LinkedList<LLVMParameter> operands = c.getOperands();
		LinkedList<ILLVMCommand> uses;
		for(LLVMParameter op : operands) {	// Gehe Operanden durch
			
			if(op.getType()==LLVMParameterType.REGISTER) {	// Ist Operand ein Register?
				
				// Loesche Befehel aus useMap
				uses = this.getUses(op.getName());
				uses.remove(c);
				if(uses.isEmpty()) {
					this.useMap.remove(op.getName());
				}
				else {
					this.useMap.put(op.getName(), uses);
				}
				
			}
	
		}
		
	}
	
	/**
	 * Gibt an, ob zu gegebenem Register eine Definition existiert
	 * @param registerName Name des Registers
	 * @return true, falls Definition existiert, sonst false
	 */
	public boolean existsDefintion(String registerName) {
		return (this.getDefinition(registerName)!=null);
	}
	
	/**
	 * Gibt an, ob zu gegebenem Register mindestens eine Verwendung existiert
	 * @param registerName Name des Registers
	 * @return true, falls eine Verwendung existiert, sonst false
	 */
	public boolean existsUses(String registerName) {
		LinkedList<ILLVMCommand> uses = this.getUses(registerName);
		if(uses==null)
			return false;
		return !(uses.isEmpty());
	}
	
	
	
}

package de.fuberlin.optimierung;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Ermöglicht die Zuordnung von Definition und Verwendungen (als ILLVMCommands) zu einem Register
 * Identifikation laeuft ueber den Namen des Registers als String
 */
public class LLVMRegisterMap {

	// Hashmap, die dem Namen eines Registers den Befehl zuordnet, in dem das Register definiert wird
	private HashMap<String,ILLVMCommand> definitionMap = new HashMap<String,ILLVMCommand>();
	
	// Hashmap, die dem Namen eines Registers die Befehle zuordnet, in dem das Register genutzt wird
	private HashMap<String,LinkedList<ILLVMCommand>> useMap = new HashMap<String,LinkedList<ILLVMCommand>>();
	
	/**
	 * Gibt alle Registernamen aus, die laut Definitionsmap definiert sind
	 * @return Menge der Registernamen
	 */
	public Set<String> getDefinedRegisterNames() {
		return this.definitionMap.keySet();
	}
	
	/**
	 * Gibt die Definition des Registers zurueck
	 * @param registerName Name des Registers
	 * @return Befehl, das Register definiert, null, falls nicht vorhanden
	 */
	public ILLVMCommand getDefinition(String registerName) {
		return this.definitionMap.get(registerName);
	}
	
	/**
	 * Gibt die Verwendungen des Registers zurueck
	 * @param registerName Name des Registers
	 * @return  Befehle, die Register nutzen, null, falls nicht vorhanden
	 */
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

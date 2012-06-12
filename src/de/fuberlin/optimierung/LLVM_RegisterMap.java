package de.fuberlin.optimierung;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Ermöglicht die Zuordnung von Definition und Verwendungen (als ILLVMCommands) zu einem Register
 * Identifikation laeuft ueber den Namen des Registers als String
 * 
 * Doppelte Verwendungen, z.B. a = x+x, werden nur einmal in den Verwendungen (usemap)
 * abgelegt.
 * Loeschbefehle auf nicht vorhandenen Eintraegen machen nichts, stuerzen nicht ab.
 */
public class LLVM_RegisterMap {

	// Hashmap, die dem Namen eines Registers den Befehl zuordnet, in dem das Register definiert wird
	private HashMap<String,ILLVM_Command> definitionMap = new HashMap<String,ILLVM_Command>();
	
	// Hashmap, die dem Namen eines Registers die Befehle zuordnet, in dem das Register genutzt wird
	private HashMap<String,LinkedList<ILLVM_Command>> useMap = new HashMap<String,LinkedList<ILLVM_Command>>();
	
	/**
	 * Gibt alle Registernamen aus, die laut Definitionsmap definiert sind
	 * @return Menge der Registernamen
	 */
	public Set<String> getDefinedRegisterNames() {
		return this.definitionMap.keySet();
	}
	
	/**
	 * Loesche alle Eintraege
	 */
	public void clean() {
		this.definitionMap.clear();
		this.useMap.clear();
	}
	
	/**
	 * Gibt die Definition des Registers zurueck
	 * @param registerName Name des Registers
	 * @return Befehl, das Register definiert, null, falls nicht vorhanden
	 */
	public ILLVM_Command getDefinition(String registerName) {
		return this.definitionMap.get(registerName);
	}
	
	/**
	 * Gibt die Verwendungen des Registers zurueck
	 * @param registerName Name des Registers
	 * @return  Befehle, die Register nutzen, null, falls nicht vorhanden
	 */
	public LinkedList<ILLVM_Command> getUses(String registerName) {
		return this.useMap.get(registerName);
	}
	
	/**
	 * Fuege Register des gegebenen Befehls in Hashmaps ein
	 * @param c Einzufuegender Befehl
	 */
	public void addCommand(ILLVM_Command c) {
		
		// Fuege Definition ein
		LLVM_Parameter target = c.getTarget();
		if(target!=null && target.getType()==LLVM_ParameterType.REGISTER)
			this.definitionMap.put(target.getName(), c);
		
		// Fuege Verwendungen ein
		LinkedList<LLVM_Parameter> operands = c.getOperands();
		
		if(operands!=null) {	// Gibt es Verwendungen zum Einfuegen?
			
			LinkedList<ILLVM_Command> uses;
			for(LLVM_Parameter op : operands) {	// Gehe Operanden durch
				
				if(op.getType()==LLVM_ParameterType.REGISTER) {	// Ist Operand ein Register?
				
					// Fuege Befehl in useMap ein
					uses = this.getUses(op.getName());
					if(uses==null) {
						uses = new LinkedList<ILLVM_Command>();
					}
					// Fuege ein, falls der Befehl noch nicht enthalten ist
					if(!uses.contains(c)) {
						uses.add(c);
					}
					this.useMap.put(op.getName(), uses);
					
				}
			}
			
		}
	}
	
	/**
	 * Loesche Register des gegebenen Befehls aus Hashmap
	 * @param c Zu loeschender Befehl
	 */
	public void deleteCommand(ILLVM_Command c) {
		
		// Loesche Definition
		LLVM_Parameter target = c.getTarget();
		if(target!=null && target.getType()==LLVM_ParameterType.REGISTER) {
			this.definitionMap.remove(target.getName());
		}
		
		// Loesche Verwendungen
		LinkedList<LLVM_Parameter> operands = c.getOperands();
		
		if(operands!=null) {	// Gibt es Verwendungen zum Loeschen?
			
			LinkedList<ILLVM_Command> uses;
			for(LLVM_Parameter op : operands) {	// Gehe Operanden durch
				
				if(op.getType()==LLVM_ParameterType.REGISTER) {	// Ist Operand ein Register?
					
					// Loesche Befehel aus useMap
					uses = this.getUses(op.getName());
					if(!(uses==null)) {
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
		}
		
	}
	
	/**
	 * Loesche gegebenen Operanden aus gegebenem Befehl
	 * @param c Befehl, aus dem geloescht werden soll
	 * @param _target Operand, der geloescht werden soll
	 */
	public void deleteCommand(ILLVM_Command c, LLVM_Parameter _target) {

		// Loesche Definition
		/*LLVM_Parameter target = c.getTarget();
		if(target!=null && target.getType()==LLVM_ParameterType.REGISTER) {
			this.definitionMap.remove(target.getName());
		}*/
		
		// Loesche Verwendungen
		LinkedList<LLVM_Parameter> operands = c.getOperands();
		
		if(operands!=null) {	// Gibt es Verwendungen zum Loeschen?
			
			LinkedList<ILLVM_Command> uses;
			
			for(LLVM_Parameter op : operands){
				
				if(op.getName().equals(_target.getName())){
					// Loesche Befehel aus useMap
					uses = this.getUses(op.getName());
					if(!(uses==null)) {
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
		LinkedList<ILLVM_Command> uses = this.getUses(registerName);
		if(uses==null)
			return false;
		return !(uses.isEmpty());
	}
	
	
	
}

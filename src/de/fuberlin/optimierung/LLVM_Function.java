package de.fuberlin.optimierung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class LLVM_Function {

	String func_define = "";
	
	private ILLVM_Block startBlock;
	private ILLVM_Block endBlock;
	private ArrayList<ILLVM_Block> blocks;
	private int numberBlocks;
	
	private HashMap<String,Integer> labelToBlock = new HashMap<String,Integer>();
	
	private LLVM_RegisterMap registerMap = new LLVM_RegisterMap();
	
	public LLVM_Function(String code) {
		
		String[] firstSplit = code.split("[{}]");
		
		func_define = "define"+firstSplit[0];
		
		String codeBlocks[] = firstSplit[1].split("\n\n");
		this.numberBlocks = codeBlocks.length;
		this.blocks = new ArrayList<ILLVM_Block>(this.numberBlocks);
		for(int i = 0; i < this.numberBlocks; i++) {
			this.blocks.add(new LLVM_Block(codeBlocks[i]));
		}
		this.startBlock = this.blocks.get(0);
		this.endBlock = this.blocks.get(this.numberBlocks-1);
	}
	
	/**
	 * Initialisiert die Hashmap labelToBlock, die Labelnamen dem jeweiligen Block zuordnet
	 * (als Index in this.blocks)
	 */
	private void mapLabelsToBlocks() {
		
		this.labelToBlock.clear();
		
		// Durchlaufe alle Bloecke
		for(int i=0; i<this.numberBlocks; i++) {
			String label = this.blocks.get(i).getLabel();		
			this.labelToBlock.put(label, i);
		}
		
	}
	
	/**
	 * Erstellt den Flussgraphen zwischen den Bloecken, d.h. die Attribute
	 * nextBlocks und previousBlocks der Bloecke werden gesetzt.
	 */
	public void createFlowGraph() {
		
		// Teste, ob Labels gesetzt sind
		// Wenn nein, dann erstelle die Labels
		if(this.numberBlocks>1) {
			if(this.blocks.get(1).getLabel().equals("")) {
				this.createNewLabels();
			}
		}
			
		// Erstelle Label zu Block Mapping
		this.mapLabelsToBlocks();
		
		// Setze Zeiger
		for(ILLVM_Block block : this.blocks) {	// Durchlaufe Bloecke
			
			ILLVM_Command branchCommand = block.getLastCommand();
			LinkedList<LLVM_Parameter> operands = branchCommand.getOperands();
			
			if(branchCommand.getOperation()==LLVM_Operation.RET) {
				// void-return
				// Aktueller Block ist this.endBlock
			}
			else if(branchCommand.getOperation()==LLVM_Operation.RET_CODE) {
				// Return mit Rueckgabewert
				// Aktueller Block ist this.endBlock
				
			}
			else if(branchCommand.getOperation()==LLVM_Operation.BR) {
				// Unbedingter Sprung
				String label = operands.getFirst().getName();
				Integer blockPosition = this.labelToBlock.get(label);
				if(blockPosition!=null) {
					block.appendToNextBlocks(this.blocks.get(blockPosition));
					this.blocks.get(blockPosition).appendToPreviousBlocks(block);
				}
				
			}
			else if(branchCommand.getOperation()==LLVM_Operation.BR_CON) {
				// Bedingter Sprung
				String label1 = operands.get(1).getName();
				String label2 = operands.get(2).getName();
				Integer blockPosition1 = this.labelToBlock.get(label1);
				Integer blockPosition2 = this.labelToBlock.get(label2);
				if(blockPosition1!=null) {
					block.appendToNextBlocks(this.blocks.get(blockPosition1));
					this.blocks.get(blockPosition1).appendToPreviousBlocks(block);
				}
				if(blockPosition2!=null) {
					block.appendToNextBlocks(this.blocks.get(blockPosition2));
					this.blocks.get(blockPosition2).appendToPreviousBlocks(block);
				}
				
			}
				
		}
		
	}
	
	/**
	 * Setze Labels, falls in urspruenglicher Eingabe weder konkrete Labelnamen
	 * noch unbezeichnete Labels in Kommentaren angegeben waren
	 */
	private void createNewLabels() {
		String nextUnnamed = "%1";
		int nextNumber = 1;
		// Erster Block muss nicht betrachtet werden
		for(int i=0; i<this.numberBlocks; i++) {
			ILLVM_Block block = this.blocks.get(i);
			// Setze ab zweitem Block das Label
			if(i>0) {
				block.setLabel(nextUnnamed);
				nextNumber++;
				nextUnnamed = "%" + nextNumber;
			}
			
			if(!block.isEmpty()) {
				ILLVM_Command c = block.getFirstCommand();
				while(c!=null) {
					LLVM_Parameter p = c.getTarget();
					if(p!=null) {
						String name = p.getName();
						if(name!=null && name.equals(nextUnnamed)) {
							nextNumber++;
							nextUnnamed = "%" + nextNumber;
						}
					}
					c = c.getSuccessor();
				}
				
			}	// if block not empty
		}	// for
	}
	
	/**
	 * Definition und Verwendungen der Register werden in registerMap abgelegt
	 * Alte Informationen werden entfernt, aktuelle gesetzt
	 */
	public void createRegisterMaps() {
		
		// Loesche alte Werte
		this.registerMap.clean();
		
		// Setze neue Werte
		for(ILLVM_Block block : this.blocks) {	// Gehe Bloecke durch
			
			// Ist Block leer?
			if(!block.isEmpty()) {
				
				// Gehe Befehle des Blockes durch
				ILLVM_Command c = block.getFirstCommand();
				while(c!=null) {
					
					// Fuege c in Register Maps ein
					this.registerMap.addCommand(c);
					c = c.getSuccessor();
					
				}
				
			}
	
		}
	}
	
	/**
	 * Teste, ob gegebenes Register nicht verwendet wird
	 * Wenn es tot ist, so wird die Definition geloescht
	 * @param registerName Name des Registers
	 * @return geloeschte Definition (um spaeter Operanden zu testen) oder null, falls nichts geloescht wurde
	 */
	private ILLVM_Command eliminateDeadRegister(String registerName) {
		// Teste, ob Verwendungen existieren
		if(!this.registerMap.existsUses(registerName) &&
				this.registerMap.existsDefintion(registerName)) {
			
			// keine Verwendungen, aber eine Definition
			ILLVM_Command c = this.registerMap.getDefinition(registerName);
			this.registerMap.deleteCommand(c);
			c.deleteCommand();
			return c;

		}
		return null;
	}
	
	/**
	 * Register in Operanden der uebergebenen Befehle werden auf spaetere Verwendung getestet
	 * Diese Befehle wurden bereits geloescht
	 * Werden sie nich verwendet, so wird die Definition des Registers geloescht
	 * @param list zu testende Befehle
	 * @return geloeschte Definitionen (um Operanden nochmals testen zu koennen)
	 */
	private LinkedList<ILLVM_Command> eliminateDeadRegistersFromList(LinkedList<ILLVM_Command> list) {
		
		LinkedList<ILLVM_Command> deletedCommands = new LinkedList<ILLVM_Command>();
		
		// Teste, ob Operanden aus list geloescht werden koennen
		for(ILLVM_Command c : list) {
			LinkedList<LLVM_Parameter> operands = c.getOperands();
			if(operands!=null) {
				for(LLVM_Parameter op : operands) {
					if(op.getType()==LLVM_ParameterType.REGISTER) {
						ILLVM_Command del = this.eliminateDeadRegister(op.getName());
						if(del!=null)
							deletedCommands.addFirst(del);
					}
				}
			}
		}
		
		return deletedCommands;
	}
	
	/**
	 * Tote Register werden aus Programm entfernt, d.h. ihre Definitionen werden geloescht
	 * Funktion wird einmal komplett durchgegangen, Abhaengigkeiten werden nicht aufgeloest
	 * Geloeschte Befehle werden zurueckgegeben, um spaeter die Operanden testen zu koennen
	 * Die Register-Maps (Definition und Verwendung) muessen aktuell sein
	 * @return Geloeschte Befehle
	 */
	private LinkedList<ILLVM_Command> eliminateDeadRegistersGlobal() {
		
		LinkedList<ILLVM_Command> deletedCommands = new LinkedList<ILLVM_Command>();
		
		String registerNames[] = this.registerMap.getDefinedRegisterNames().
				toArray(new String[0]);
		
		// Iteriere ueber alle definierten Register
		for(String registerName : registerNames) {
			
			// Teste fuer jedes Register r, ob Verwendungen existieren
			// c ist geloeschter Befehl (Definition) oder null, falls
			// es nich geloescht werden kann
			ILLVM_Command c = this.eliminateDeadRegister(registerName);
			if(c!=null)
				deletedCommands.addFirst(c);
			
		}
		
		return deletedCommands;
		
	}
	
	/**
	 * Tote Register werden aus Programm entfernt, d.h. ihre Definitionen werden geloescht
	 * Abhaengigkeiten von toten Registern werden abgearbeitet
	 * eliminateDeadRegistersGlobal und eliminateDeadRegistersFromList werden als
	 * Unterfunktionen verwendet
	 */
	public void eliminateDeadRegisters() {
		
		LinkedList<ILLVM_Command> deletedCommands;
		deletedCommands = this.eliminateDeadRegistersGlobal();
	
		// Bisher geloeschte Befehle koennen Operanden enthalten, die nun keine Verwendung mehr haben
		// Dann koennen diese ebenfalls geloescht werden
		// Teste also geloeschte Befehle durch, bis nichts mehr geloescht werden kann
		while(!deletedCommands.isEmpty()) {
		
			deletedCommands = this.eliminateDeadRegistersFromList(deletedCommands);
			
		}
		
	}
	
	/**
	 * Entferne tote Bloecke (Bloecke, die nicht erreicht werden koennen)
	 * Diese haben keine Vorgaenger im Flussgraphen (und sind ungleich dem ersten Block)
	 * Geloeschte Befehle werden zurueckgegebens
	 */
	private LinkedList<ILLVM_Command> eliminateDeadBlocksGlobal() {
		
		LinkedList<ILLVM_Command> deletedCommands = new LinkedList<ILLVM_Command>();
		LinkedList<Integer> deletedBlocks = new LinkedList<Integer>();
		
		for(int i=1; i<this.numberBlocks; i++) {
			ILLVM_Block block = this.blocks.get(i);
			if(!block.hasPreviousBlocks()) {
				block.deleteBlock();
				deletedBlocks.add(i);
				
				// Ist Block leer?
				if(!block.isEmpty()) {
					
					// Gehe Befehle des Blockes durch
					ILLVM_Command c = block.getFirstCommand();
					while(c!=null) {
						
						// Fuege c in deletedCommands ein
						deletedCommands.add(c);
						c = c.getSuccessor();
						
					}
					
				}
			}
		}
		
		// Entferne geloeschte Bloecke aus this.blocks
		int numberOfAlreadyDeletedBlocks = 0;
		for(Integer i : deletedBlocks) {
			this.blocks.remove(i-numberOfAlreadyDeletedBlocks);
			numberOfAlreadyDeletedBlocks++;
			this.numberBlocks--;
		}
		
		// Entferne geloeschte Befehle aus Register-Hashmaps
		for(ILLVM_Command c : deletedCommands) {
			this.registerMap.deleteCommand(c);
		}
		
		return deletedCommands;
		
	}
	
	/**
	 * Tote Bloecke werden aus dem Programm entfernt
	 * Operanden der geloeschten Befehle werden auf weitere Verwendungen getestet
	 * und gegebenenfalls ebenfalls geloescht
	 * Abhaengigkeiten zwischen Bloecken werden nicht aufgeloest
	 */
	public void eliminateDeadBlocks() {
		LinkedList<ILLVM_Command> deletedCommands;
		deletedCommands = this.eliminateDeadBlocksGlobal();
	
		// Bisher geloeschte Befehle koennen Operanden enthalten, die nun keine Verwendung mehr haben
		// Dann koennen diese ebenfalls geloescht werden
		// Teste also geloeschte Befehle durch, bis nichts mehr geloescht werden kann
		while(!deletedCommands.isEmpty()) {
		
			deletedCommands = this.eliminateDeadRegistersFromList(deletedCommands);
			
		}
	}
	
	public String toString() {
		String output = func_define + "{\n";
		for (int i = 0; i < this.numberBlocks; i++) {
			output += blocks.get(i).toString();
		}
		output += "}";
		return output;
	}
}

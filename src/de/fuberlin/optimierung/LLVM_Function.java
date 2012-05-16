package de.fuberlin.optimierung;

import java.util.HashMap;
import java.util.LinkedList;

public class LLVM_Function {

	String func_define = "";
	
	private ILLVM_Block startBlock;
	private ILLVM_Block endBlock;
	private ILLVM_Block blocks[];
	private int numberBlocks;
	
	private HashMap<String,Integer> labelToBlock = new HashMap<String,Integer>();
	
	private LLVM_RegisterMap registerMap = new LLVM_RegisterMap();
	
	public LLVM_Function(String code) {
		
		func_define = "define"+code.split("\n")[0];
		String[] firstSplit = code.split("[{}]");
		
		String codeBlocks[] = firstSplit[1].split("\n\n");
		this.numberBlocks = codeBlocks.length;
		this.blocks = new LLVM_Block[this.numberBlocks];
		for(int i = 0; i < this.numberBlocks; i++) {
			this.blocks[i] = new LLVM_Block(codeBlocks[i]);
		}
		this.startBlock = this.blocks[0];
		this.endBlock = this.blocks[this.numberBlocks-1];
	}
	
	/**
	 * Initialisiert die Hashmap labelToBlock, die Labelnamen dem jeweiligen Block zuordnet
	 * (als Index in this.blocks)
	 */
	private void mapLabelsToBlocks() {
		
		// Durchlaufe alle Bloecke
		for(int i=0; i<this.numberBlocks; i++) {
			String label = this.blocks[i].getLabel();
			
			// Zum Testen
			/*if(i==1)
				label = "%4";
			if(i==2)
				label = "%7";
			if(i==3)
				label = "%10";
			if(i==4)
				label = "%13";*/
			
			
			this.labelToBlock.put(label, i);
		}
		
	}
	
	/**
	 * Erstellt den Flussgraphen zwischen den Bloecken, d.h. die Attribute
	 * nextBlocks und previousBlocks der Bloecke werden gesetzt.
	 */
	public void createFlowGraph() {
		
		// falls Labels gesetzt sind:
		
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
					block.appendToNextBlocks(this.blocks[blockPosition]);
					this.blocks[blockPosition].appendToPreviousBlocks(block);
				}
				
			}
			else if(branchCommand.getOperation()==LLVM_Operation.BR_CON) {
				// Bedingter Sprung
				String label1 = operands.get(1).getName();
				String label2 = operands.get(2).getName();
				Integer blockPosition1 = this.labelToBlock.get(label1);
				Integer blockPosition2 = this.labelToBlock.get(label2);
				if(blockPosition1!=null) {
					block.appendToNextBlocks(this.blocks[blockPosition1]);
					this.blocks[blockPosition1].appendToPreviousBlocks(block);
				}
				if(blockPosition2!=null) {
					block.appendToNextBlocks(this.blocks[blockPosition2]);
					this.blocks[blockPosition2].appendToPreviousBlocks(block);
				}
				
			}
				
		}
		
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
		if(!this.registerMap.existsUses(registerName)) {
			
			// Wenn nein, loesche Befehl (Definition)
			ILLVM_Command c = this.registerMap.getDefinition(registerName);
			this.registerMap.deleteCommand(c);
			c.deleteCommand();
			return c;

		}
		return null;
	}
	
	/**
	 * Register in Operanden der uebergebenen Befehle werden auf spaetere Verwendung getestet
	 * Werden sie nich verwendet, so wird die Definition geloescht
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
	 * Die Register-Maps (Definition und Verwendung) muessen aktuell sein
	 * Abhängigkeiten von toten Registern werden abgearbeitet
	 */
	public void eliminateDeadRegistersGlobal() {
		
		LinkedList<ILLVM_Command> deletedCommands = new LinkedList<ILLVM_Command>();
		
		// Iteriere ueber alle definierten Register
		for(String registerName : this.registerMap.getDefinedRegisterNames()) {
			
			// Teste fuer jedes Register r, ob Verwendungen existieren
			// c ist geloeschter Befehl (Definition) oder null, falls
			// es nich geloescht werden kann
			ILLVM_Command c = this.eliminateDeadRegister(registerName);
			if(c!=null)
				deletedCommands.addFirst(c);
			
		}
		
		// Bisher geloeschte Befehle koennen Operanden enthalten, die nun keine Verwendung mehr haben
		// Dann koennen diese ebenfalls geloescht werden
		// Teste also geloeschte Befehle durch, bis nichts mehr geloescht werden kann
		while(!deletedCommands.isEmpty()) {
		
			deletedCommands = this.eliminateDeadRegistersFromList(deletedCommands);
			
		}
		
	}
	
	/**
	 * Entferne tote Bloecke (Bloecke, die nicht erreicht werden koennen)
	 */
	public void eliminateDeadBlocks() {
		
		// Gehe alle Bloecke durch
		
		// Falls Block b existiert ohne Voraengerbloecke (der nicht der
		// erste Block ist), so entferne ihn
		
		// Nachfolgebloecke koennen eventuell nun entfernt werden		
	}
	
	public String toString() {
		String output = func_define + "\n";
		for (int i = 0; i < blocks.length; i++) {
			output += blocks[i].toString();
		}
		output += "}";
		return output;
	}
}

package de.fuberlin.optimierung;

import java.util.LinkedList;

public class LLVM_Function {

	String func_define = "";
	
	private ILLVM_Block startBlock;
	private ILLVM_Block endBlock;
	private ILLVM_Block blocks[];
	private int numberBlocks;
	
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
	
	public void createRegisterMaps() {
		
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
	public LinkedList<ILLVM_Command> eliminateDeadRegistersFromList(LinkedList<ILLVM_Command> list) {
		
		LinkedList<ILLVM_Command> deletedCommands = new LinkedList<ILLVM_Command>();
		
		// Teste, ob Operanden aus list geloescht werden koennen
		for(ILLVM_Command c : list) {
			for(LLVM_Parameter op : c.getOperands()) {
				if(op.getType()==LLVM_ParameterType.REGISTER) {
					ILLVM_Command del = this.eliminateDeadRegister(op.getName());
					if(del!=null)
						deletedCommands.addFirst(del);
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
	
	public String toString() {
		String output = func_define + "\n";
		for (int i = 0; i < blocks.length; i++) {
			output += blocks[i].toString();
		}
		output += "}";
		return output;
	}
}

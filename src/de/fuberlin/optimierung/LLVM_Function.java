package de.fuberlin.optimierung;

public class LLVM_Function {

	String func_define = "";
	
	private ILLVMBlock startBlock;
	private ILLVMBlock endBlock;
	private ILLVMBlock blocks[];
	private int numberBlocks;
	
	private LLVMRegisterMap registerMap = new LLVMRegisterMap();
	
	public LLVM_Function(String code) {
		
		func_define = "define"+code.split("\n")[0];
		String[] firstSplit = code.split("[{}]");
		
		String codeBlocks[] = firstSplit[1].split("\n\n");
		this.numberBlocks = codeBlocks.length;
		this.blocks = new LLVMBlock[this.numberBlocks];
		for(int i = 0; i < this.numberBlocks; i++) {
			this.blocks[i] = new LLVMBlock(codeBlocks[i]);
		}
		this.startBlock = this.blocks[0];
		this.endBlock = this.blocks[this.numberBlocks-1];
	}
	
	public void createRegisterMaps() {
		
		for(ILLVMBlock block : this.blocks) {	// Gehe Bloecke durch
			
			// Ist Block leer?
			if(!block.isEmpty()) {
			
				// Gehe Befehle des Blockes durch
				for(ILLVMCommand c = block.getFirstCommand(); !c.isLastCommand(); c = c.getSuccessor()) {
					
					// Fuege c in Register Maps ein
					this.registerMap.addCommand(c);
				}
				
			}
	
		}
	}
	
	public boolean eliminateDeadRegisters() {
		boolean deleted = false;
		// Iteriere ueber alle definierten Register
		for(String registerName : this.registerMap.getDefinedRegisterNames()) {
			
			// Teste fuer jedes Register r ob Verwendungen existieren
			if(!this.registerMap.existsUses(registerName)) {
				
				// Wenn nein, loesche Befehl (Definition)
				ILLVMCommand c = this.registerMap.getDefinition(registerName);
				this.registerMap.deleteCommand(c);
				c.deleteCommand();
				deleted = true;
			}
			
		}
		return deleted;
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

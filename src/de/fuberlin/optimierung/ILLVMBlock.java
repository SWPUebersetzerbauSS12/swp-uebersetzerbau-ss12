package de.fuberlin.optimierung;

public interface ILLVMBlock {
	
	// Fuer lokale Optimierung des Blockes
	// Parameter/Art der Rueckgabe noch zu ueberdenken
	public void optimizeBlock();
	public void deleteBlock();

	public void setFirstCommand(ILLVMCommand first);
	public void setLastCommand(ILLVMCommand last);
	public ILLVMCommand getFirstCommand();
	public ILLVMCommand getLastCommand();
}

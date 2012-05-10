package de.fuberlin.optimierung;

public interface ILLVM_Block {
	
	// Fuer lokale Optimierung des Blockes
	// Parameter/Art der Rueckgabe noch zu ueberdenken
	public void optimizeBlock();
	public void deleteBlock();

	public void setFirstCommand(ILLVM_Command first);
	public void setLastCommand(ILLVM_Command last);
	public ILLVM_Command getFirstCommand();
	public ILLVM_Command getLastCommand();
	
	public boolean isEmpty();
}

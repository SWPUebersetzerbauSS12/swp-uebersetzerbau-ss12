package de.fuberlin.optimierung;

public interface ILLVM_Block {
	
	// Fuer lokale Optimierung des Blockes
	// Parameter/Art der Rueckgabe noch zu ueberdenken
	public void optimizeBlock();
	public void deleteBlock();
	
	public boolean createInOutLive();

	public void setFirstCommand(ILLVM_Command first);
	public void setLastCommand(ILLVM_Command last);
	public ILLVM_Command getFirstCommand();
	public ILLVM_Command getLastCommand();
	public String getLabel();
	public void setLabel(String label);
	
	public void appendToPreviousBlocks(ILLVM_Block block);
	public void appendToNextBlocks(ILLVM_Block block);
	public void removeFromPreviousBlocks(ILLVM_Block block);
	public void removeFromNextBlocks(ILLVM_Block block);
	
	public boolean isEmpty();
	public boolean hasPreviousBlocks();
}

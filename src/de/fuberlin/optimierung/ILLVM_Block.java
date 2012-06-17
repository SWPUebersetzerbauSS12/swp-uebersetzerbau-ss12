package de.fuberlin.optimierung;

import java.util.LinkedList;

public interface ILLVM_Block {
	
	// Fuer lokale Optimierung des Blockes
	// Parameter/Art der Rueckgabe noch zu ueberdenken
	public void optimizeBlock();
	public void deleteBlock();
	
	public void createDefUseSets();
	public boolean updateInOutLiveVariables();
	public void deleteDeadStores();
	
	public void createGenKillSets();
	public boolean updateInOutReaching();

	public void setFirstCommand(ILLVM_Command first);
	public void setLastCommand(ILLVM_Command last);
	public ILLVM_Command getFirstCommand();
	public ILLVM_Command getLastCommand();
	public String getLabel();
	public void setLabel(String label);
	public LinkedList<String> getInLive();
	public LinkedList<ILLVM_Command> getOutReaching();
	
	public LinkedList<ILLVM_Block> getNextBlocks();
	public LinkedList<ILLVM_Block> getPreviousBlocks();
	
	public void appendToPreviousBlocks(ILLVM_Block block);
	public void appendToNextBlocks(ILLVM_Block block);
	public void removeFromPreviousBlocks(ILLVM_Block block);
	public void removeFromNextBlocks(ILLVM_Block block);
	
	public void removeCommonExpressions ();
	
	public boolean isEmpty();
	public boolean hasPreviousBlocks();
	
	public String toGraph();
}

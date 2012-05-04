package de.fuberlin.optimierung;

interface ILLVMBlock {
	
	// Fuer lokale Optimierung des Blockes
	// Parameter/Art der Rueckgabe noch zu ueberdenken
	public void optimizeBlock();

	public void deleteBlock();

}

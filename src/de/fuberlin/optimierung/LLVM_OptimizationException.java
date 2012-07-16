package de.fuberlin.optimierung;

public class LLVM_OptimizationException extends Exception {
	/**
	 * Exception, bei der die Optimierung abgebrochen wird,
	 * da eine korrekte Optimierung durch einen Fehler verhindert wird
	 */
	private static final long serialVersionUID = 413334314594510088L;
	public LLVM_OptimizationException()
	{	
	}
	
	public LLVM_OptimizationException(String s)
	{
		super(s);	
	}
}

package de.fuberlin.projectci.grammar;

public class BNFParsingErrorException extends Exception {
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -7874352113079904264L;
	
	public BNFParsingErrorException(StackTraceElement[] stackTrace) {
		this.setStackTrace(stackTrace);
		this.printStackTrace();
	}

	public BNFParsingErrorException(String string) {
		super(string);
	}

}

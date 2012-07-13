package de.fuberlin.projectci.grammar;

/**
 * 
 * Spezielle Exception, die beim Einlesen einer BNF-Grammatik-Datei auftreten kann.
 *
 */
public class BNFParsingErrorException extends RuntimeException {
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -7874352113079904264L;

	public BNFParsingErrorException() {
		super();
	}

	public BNFParsingErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public BNFParsingErrorException(String message) {
		super(message);
	}

	public BNFParsingErrorException(Throwable cause) {
		super(cause);
	}
	

}

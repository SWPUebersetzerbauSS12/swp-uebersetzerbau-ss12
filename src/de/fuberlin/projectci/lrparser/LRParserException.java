package de.fuberlin.projectci.lrparser;

/**
 * 
 * Zeigt einen Fehler während des Parsens an. Wird von IParser.parse vereinbart.
 *
 */
public class LRParserException extends RuntimeException{

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 7778258171499091113L;

	public LRParserException() {
		super();
	}
	
	public LRParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public LRParserException(String message) {
		super(message);
	}

	public LRParserException(Throwable cause) {
		super(cause);
	}
}

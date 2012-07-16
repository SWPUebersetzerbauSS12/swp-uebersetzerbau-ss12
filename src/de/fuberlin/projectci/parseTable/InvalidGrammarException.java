package de.fuberlin.projectci.parseTable;

/**
 * Zeigt an, dass eine Grammatik keine gültige (S|LA)LR-Grammatik ist.
 */

@SuppressWarnings("serial")
public class InvalidGrammarException extends Exception{

	public InvalidGrammarException() {
		super();
	}

	public InvalidGrammarException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidGrammarException(String message) {
		super(message);
	}

	public InvalidGrammarException(Throwable cause) {
		super(cause);
	}
}
 

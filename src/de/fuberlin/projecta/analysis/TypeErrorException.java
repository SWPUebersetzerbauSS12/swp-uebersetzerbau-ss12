package de.fuberlin.projecta.analysis;

public class TypeErrorException extends RuntimeException {

	private static final long serialVersionUID = 1950906382044314157L;

	public TypeErrorException() {
	}

	public TypeErrorException(String message) {
		super(message);
	}

	public TypeErrorException(Throwable cause) {
		super(cause);
	}

	public TypeErrorException(String message, Throwable cause) {
		super(message, cause);
	}

}

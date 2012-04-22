package parser;

public class ParserException extends Exception {

	private static final long serialVersionUID = 1976621175796612561L;

	public ParserException() {
		super("An unexpected parser-error occured");
	}

	public ParserException(String message) {
		super(message);
	}

	public ParserException(Throwable cause) {
		super(cause);
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}
}

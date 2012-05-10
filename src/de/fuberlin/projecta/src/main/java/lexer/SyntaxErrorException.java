package lexer;

public class SyntaxErrorException extends RuntimeException {

	private static final long serialVersionUID = 6675624905821545289L;

	public SyntaxErrorException() {
		super("An unexpected syntaxerror happend");
	}

	public SyntaxErrorException(String message) {
		super(message);
	}

	public SyntaxErrorException(Throwable cause) {
		super(cause);
	}

	public SyntaxErrorException(String message, Throwable cause) {
		super(message, cause);
	}

}

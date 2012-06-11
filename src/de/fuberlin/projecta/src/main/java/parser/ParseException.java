package parser;

import lexer.IToken;
import lombok.Getter;

public class ParseException extends RuntimeException {

	@Getter
	private IToken token;
	@Getter
	private String details;

	private static final long serialVersionUID = 1976621175796612561L;

	public ParseException(IToken token) {
		this("An unexpected parser error occured", token);
	}

	public ParseException(String brief, IToken token) {
		this(brief, "(No details)", token);
	}

	public ParseException(String brief, String details, IToken token) {
		super(brief);
		this.details = details;
		this.token = token;
	}

	/**
	 * Convenience method to get line number
	 */
	public int getLineNumber() {
		if (token != null)
			return token.getLineNumber();
		return -1;
	}

	/**
	 * Convenience method to get offset
	 */
	public int getOffset() {
		if (token != null)
			return token.getOffset();
		return -1;
	}

	public String getText() {
		if (token != null)
			return token.getText();
		return "";
	}

}

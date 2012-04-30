package lexer;

import lexer.IToken.TokenType;
import lexer.io.ICharStream;

public class Lexer implements ILexer {

	private int line;

	// TODO: replace delimiterRegexp with real delimiters
	private String delimiterRegexp = "[\\s(=)+-/;\\*]";

	ICharStream is;

	public Lexer(ICharStream is) {
		line = 1;
		this.is = is;
	}

	public Token getNextToken() {
		skipWhiteSpaceAndCommentary();
		if (is.isEmpty()) {
			// End of input stream - nothing more to read
			return new Token(TokenType.EOF, null, this.line, is.getOffset());
		}
		
		String peek = is.getNextChars(1);

		Token t;
		if ((t = reservedAndTerminals()) != null) {
			return t;
		}
		if ((t = identifier()) != null) {
			return t;
		}
		if ((t = stringConstant()) != null) {
			return t;
		}
		if ((t = numConstant()) != null) {
			return t;
		}
		// if no rule could be applied there is something wrong!
		throw new SyntaxErrorException("Undefined something at line " + line
				+ " near " + peek);
	}

	private void skipWhiteSpaceAndCommentary() {
		String peek;
		if(is.isEmpty())
			return;
		do {
			do {
				peek = is.getNextChars(1);
				if (peek.matches("\\n")) {
					this.line += 1;
					is.resetOffset();
				}
				if (peek.matches("\\s"))
					is.consumeChars(1);
			} while (peek.matches("\\s"));
			peek = is.getNextChars(2);
			if (peek.equals("//")) {
				do {
					is.consumeChars(1);
					peek = is.getNextChars(2);
				} while (!peek.endsWith("\n"));
				is.consumeChars(1);
				this.line += 1;
				is.resetOffset();
			} else if (peek.equals("/*")) {
				do {
					is.consumeChars(1);
					peek = is.getNextChars(2);
					if (peek.endsWith("\n")) {
						this.line += 1;
						is.resetOffset();
					}
				} while (!peek.equals("*/"));
				is.consumeChars(2);
			}
			peek = is.getNextChars(2);
		} while (peek.matches("\\s.")
				|| peek.equals("//") || peek.equals("/*"));

	}

	private Token numConstant() throws SyntaxErrorException {
		String peek = is.getNextChars(1);
		String result = "";
		if(!peek.matches("[\\d\\.]")){
			return null;
		}
		while (peek.matches("\\d")) {
			result += peek;
			is.consumeChars(1);
			peek = is.getNextChars(1);
		}
		// We now have read a series of digits
		// if we read a dot or an e/E now we have a real value
		// every other character will indicate that we have an int value
		if (!peek.matches("[eE\\.]")) {
			return new Token(TokenType.INT_LITERAL, "result", line, is.getOffset());
		}
		if (peek.matches("\\.")) {
			result += peek;
			is.consumeChars(1);
			peek = is.getNextChars(1);
			while (peek.matches("\\d")) {
				result += peek;
				is.consumeChars(1);
				peek = is.getNextChars(1);
			}
		}
		if (peek.matches("[eE]")) {
			result += peek;
			is.consumeChars(1);
			peek = is.getNextChars(1);
			if (peek.matches("[+-]")) {
				// optional sign
				result += peek;
				is.consumeChars(1);
				peek = is.getNextChars(1);
			}
			if (peek.matches("\\d")) {
				while (peek.matches("\\d")) {
					result += peek;
					is.consumeChars(1);
					peek = is.getNextChars(1);
				}
			} else {
				throw new SyntaxErrorException(
						"Maleformed real value at line: " + this.line
								+ " near: " + is.getOffset());
			}
			return new Token(TokenType.REAL_LITERAL, result, this.line, is.getOffset());
		}
		return new Token(TokenType.REAL_LITERAL, result, this.line, is.getOffset());
	}

	private Token identifier() throws SyntaxErrorException {
		String peek = is.getNextChars(1);
		String id = "";
		final int offset = is.getOffset();
		if (peek.matches("[A-Za-z]"))
			while (!is.isEmpty() && peek.matches("[A-Za-z0-9_]")) {
				id += peek;
				is.consumeChars(1);
				peek = is.getNextChars(1);
			}
		if (!id.isEmpty())
			return new Token(TokenType.ID, id, this.line, offset);
		return null;

	}

	private Token stringConstant() throws SyntaxErrorException {
		String peek = is.getNextChars(1);
		String delimiter = "'";
		final int offset = is.getOffset();

		switch (peek.charAt(0)) {
		case '\'':
			break;
		case '"':
			delimiter = "\"";
			break;
		default:
			return null;
		}
		is.consumeChars(1);
		String result = "";
		while (true) {
			peek = is.getNextChars(1);
			is.consumeChars(1);
			if (peek.matches("\\s") && peek.charAt(0) != ' ') {
				// throw new
				// SyntaxErrorException("Unallowed whitespace in string in line "
				// + this.lineNumber);
				return null;
			}
			if (peek.startsWith(delimiter)) {
				return new Token(TokenType.STRING_LITERAL, result, this.line, offset);
			}
			result += peek;
			if (is.isEmpty()) {
				return new Token(TokenType.STRING_LITERAL, result, this.line, offset);
			}
		}
	}

	private Token reservedAndTerminals() throws SyntaxErrorException {
		/* if then else while do break return print */
		final int offset = is.getOffset();
		String s = is.getNextChars(1);
		if (s.equals("i")) {
			// TODO: replace delimiterRegexp with real delimiters (there is no
			// '+' after if!)
			if (is.getNextChars(3).matches("if" + delimiterRegexp)) {
				is.consumeChars(2);
				return new Token(TokenType.IF, null, this.line, offset);
			}
			if (is.getNextChars(4).matches("int" + delimiterRegexp)) {
				is.consumeChars(3);
				return new Token(TokenType.INT_TYPE, null, this.line, offset);
			}
		}
		if (s.equals("t")) {
			if (is.getNextChars(5).matches("then" + delimiterRegexp)) {
				is.consumeChars(4);
				return new Token(TokenType.THEN, null, this.line, offset);
			}
			if (is.getNextChars(5).matches("true" + delimiterRegexp)) {
				is.consumeChars(4);
				return new Token(TokenType.BOOL_LITERAL, "true", this.line, offset);
			}
		}
		if (s.equals("e")
				&& is.getNextChars(5).matches("else" + delimiterRegexp)) {
			is.consumeChars(4);
			return new Token(TokenType.ELSE, null, this.line, offset);
		}
		if (s.equals("w")
				&& is.getNextChars(6).matches("while" + delimiterRegexp)) {
			is.consumeChars(5);
			return new Token(TokenType.WHILE, null, this.line, offset);
		}
		if (s.equals("d")) {
			if (is.getNextChars(3).matches("do" + delimiterRegexp)) {
				is.consumeChars(2);
				return new Token(TokenType.DO, null, this.line, offset);
			} else if (is.getNextChars(4).matches("def ")) {
				is.consumeChars(3);
				return new Token(TokenType.DEF, null, this.line, offset);
			}
		}
		if (s.equals("r")) {
			if (is.getNextChars(7).matches("return" + delimiterRegexp)) {
				is.consumeChars(6);
				return new Token(TokenType.RETURN, null, this.line, offset);
			}
			if (is.getNextChars(5).equals("real ")) {
				is.consumeChars(4);
				return new Token(TokenType.REAL_TYPE, null, this.line, offset);
			}
			if (is.getNextChars(7).matches("record" + delimiterRegexp)) {
				is.consumeChars(6);
				return new Token(TokenType.RECORD, null, this.line, offset);
			}
		}
		if (s.equals("b")) {
			if (is.getNextChars(6).matches("break" + delimiterRegexp)) {
				is.consumeChars(5);
				return new Token(TokenType.BREAK, null, this.line, offset);
			}
			if(is.getNextChars(5).matches("bool" + delimiterRegexp)){
				is.consumeChars(4);
				return new Token(TokenType.BOOL_TYPE, null, line, offset);
			}
		}
		if (s.equals("p")
				&& is.getNextChars(6).matches("print" + delimiterRegexp)) {
			is.consumeChars(5);
			return new Token(TokenType.PRINT, null, this.line, offset);
		}
		if (s.equals("f")
				&& is.getNextChars(6).matches("false" + delimiterRegexp)) {
			is.consumeChars(5);
			return new Token(TokenType.BOOL_LITERAL, "false", this.line, offset);
		}
		if (s.equals("+")) {
			is.consumeChars(1);
			return new Token(TokenType.OP_ADD, null, this.line, offset);
		}
		if (s.equals("-")) {
			is.consumeChars(1);
			return new Token(TokenType.OP_MINUS, null, this.line, offset);
		}
		if (s.equals("*")) {
			is.consumeChars(1);
			return new Token(TokenType.OP_MUL, null, this.line, offset);
		}
		if (s.equals("/")) {
			is.consumeChars(1);
			return new Token(TokenType.OP_DIV, null, this.line, offset);
		}
		if (s.equals("&")) {
			if (is.getNextChars(2).equals("&&")) {
				is.consumeChars(2);
				return new Token(TokenType.OP_AND, "AND", this.line, offset);
			}
		}
		if (s.equals("|")) {
			if (is.getNextChars(2).equals("||")) {
				is.consumeChars(2);
				return new Token(TokenType.OP_OR, null, this.line, offset);
			}
		}
		if (s.equals("!")) {
			s = is.getNextChars(2);
			if (s.equals("!=")) {
				is.consumeChars(2);
				return new Token(TokenType.OP_NE, null, this.line, offset);
			} else {
				is.consumeChars(1);
				return new Token(TokenType.OP_NOT, null, this.line, offset);
			}
		}
		if (s.equals("<")) {
			s = is.getNextChars(2);
			if (s.equals("<=")) {
				is.consumeChars(2);
				return new Token(TokenType.OP_LE, null, this.line, offset);
			} else {
				is.consumeChars(1);
				return new Token(TokenType.OP_LT, null, this.line, offset);
			}
		}
		if (s.equals(">")) {
			s = is.getNextChars(2);
			if (s.equals(">=")) {
				is.consumeChars(2);
				return new Token(TokenType.OP_GE, null, this.line, offset);
			} else {
				is.consumeChars(1);
				return new Token(TokenType.OP_GT, null, this.line, offset);
			}
		}
		if (s.equals("=")) {
			s = is.getNextChars(2);
			if (s.equals("==")) {
				is.consumeChars(2);
				return new Token(TokenType.OP_EQ, null, this.line, offset);
			} else {
				is.consumeChars(1);
				return new Token(TokenType.OP_ASSIGN, null, this.line, offset);
			}
		}
		if (s.equals("(")) {
			is.consumeChars(1);
			return new Token(TokenType.LPAREN, null, this.line, offset);
		}
		if (s.equals(")")) {
			is.consumeChars(1);
			return new Token(TokenType.RPAREN, null, this.line, offset);
		}
		if (s.equals("[")) {
			is.consumeChars(1);
			return new Token(TokenType.LBRACKET, null, this.line, offset);
		}
		if (s.equals("]")) {
			is.consumeChars(1);
			return new Token(TokenType.RBRACKET, null, this.line, offset);
		}
		if (s.equals("{")) {
			is.consumeChars(1);
			return new Token(TokenType.LBRACE, null, this.line, offset);
		}
		if (s.equals("}")) {
			is.consumeChars(1);
			return new Token(TokenType.RBRACE, null, this.line, offset);
		}
		if (s.equals(";")) {
			is.consumeChars(1);
			return new Token(TokenType.OP_SEMIC, null, this.line, offset);
		}
		if (s.equals(",")) {
			is.consumeChars(1);
			return new Token(TokenType.OP_COMMA, null, this.line, offset);
		}
		if (s.equals(".")) {
			is.consumeChars(1);
			return new Token(TokenType.OP_DOT, null, this.line, offset);
		}
		return null;
	}
}

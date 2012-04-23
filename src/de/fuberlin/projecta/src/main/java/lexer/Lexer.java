package lexer;

import lexer.IToken.TokenType;
import lexer.io.ICharStream;

public class Lexer implements ILexer {

	private int line;

	private String delimiterRegexp = "[\\s(=)+-/\\*]";

	ICharStream is;

	public Lexer(ICharStream is) {
		line = 1;
		this.is = is;
	}

	public Token getNextToken() {
		String peek;
		do {
			if (is.isEmpty()) {
				// End of input stream - nothing more to read
				return new Token(TokenType.EOF, null, this.line, is.getOffset());
			}
			peek = is.getNextChars(1);
			if (peek.matches("\\n")) {
				this.line += 1;
				is.resetOffset();
			}
			if (peek.matches("\\s"))
				is.consumeChars(1);
		} while (peek.matches("\\s"));
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

	private Token numConstant() throws SyntaxErrorException {
		int state = 0;
		String result = "";
		String peek = is.getNextChars(1);
		if (peek.matches("\\d")) {
			if (state == 0)
				state = 1;
			else {
				state = 8;
			}
			while (new String(peek).matches("\\d")) {
				is.consumeChars(1);
				result += peek;
				peek = is.getNextChars(1);
			}
		}

		if (peek.equals(".")) {
			if (state == 0)
				state = 2;
			else if (state == 1)
				state = 4;
			else
				state = 8;
			is.consumeChars(1);
			result += peek;
			peek = is.getNextChars(1);
			if (peek.matches("\\d")) {
				if (state == 2)
					state = 3;
				else if (state == 4)
					state = 4;
				else
					state = 8;
			}
			while (peek.matches("\\d")) {
				is.consumeChars(1);
				result += peek;
				peek = is.getNextChars(1);
			}
		}
		if (peek.equals("e") || peek.equals("E")) {
			if (state == 3 || state == 4)
				state = 5;
			else
				state = 8;
			is.consumeChars(1);
			result += peek;
			peek = is.getNextChars(1);
			if (peek.equals("+") || peek.equals("-")) {
				if (state == 5)
					state = 6;
				else
					state = 8;
				result += peek;
				is.consumeChars(1);
				peek = is.getNextChars(1);
			}
			if (!(peek.matches("\\d"))) {
				// TODO: throw new
				// SyntaxErrorException("Malformed floating point number");
				state = 8;
			} else {
				if (state == 6)
					state = 7;
				else
					state = 8;
			}
			while (peek.matches("\\d")) {
				is.consumeChars(1);
				result += peek;
				peek = is.getNextChars(1);
			}
		}
		if (state == 1 || state == 3 || state == 4 || state == 7)
			return new Token(TokenType.NUM, result, this.line, is.getOffset());
		else
			throw new SyntaxErrorException("Malformed floating point number");
	}

	private Token identifier() throws SyntaxErrorException {
		String peek = is.getNextChars(1);
		String id = "";
		if (peek.matches("[A-Za-z]"))
			while (!is.isEmpty() && peek.matches("[A-Za-z0-9_]")) {
				id += peek;
				is.consumeChars(1);
				peek = is.getNextChars(1);
			}
		if (!id.isEmpty())
			return new Token(TokenType.ID, id, this.line, is.getOffset());
		return null;

	}

	private Token stringConstant() throws SyntaxErrorException {
		String peek = is.getNextChars(1);
		String delimiter = "'";
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
				return new Token(TokenType.STRING, result, this.line,
						is.getOffset());
			}
			result += new String(peek);
			if (is.isEmpty()) {
				return new Token(TokenType.STRING, result, this.line,
						is.getOffset());
			}
		}
	}

	private Token reservedAndTerminals() throws SyntaxErrorException {
		/* if then else while do break return print */
		String s = is.getNextChars(1);
		if (s.equals("i")) {
			// TODO: replace delimiterRegexp with real delimiters (there is no
			// '+' after if!)
			if (is.getNextChars(3).matches("if" + delimiterRegexp)) {
				is.consumeChars(2);
				return new Token(TokenType.IF, null, this.line, is.getOffset());
			}
			// TODO: replace delimiterRegexp with real delimiters
			if (is.getNextChars(4).matches("int" + delimiterRegexp)) {
				is.consumeChars(3);
				return new Token(TokenType.INT, null, this.line, is.getOffset());
			}
		}
		if (s.equals("t")) {
			s = is.getNextChars(5);
			// TODO: replace delimiterRegexp with real delimiters
			if (s.matches("then" + delimiterRegexp)) {
				is.consumeChars(4);
				return new Token(TokenType.THEN, null, this.line,
						is.getOffset());
			}
		}
		// TODO: replace delimiterRegexp with real delimiters
		if (s.equals("e")
				&& is.getNextChars(5).matches("else" + delimiterRegexp)) {
			is.consumeChars(4);
			return new Token(TokenType.ELSE, null, this.line, is.getOffset());
		}
		// TODO: replace delimiterRegexp with real delimiters
		if (s.equals("w")
				&& is.getNextChars(6).matches("while" + delimiterRegexp)) {
			is.consumeChars(5);
			return new Token(TokenType.WHILE, null, this.line, is.getOffset());
		}
		// TODO: replace delimiterRegexp with real delimiters
		if (s.equals("d")) {
			if (is.getNextChars(3).matches("do" + delimiterRegexp)) {
				is.consumeChars(2);
				return new Token(TokenType.DO, null, this.line, is.getOffset());
			} else if (is.getNextChars(4).matches("def ")) {
				is.consumeChars(3);
				return new Token(TokenType.DEF, null, this.line, is.getOffset());
			}
		}
		// TODO: replace delimiterRegexp with real delimiters
		if (s.equals("r")) {
			if (is.getNextChars(7).matches("return" + delimiterRegexp)) {
				is.consumeChars(6);
				return new Token(TokenType.RETURN, null, this.line,
						is.getOffset());
			}
			if (is.getNextChars(5).equals("real ")) {
				is.consumeChars(4);
				return new Token(TokenType.REAL, null, this.line,
						is.getOffset());
			}
		}
		// TODO: replace delimiterRegexp with real delimiters
		if (s.equals("b")
				&& is.getNextChars(6).matches("break" + delimiterRegexp)) {
			is.consumeChars(5);
			return new Token(TokenType.BREAK, null, this.line, is.getOffset());
		}
		// TODO: replace delimiterRegexp with real delimiters
		if (s.equals("p")
				&& is.getNextChars(6).matches("print" + delimiterRegexp)) {
			is.consumeChars(5);
			return new Token(TokenType.PRINT, null, this.line, is.getOffset());
		}
		if (s.equals("+")) {
			is.consumeChars(1);
			return new Token(TokenType.ARITHOP, "ADD", this.line,
					is.getOffset());
		}
		if (s.equals("-")) {
			is.consumeChars(1);
			return new Token(TokenType.ARITHOP, "SUB", this.line,
					is.getOffset());
		}
		if (s.equals("*")) {
			is.consumeChars(1);
			return new Token(TokenType.ARITHOP, "MUL", this.line,
					is.getOffset());
		}
		if (s.equals("/")) {
			is.consumeChars(1);
			return new Token(TokenType.ARITHOP, "DIV", this.line,
					is.getOffset());
		}
		if (s.equals("&")) {
			if (is.getNextChars(2).equals("&&")) {
				is.consumeChars(2);
				return new Token(TokenType.BOOLOP, "AND", this.line,
						is.getOffset());
			}
		}
		if (s.equals("|")) {
			if (is.getNextChars(2).equals("||")) {
				is.consumeChars(2);
				return new Token(TokenType.BOOLOP, "OR", this.line,
						is.getOffset());
			}
		}
		if (s.equals("!")) {
			s = is.getNextChars(2);
			if (s.equals("!=")) {
				is.consumeChars(2);
				return new Token(TokenType.RELOP, "NE", this.line,
						is.getOffset());
			} else {
				is.consumeChars(1);
				return new Token(TokenType.BOOLOP, "NOT", this.line,
						is.getOffset());
			}
		}
		if (s.equals("<")) {
			s = is.getNextChars(2);
			if (s.equals("<=")) {
				is.consumeChars(2);
				return new Token(TokenType.RELOP, "LE", this.line,
						is.getOffset());
			} else {
				is.consumeChars(1);
				return new Token(TokenType.RELOP, "LT", this.line,
						is.getOffset());
			}
		}
		if (s.equals(">")) {
			s = is.getNextChars(2);
			if (s.equals(">=")) {
				is.consumeChars(2);
				return new Token(TokenType.RELOP, "GE", this.line,
						is.getOffset());
			} else {
				is.consumeChars(1);
				return new Token(TokenType.RELOP, "GT", this.line,
						is.getOffset());
			}
		}
		if (s.equals("=")) {
			s = is.getNextChars(2);
			if (s.equals("==")) {
				is.consumeChars(2);
				return new Token(TokenType.RELOP, "EQ", this.line,
						is.getOffset());
			} else {
				is.consumeChars(1);
				return new Token(TokenType.ASSIGN, null, this.line,
						is.getOffset());
			}
		}
		if (s.equals("(")) {
			is.consumeChars(1);
			return new Token(TokenType.BRL, null, this.line, is.getOffset());
		}
		if (s.equals(")")) {
			is.consumeChars(1);
			return new Token(TokenType.BRR, null, this.line, is.getOffset());
		}
		if (s.equals("[")) {
			is.consumeChars(1);
			return new Token(TokenType.SBRL, null, this.line, is.getOffset());
		}
		if (s.equals("]")) {
			is.consumeChars(1);
			return new Token(TokenType.SBRR, null, this.line, is.getOffset());
		}
		if (s.equals("{")) {
			is.consumeChars(1);
			return new Token(TokenType.CBRL, null, this.line, is.getOffset());
		}
		if (s.equals("}")) {
			is.consumeChars(1);
			return new Token(TokenType.CBRR, null, this.line, is.getOffset());
		}
		if (s.equals(";")) {
			is.consumeChars(1);
			return new Token(TokenType.SEMIC, null, this.line, is.getOffset());
		}
		if (s.equals(",")) {
			is.consumeChars(1);
			return new Token(TokenType.COMMA, null, this.line, is.getOffset());
		}
		if (s.equals(".")) {
			is.consumeChars(1);
			return new Token(TokenType.DOT, null, this.line, is.getOffset());
		}
		return null;
	}
}

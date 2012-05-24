package lexer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
public
@Data
class Token implements IToken {

	/**
	 * Get the real type of this token (used internally only)
	 */
	@Getter
	private TokenType internalType;

	private Object attribute;

	private int lineNumber;
	private int offset;

	@Override
	public String getType() {
		return internalType.toString();
	}

	@Override
	public String toString() {
		return "<" + internalType + ", " + attribute + ", " + lineNumber + ", "
				+ offset + ">";
	}

}

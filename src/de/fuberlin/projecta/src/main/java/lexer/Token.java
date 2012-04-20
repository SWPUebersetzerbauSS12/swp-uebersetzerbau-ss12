package lexer;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public
@Data
class Token implements IToken {

	private TokenType name;

	private String attribute;

	@Override
	public String toString() {
		return "<" + name + ", " + attribute + ">";
	}

}

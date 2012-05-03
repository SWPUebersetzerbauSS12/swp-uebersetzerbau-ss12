package utils;

public class Rule implements IRule {
	private String tokenValue;
	private String tokenType;
	private String regexp;

	public Rule(String tokenType, String tokenValue, String regexp) {
		this.tokenValue = tokenValue;
		this.tokenType = tokenType;
		this.regexp = regexp;
	}

	public String getRegexp() {
		return regexp;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	@Override
	public String toString() {
		return "type: " + tokenType + " value:" + tokenValue + " regexp: "
				+ regexp;
	}
}

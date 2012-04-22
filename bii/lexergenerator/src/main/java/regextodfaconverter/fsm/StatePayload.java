package regextodfaconverter.fsm;

import tokenmatcher.TokenType;


public class StatePayload implements tokenmatcher.StatePayload {

	private TokenType tokenType;
	
	private int backsteps = 0;
	
  
	public StatePayload( TokenType tokenType, int backsteps) {
		super();
		this.tokenType = tokenType;
		this.backsteps = backsteps;
	}
	
	public TokenType getTokenType() {
		return tokenType;
	}

	public int getBacksteps() {
		return backsteps;
	}
	

}

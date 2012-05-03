package de.fuberlin.projectci.test.driver;

import java.util.ArrayList;
import java.util.List;

import de.fuberlin.projectci.extern.ILexer;
import de.fuberlin.projectci.extern.IToken;
import de.fuberlin.projectci.extern.IToken.TokenType;

public class DummyLexer implements ILexer{
	private List<IToken> tokens=new ArrayList<IToken>();
	private int tokenIndex=0;
	
	
	@Override
	public IToken getNextToken() {
		if (tokenIndex<tokens.size()){
			return tokens.get(tokenIndex++);			
		}
		return null;
	}

	void addToken(TokenType tokenType, String attribute){ 
		tokens.add(new DummyToken(tokenType, attribute));
		
	}
	
	static class DummyToken implements IToken{
		private TokenType tokenType;
		private String attribute;
		
		
		public DummyToken(TokenType tokenType, String attribute) {
			super();
			this.tokenType = tokenType;
			this.attribute = attribute;
		}

		@Override
		public TokenType getType() {
			return tokenType;
		}

		@Override
		public String getAttribute() {
			return attribute;
		}

		@Override
		public int getOffset() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getLineNumber() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
	
	
}

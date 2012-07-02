/*
 * 
 * Copyright 2012 lexergen.
 * This file is part of lexergen.
 * 
 * lexergen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * lexergen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with lexergen.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * lexergen:
 * A tool to chunk source code into tokens for further processing in a compiler chain.
 * 
 * Projectgroup: bi, bii
 * 
 * Authors: Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package de.fuberlin.bii.tokenmatcher;

import de.fuberlin.bii.utils.Test;
import de.fuberlin.commons.lexer.IToken;


public class Token implements IToken {
	// TODO: resolve conflict with given interface IToken

	private String type;
	private Object attribute;
	private int line;
	private int offset;

	private static Token eofToken = new Token("EOF", 0, 0);

	public Token(String type, Object attribute, int line, int offset) {
		super();
		this.type = type;
		this.attribute = attribute;
		this.line = line;
		this.offset = offset;
	}

	public Token(String type, int line, int offset) {
		super();
		this.type = type;
		this.line = line;
		this.offset = offset;
	}

	public String getType() {
		return type;
	}

	public <E extends Enum<E>> E tryGetTypeAsEnum(Class<E> enumClass)
			throws IllegalArgumentException, NullPointerException {
		return Enum.valueOf(enumClass, type);
	}

	public Object getAttribute() {
		return attribute;
	}

	public String getAttributeAsString() {
		return attribute.toString();
	}

	public int getOffset() {
		return offset;
	}

	public int getLineNumber() {
		return line;
	}

	public static boolean isTokenLineComment(Token token) {
		return "COMMENT".equalsIgnoreCase(token.getType())
				&& "LINE".equalsIgnoreCase(token.getAttributeAsString());
	}

	public static boolean isTokenStartingBlockComment(Token token) {
		return "COMMENT".equalsIgnoreCase(token.getType())
				&& "BLOCK_BEGIN".equalsIgnoreCase(token.getAttributeAsString());
	}

	public static boolean isTokenEndingBlockComment(Token token) {
		return "COMMENT".equalsIgnoreCase(token.getType())
				&& "BLOCK_END".equalsIgnoreCase(token.getAttributeAsString());
	}

	public static Token getEofToken() {
		return eofToken;
	}

	public static boolean isEofToken(IToken iToken) {
		return Test.isAssigned(iToken) && iToken.getType().equals("EOF");
	}

	public boolean isEofToken() {
		return type.equals("EOF");
	}

	@Override
	public String toString() {
		return "<" + getType() + ", " + getAttribute().toString() + ">";
	}

	@Override
	public String getText() {
		return getAttributeAsString();
	}
}

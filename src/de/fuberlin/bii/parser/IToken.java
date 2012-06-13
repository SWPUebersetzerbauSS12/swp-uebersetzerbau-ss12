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
 * Comment: predefined by parsergroup 
 * 
 * Authors: Kevin Funk (Parsergruppe)
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package de.fuberlin.bii.parser;

public interface IToken {

	
	/**
	 * Get the type of this Token
	 * 
	 * @return Token type
	 */
	String getType();

	
	/**
	 * Get the type of this Token as an enum value
	 * 
	 * @return Token type
	 */
	<E extends Enum<E>> E tryGetTypeAsEnum( Class<E> enumClass) throws IllegalArgumentException, NullPointerException;
	  

	/**
	 * Get the Token attribute value
	 * 
	 * E.g. for a Token of type REAL this can be "0.0"
	 * 
	 * @return Attribute value
	 */
	Object getAttribute();


	/**
	 * Get the start offset of this Token's attribute
	 * 
	 * @note The position is relative to the beginning of the line
	 * 
	 * @return Start offset
	 */
	int getOffset();


	/**
	 * Get the line number of this Token's attribute
	 * 
	 * @return End offset
	 */
	int getLineNumber();
}
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
 * Authors: Johannes Dahlke, yanlei li
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package tokenmatcher;

/**
 * The TokenAttributor yields the corresponding attribute of an token in addiction 
 * to the token type and the readed lexem.
 * 
 * @author Yanlei Li
 *         Johannes Dahlke
 *
 */
public class TokenAttributor {
	
	/**
	 * Converts a lexem depending of the token type from string type to the target type of the token.
	 * e.g. the lexem "123" with token type INT will be convertet to an int value 123.
	 *  
	 * @param lexem the chunked string from the input source 
	 * @param tokenType the identified type
	 * @return an value with common type
	 */
	public Object convertLexemToAttributeForTokenWithType( String lexem, TokenType tokenType) {
		switch ( tokenType) {
			/** handle relational operators <(LT), <=(LE), ==(EQ), !=(NE), >(GT), >=(GE) */
			case OP_LT: 
			case OP_LE: 
			case OP_EQ:
			case OP_NE:
			case OP_GT:
			case OP_GE: 
				return null;
			/** handle logical operators ||(OR), &&(AND), !(NOT) */
			case OP_OR :
			case OP_AND :
			case OP_NOT :
				return null;
		  // etc 
			// ...
			// for all types in TokenType
			// some of them like INT needs a special conversion
			case INT:
				return Integer.valueOf( lexem);
			// but aware: In an INT we can store just as well an lexem in HEX-format like 0xA0F4 or 
			// tip: use Integer.valueOf( lexem, 16);
			// and so on for all defined token types
			default:
			  return null;
		}
	}

}

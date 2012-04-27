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
 * Authors: Kevin Funk (Parsergruppe)
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package tokenmatcher;

/**
 * 
 * @author Vorgegeben durch Parsergruppe
 * 
 * 
 */
public enum TokenType {
	/** relational operators <(LT), <=(LE), ==(EQ), !=(NE), >(GT), >=(GE) */
	OP_LT, OP_LE, OP_EQ, OP_NE, OP_GT, OP_GE,
	/** ||(OR), &&(AND), !(NOT) */
	OP_OR, OP_AND, OP_NOT,
	/** Arithmetic operators +(ADD) -(SUB) *(MUL) /(DIV) -(NEG) */
	OP_ADD, OP_SUB, OP_MUL, OP_DIV, OP_NEG,
	/** Assignment (=) operator */
	OP_ASSIGN,
	/** Comma (,) operator */
	OP_COMMA,
	/** Dot (.) operator */
	OP_DOT,
	/** Semicolon (;) operator */
	OP_SEMIC,
	/** Other reserverd key words */
	IF, THEN, ELSE, WHILE, DO, BREAK, RETURN, PRINT,
	/** Function definition */
	DEF,
	/** Identifier */
	ID,
	/** String constant */
	STRING,
	/** Integer number */
	INT,
	/** Real number */
	REAL,
	/** For array definitions, this marks the field count */
	INDEX,
	/** "(" */
	LPAREN,
	/** ")" */
	RPAREN,
	/** "[" */
	LBRACKET,
	/** "]" */
	RBRACKET,
	/** "{" */
	LBRACE,
	/** "}" */
	RBRACE,
	/** End-of-file marker */
	EOF,
	// next enums only required for lexer 
	/* mark the start ofa block comment */
	BLOCKCOMMENT_BEGIN,
	/* mark the end of a block comment */
	BLOCKCOMMENT_END,
	/* mark the start of a line comment */
	LINECOMMENT_BEGIN
}
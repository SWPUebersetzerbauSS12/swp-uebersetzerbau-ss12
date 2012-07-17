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
 * Authors: Maximilian Schröder, Daniel Rotar, Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package de.fuberlin.bii.lexergen;

/**
 * Stellt einen Fehler dar, der bei der Verwendung einer Lexergeneratorklasse
 * auftreten kann.
 * 
 * @author Daniel Rotar
 * 
 */
public class LexergeneratorException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3639221114631303496L;

	/**
	 * Erstellt ein neues LexergeneratorException Objekt.
	 */
	public LexergeneratorException() {
		super();
	}

	/**
	 * Erstellt ein neues LexergeneratorException Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public LexergeneratorException(String message) {
		super(message);
	}

	public LexergeneratorException(String message, Throwable cause) {
		super(message, cause);
	}

	public LexergeneratorException(Throwable cause) {
		super(cause);
	}
	
	
}

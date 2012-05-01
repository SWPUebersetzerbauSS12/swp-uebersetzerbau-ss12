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
 * Authors: Maximilian Schröder
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package dfaprovider;

/**
 * Exceptionklasse für MinimalDfaProvider
 * 
 * @author Maximilian Schröder
 *
 */
public class MinimalDfaProviderException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6521418734139082883L;
	
	/**
	 * Erstellt ein neues MinimalDfaProviderException Objekt.
	 */
	public MinimalDfaProviderException() {
		super();
	}
	
	/**
	 * Erstellt ein neues MinimalDfaProviderException Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public MinimalDfaProviderException(String message) {
		super(message);
	}
	
}

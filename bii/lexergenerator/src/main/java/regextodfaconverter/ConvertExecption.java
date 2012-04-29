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

package regextodfaconverter;

/**
 * Stellt einen Fehler dar, der beim Konvertieren eines regulären Ausdrucks in
 * einen DFA vorkommt.
 * 
 * @author Johannes Dahlke
 * 
 */
public class ConvertExecption extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2213213933640594695L;

	/**
	 * Erstellt ein neues ConvertExecption Objekt.
	 */
	public ConvertExecption() {
		super();
	}

	/**
	 * Erstellt ein neues ConvertExecption Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public ConvertExecption(String message) {
		super(message);
	}

}

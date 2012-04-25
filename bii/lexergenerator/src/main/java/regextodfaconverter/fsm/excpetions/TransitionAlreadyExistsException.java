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
 * Authors: Daniel Rotar
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package regextodfaconverter.fsm.excpetions;

/**
 * Stellt einen Fehler dar, der auftritt, wenn versucht wird in einem Zustand
 * einen Übergang hinzuzufügen, der bereits vorhanden ist.
 * 
 * @author Daniel Rotar
 * 
 */
public class TransitionAlreadyExistsException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1444077926614652782L;

	/**
	 * Erstellt ein neues TransitionException Objekt.
	 */
	public TransitionAlreadyExistsException() {
		super("Der Übergang existiert bereits!");
	}

}

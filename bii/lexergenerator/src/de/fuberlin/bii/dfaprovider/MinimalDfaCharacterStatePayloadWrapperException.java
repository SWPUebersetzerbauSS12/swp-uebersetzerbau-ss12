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

package dfaprovider;

/**
 * Stellt einen Fehler dar, der auftritt, wenn im
 * MinimalDfaCharacterStatePayloadWrapper ein Fehler vorliegt.
 * 
 * Mögliche Fehlerfälle sind: - Fehler beim Deserialisieren des
 * {@link MinimalDfaCharacterStatePayloadWrapper} - laden aus der übergebenen
 * Datei nicht möglich bzw. fehlerhaft - Cast nach
 * {@link MinimalDfaCharacterStatePayloadWrapper}" schlägt fehl
 * 
 * @author Maximilian Schröder
 * 
 */
public class MinimalDfaCharacterStatePayloadWrapperException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5365197771428767919L;

	/**
	 * Erstellt ein neues MinimalDfaCharacterStatePayloadWrapperException
	 * Objekt.
	 */
	public MinimalDfaCharacterStatePayloadWrapperException() {
		super();
	}

	/**
	 * Erstellt ein neues MinimalDfaCharacterStatePayloadWrapperException
	 * Objekt.
	 * 
	 * @param message
	 *            Die genaue Fehlerbeschreibung.
	 */
	public MinimalDfaCharacterStatePayloadWrapperException(String message) {
		super(message);
	}
}

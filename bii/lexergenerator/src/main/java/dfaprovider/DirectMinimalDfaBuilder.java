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
 * Authors: 
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package dfaprovider;

import java.io.File;

import regextodfaconverter.MinimalDfa;
import regextodfaconverter.fsm.StatePayload;

/**
 * Stellt einen MinimalDFA-Builder dar, der den DFA über den direkten Weg von
 * Regex zu DFA erstellt.
 * 
 * @author ?
 * 
 */
public class DirectMinimalDfaBuilder {

	/**
	 * Erstellt den minimalen DFA für die angegebenen regulären Definitionen und
	 * gibt ihn zurück.
	 * 
	 * @param regularDefinitionFile
	 *            Der absolute Pfad zu der Datei, die die regulären Definitionen
	 *            enthalten.
	 * @return Der minimalen DFA für die angegebenen regulären Definitionen.
	 * @throws MinimalDfaBuilderException
	 *             Wenn ein Fehler beim Erstellen des DFA's auftritt.
	 */
	public MinimalDfa<Character, StatePayload> buildMinimalDfa(
			File regularDefinitionFile) throws MinimalDfaBuilderException {
		if (!regularDefinitionFile.exists()) {
			throw new MinimalDfaBuilderException("Die angegebene Datei '"
					+ regularDefinitionFile.getAbsolutePath()
					+ "'zu den regulären Definitionen exisitiert nicht!");
		}		
		// TODO: buildMinimalDfa implementieren.
		return null;
	}
}

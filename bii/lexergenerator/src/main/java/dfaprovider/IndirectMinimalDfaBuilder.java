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
import java.io.IOException;
import java.util.ArrayList;

import parsetokdef.LexTokDef;
import parsetokdef.LexTokDefTest;
import parsetokdef.ReadTokDefAbstract;
import parsetokdef.ReadTokDefinition;
import parsetokdef.TokenDefinitionException;

import regextodfaconverter.ConvertExecption;
import regextodfaconverter.MinimalDfa;
import regextodfaconverter.RegexToNfaConverter;
import regextodfaconverter.fsm.FiniteStateMachine;
import tokenmatcher.StatePayload;
import utils.IRule;

/**
 * Stellt einen MinimalDFA-Builder dar, der den DFA über den indirekten Weg von
 * Regex zu NFA zu DFA erstellt.
 * 
 * @author Daniel Rotar
 * @author Benjamin Weißenfels
 * @author Alexander Niemeier
 * 
 */
public class IndirectMinimalDfaBuilder implements MinimalDfaBuilder {

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
	 * @throws TokenDefinitionException 
	 */
	public MinimalDfa<Character, StatePayload> buildMinimalDfa(
			File regularDefinitionFile) throws MinimalDfaBuilderException, TokenDefinitionException {
		if (regularDefinitionFile == null) {
			throw new MinimalDfaBuilderException(
					"Der Parameter 'regularDefinitionFile' darf nicht null sein!");
		}
		if (!regularDefinitionFile.exists()) {
			throw new MinimalDfaBuilderException("Die angegebene Datei '"
					+ regularDefinitionFile.getAbsolutePath()
					+ "'zu den regulären Definitionen exisitiert nicht!");
		}

		RegexToNfaConverter<StatePayload> converter = new RegexToNfaConverter<StatePayload>();
		ArrayList<FiniteStateMachine<Character, StatePayload>> fsms = new ArrayList<FiniteStateMachine<Character, StatePayload>>();

		StatePayload payload = null;
		String regex = "";
		FiniteStateMachine<Character, StatePayload> fsm = null;

		ReadTokDefAbstract rtd = null;
		try {
			rtd = new LexTokDef(regularDefinitionFile);
		} catch (IOException e1) {
			// Problem with the File Descriptor
			e1.printStackTrace();
		}
		int counter = 0;
		for (IRule irule : rtd.getRules()) {
			counter++;
			payload = new regextodfaconverter.fsm.StatePayload(
					irule.getTokenType(), irule.getTokenValue(), counter * (-1));
			regex = irule.getRegexp();

			// Aus Regex NFA machen.
			try {
				fsm = converter.convertToNFA(regex, payload);
			} catch (ConvertExecption e) {
				throw new MinimalDfaBuilderException(
						"Der reguläre Ausdruck '"
								+ regex
								+ "' kann nicht in einen Automaten umgewandelt werden: "
								+ e.getMessage());
			}
			fsms.add(fsm);
		}

		// TEMP:
		payload = new regextodfaconverter.fsm.StatePayload("VAR_NAME", "llo",
				-3);
		regex = "llo";

		// Aus Regex NFA machen.
		try {
			fsm = converter.convertToNFA(regex, payload);
		} catch (ConvertExecption e) {
			throw new MinimalDfaBuilderException("Der reguläre Ausdruck '"
					+ regex
					+ "' kann nicht in einen Automaten umgewandelt werden: "
					+ e.getMessage());
		}
		fsms.add(fsm);
		// -----------------------------------------------------------------------------

		// Alle FSMs vereinigen
		if (fsms.size() == 0) {
			throw new MinimalDfaBuilderException(
					"Die angegebene Datei enthält keine gültigen regulären Definitionen!");
		} else if (fsms.size() == 1) {
			fsm = fsms.get(0);
		} else {
			fsm = fsms.get(0);
			for (int i = 1; i < fsms.size(); i++) {
				fsm.union(fsms.get(i));
			}
		}

		MinimalDfa<Character, StatePayload> mDfa = null;
		try {
			mDfa = new MinimalDfa<Character, StatePayload>(fsm);
		} catch (ConvertExecption e) {
			throw new MinimalDfaBuilderException(
					"Fehler beim Erstellen des minimalen DFA's: "
							+ e.getMessage());
		}

		return mDfa;
	}
}

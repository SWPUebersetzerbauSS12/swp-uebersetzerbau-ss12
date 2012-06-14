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

import java.io.File;

import de.fuberlin.bii.bufferedreader.BufferedLexemeReader;
import de.fuberlin.bii.bufferedreader.LexemeReaderException;

import de.fuberlin.bii.dfaprovider.DirectMinimalDfaBuilder;
import de.fuberlin.bii.dfaprovider.IndirectMinimalDfaBuilder;
import de.fuberlin.bii.dfaprovider.MinimalDfaBuilder;
import de.fuberlin.bii.dfaprovider.MinimalDfaProvider;

import de.fuberlin.commons.lexer.*;
import de.fuberlin.bii.regextodfaconverter.MinimalDfa;
import de.fuberlin.bii.tokenmatcher.StatePayload;
import de.fuberlin.bii.tokenmatcher.Tokenizer;
import de.fuberlin.bii.tokenmatcher.errorhandler.ErrorCorrector.CorrectionMode;

/**
 * Stellt einen Lexergenerator (mit Namen "Lexergen") dar.
 * 
 * @author Daniel Rotar
 * 
 */
public class Lexergen implements Lexergenerator {
	/**
	 * Die Datei, die die regulären Definitionen enthält.
	 */
	private File _regularDefinitionFile;

	/**
	 * Die Datei, die das Quellprogramm enthält.
	 */
	private File _sourceProgramFile;

	/**
	 * Der Typ der DFA-Erstellung.
	 */
	private BuilderType _builderType;

	/**
	 * Der Modus der Fehlerbehandlung.
	 */
	private CorrectionMode _errorCorrectionMode;

	/**
	 * Der verwendete Tokenizer;
	 */
	Tokenizer _tokenizer;

	/**
	 * Gibt die Datei, die die regulären Definitionen enthält zurück.
	 * 
	 * @return Die Datei, die die regulären Definitionen enthält.
	 */
	public File getRegularDefinitionFile() {
		return _regularDefinitionFile;
	}

	/**
	 * Gibt die Datei, die das Quellprogramm enthält zurück.
	 * 
	 * @return Die Datei, die das Quellprogramm enthält.
	 */
	public File getSourceProgramFile() {
		return _sourceProgramFile;
	}

	/**
	 * Setzt den Typ der DFA-Erstellung fest.
	 * 
	 * @return Der Typ der DFA-Erstellung.
	 */
	public BuilderType getBuilderType() {
		return _builderType;
	}

	/**
	 * Gibt den Modus der Fehlerbehandlung zurück.
	 * 
	 * @return Der Modus der Fehlerbehandlung.
	 */
	public CorrectionMode getErrorCorrectionMode() {
		return _errorCorrectionMode;
	}

	/**
	 * Erstellt ein neues Lexergen Objekt.
	 * 
	 * @param regularDefinitionFile
	 *            Die Datei, die die regulären Definitionen enthält.
	 * @param sourceProgramFile
	 *            Die Datei, die das Quellprogramm enthält.
	 * @param builderType
	 *            Der Typ der DFA-Erstellung.
	 * @param errorCorrectionMode
	 *            Der Modus der Fehlerbehandlung.
	 * @param forceRebuild
	 *            Erzwingt die Neuerstellung des DFAs.
	 */
	public Lexergen(File regularDefinitionFile, File sourceProgramFile,
			BuilderType builderType, CorrectionMode errorCorrectionMode,
			boolean forceDfaRebuild) throws LexergeneratorException {

		_regularDefinitionFile = regularDefinitionFile;
		_sourceProgramFile = sourceProgramFile;
		_builderType = builderType;
		_errorCorrectionMode = errorCorrectionMode;
		// TODO @Johannes: correctionMode einbauen.

		MinimalDfaBuilder builder;
		if (builderType == BuilderType.indirectBuilder) {
			builder = new IndirectMinimalDfaBuilder();
		} else if (builderType == BuilderType.directBuilder) {
			builder = new DirectMinimalDfaBuilder();
		} else {
			throw new LexergeneratorException(
					"Der BuilderType wird nicht unterstützt!");
		}

		MinimalDfa<Character, StatePayload> mDfa;
		try {
			mDfa = MinimalDfaProvider.getMinimalDfa(regularDefinitionFile,
					builder, forceDfaRebuild);
		} catch (Exception e) {
			throw new LexergeneratorException("Fehler beim Aufbau des DFAs: "
					+ e.getMessage());
		}

		try {
			_tokenizer = new Tokenizer(new BufferedLexemeReader(
					sourceProgramFile.getAbsolutePath()), mDfa);
		} catch (Exception e) {
			throw new LexergeneratorException(
					"Fehler beim Aufbau des Tokenizers: " + e.getMessage());
		}
	}

	/**
	 * Erstellt ein neues Lexergen Objekt.
	 * 
	 * @param regularDefinitionFile
	 *            Die Datei, die die regulären Definitionen enthält.
	 * @param sourceProgramFile
	 *            Die Datei, die das Quellprogramm enthält.
	 * @param builderType
	 *            Der Typ der DFA-Erstellung.
	 * @param errorCorrectionMode
	 *            Der Modus der Fehlerbehandlung.
	 */
	public Lexergen(File regularDefinitionFile, File sourceProgramFile,
			BuilderType builderType, CorrectionMode errorCorrectionMode)
			throws LexergeneratorException {
		this(regularDefinitionFile, sourceProgramFile, builderType,
				CorrectionMode.PANIC_MODE, false);
	}

	/**
	 * Erstellt ein neues Lexergen Objekt.
	 * 
	 * @param regularDefinitionFile
	 *            Die Datei, die die regulären Definitionen enthält.
	 * @param sourceProgramFile
	 *            Die Datei, die das Quellprogramm enthält.
	 * @param builderType
	 *            Der Typ der DFA-Erstellung.
	 */
	public Lexergen(File regularDefinitionFile, File sourceProgramFile,
			BuilderType builderType) throws LexergeneratorException {
		this(regularDefinitionFile, sourceProgramFile, builderType,
				CorrectionMode.PANIC_MODE);
	}

	/**
	 * Erstellt ein neues Lexergen Objekt.
	 * 
	 * @param regularDefinitionFile
	 *            Die Datei, die die regulären Definitionen enthält.
	 * @param sourceProgramFile
	 *            Die Datei, die das Quellprogramm enthält.
	 */
	public Lexergen(File regularDefinitionFile, File sourceProgramFile)
			throws LexergeneratorException {
		this(regularDefinitionFile, sourceProgramFile,
				BuilderType.indirectBuilder);
	}

	/**
	 * Gibt das nächste Token zurück.
	 * 
	 * @return Das nächste Token.
	 */
	public IToken getNextToken() throws LexergeneratorException {
		try {
			return _tokenizer.getNextToken();
		} catch (Exception e) {
			throw new LexergeneratorException(
					"Fehler beim Lesen des nächsten Tokens: " + e.getMessage());
		}
	}

	/**
	 * Setzt die Position im Quellprogramm auf die Startposition zurück.
	 */
	public void reset() throws LexergeneratorException {
		try {
			_tokenizer.reset();
		} catch (LexemeReaderException e) {
			throw new LexergeneratorException("Fehler beim Zurücksetzen: "
					+ e.getMessage());
		}
	}

}

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

import de.fuberlin.commons.lexer.*;

/**
 * Stellt die Schnittstelle eines Lexergenerators dar.
 * 
 * @author Daniel Rotar
 * 
 */
public interface Lexergenerator {

	/**
	 * Gibt die Datei, die die regulären Definitionen enthält zurück.
	 * 
	 * @return Die Datei, die die regulären Definitionen enthält.
	 */
	File getRegularDefinitionFile();

	/**
	 * Gibt die Datei, die das Quellprogramm enthält zurück.
	 * 
	 * @return Die Datei, die das Quellprogramm enthält.
	 */
	File getSourceProgramFile();

	/**
	 * Gibt das nächste Token zurück.
	 * 
	 * @return Das nächste Token.
	 */
	IToken getNextToken() throws LexergeneratorException;

	/**
	 * Setzt die Position im Quellprogramm auf die Startposition zurück.
	 */
	void reset() throws LexergeneratorException;

}

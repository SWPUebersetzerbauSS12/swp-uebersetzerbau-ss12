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
 * Authors: Benjamin Weißenfels 
 *            
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package de.fuberlin.bii.parsetokdef;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import de.fuberlin.bii.utils.IRule;
import de.fuberlin.bii.utils.Rule;

/**
 * 
 * @author Benjamin Weißenfels
 * @deprecated
 */
public class ReadTokDefinition extends ReadTokDefAbstract {

	/**
	 * reads a token defintion file. If file is null we take the test token
	 * definition file definition is taken.
	 * 
	 * @param file
	 * @throws IOException
	 * @throws TokenDefinitionException
	 */

	public ReadTokDefinition(File file) throws IOException,
			TokenDefinitionException {
		super(file);
	}

	/**
	 * reads a token defintion file. If the path is null, the default token
	 * definition is taken.
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws TokenDefinitionException
	 */
	public void readFile(File file) throws FileNotFoundException,
			TokenDefinitionException {
		Scanner s = new Scanner(file);

		// new delimeter for getting the tokens
		s.useDelimiter("(\\n+)|(\\t+\\{)");

		readDefinition(s);
		readRules(s);
	}

	protected void readDefinition(Scanner s) throws TokenDefinitionException {

		HashMap<String, Boolean> seenPattern = new HashMap<String, Boolean>();
		definitions = (definitions == null) ? new HashMap<String, String>()
				: definitions;

		while (s.hasNextLine()) {
			String pattern = s.next();
			String name = null;
			line++;

			// check, if line is empty
			if (pattern.matches("\\t+|\\s+|\n")) {
				continue;
			}

			// check, if the end of definition is reached
			if (pattern.equals("%%")) {
				return;
			}

			name = s.next().replace("{", "").replace("}", "");

			// check, if this pattern was already read
			if (seenPattern.containsKey(pattern)) {
				continue;
			}

			// do not allow digits in declarations
			if (name.matches("([0-9][0-9]*,[0-9][0-9]*)|([0-9][0-9]*)|([0-9][0-9]*,)")) {
				throw new TokenDefinitionException(line,
						"Number are not allowed in declarations");
			}

			// try to make a definition entry
			if (!definitions.containsKey(name)) {
				pattern = replaceDef(pattern);
				definitions.put(name, pattern);
			}
		}
	}

	protected void readRules(Scanner s) throws TokenDefinitionException {

		rules = (rules == null) ? new ArrayList<IRule>() : rules;

		while (s.hasNextLine()) {

			String pattern = null;
			String action = null;

			line++;

			if (s.hasNext())
				pattern = s.next();
			else
				throw new TokenDefinitionException(line, "missing pattern");

			if (s.hasNext())
				action = s.next().replace("}", "");
			else
				throw new TokenDefinitionException(line, "missing rule");

			pattern = replaceDef(pattern);
			IRule tpl = new Rule(getTokenType(action), getTokenValue(action),
					pattern);
			rules.add(tpl);
		}
	}

}

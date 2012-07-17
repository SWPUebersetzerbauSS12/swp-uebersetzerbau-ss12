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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.fuberlin.bii.utils.IRule;
import de.fuberlin.bii.utils.Rule;

/**
 * Liest zeichenweise eine Tokendefinitionsdatei ein. Weitere Information:
 * {@linkplain https
 * ://github.com/PascalCase/swp-uebersetzerbau-ss12/wiki/Lexergenerator}
 * 
 * @author Benjamin Weißenfels
 * 
 */
public class LexTokDef extends ReadTokDefAbstract {

	private HashMap<String, Boolean> seenPattern;

	public LexTokDef(File regularDefinitionFile) throws IOException,
			TokenDefinitionException {
		super(regularDefinitionFile);
	}

	@Override
	public void readFile(File file) throws TokenDefinitionException,
			IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		String pattern = null;
		seenPattern = new HashMap<String, Boolean>();
		definitions = new HashMap<String, String>();
		rules = new ArrayList<IRule>();
		int r = 0;
		int col = 0;
		line = 1;

		// read declarations character wise
		while ((r = br.read()) != -1) {

			if ("%%".equals(sb.toString())) {
				// delete the input
				sb = new StringBuilder();
				line++;

				// read the rules
				break;
			}

			col++;
			if (r == '\t' && sb.length() > 0) {

				pattern = sb.toString();

				if (!seenPattern.containsKey(pattern)) {
					seenPattern.put(pattern, true);
					sb = new StringBuilder();
					continue;
				}

				// this pattern was already, which makes no sense
				throw new TokenDefinitionException(line, col,
						"double pattern found");
			}

			// read the declaration identifier
			if (r == '\n' && sb.length() > 0) {

				String name = sb.toString().replace("{", "").replace("}", "");

				if (pattern == null)
					throw new TokenDefinitionException(line, col,
							"no pattern defined");

				// do not allow digits in declarations
				if (name.matches("([0-9][0-9]*,[0-9][0-9]*)|([0-9][0-9]*)|([0-9][0-9]*,)")) {
					throw new TokenDefinitionException(line, col,
							"Number are not allowed in declarations");
				}

				// try to make a definition entry
				if (!definitions.containsKey(name)) {
					pattern = replaceDef(pattern);
					definitions.put(name, pattern);
					line++;
					col = 0;
					pattern = null;
					sb = new StringBuilder();
					continue;
				}

				// do not use a name for declaration twice
				throw new TokenDefinitionException(line, col,
						"this declaration name already exists");
			}

			// match some errors
			if (r == '\n' && pattern != null) {
				throw new TokenDefinitionException(line, col,
						"missing declaration");
			}

			if (r == '\n') {
				line++;
				col = 0;
				continue;
			}

			if (r == '\t') {
				continue;
			}

			sb.append((char) r);
		}

		// read rules
		while ((r = br.read()) != -1) {

			col++;
			if (r == '\t' && sb.length() > 0) {

				pattern = sb.toString();

				if (!seenPattern.containsKey(pattern)) {
					pattern = replaceDef(pattern);
					sb = new StringBuilder();
					continue;
				}

				throw new TokenDefinitionException(line, "double pattern found");
			}

			if (r == '\n' && sb.length() > 0) {

				if (pattern == null)
					throw new TokenDefinitionException(line, col,
							"no pattern defined");

				// add the new rule
				String action = sb.substring(1, sb.length() - 1);
				IRule rule = new Rule(getTokenType(action),
						getTokenValue(action), pattern);
				rules.add(rule);

				// set back some control variables
				line++;
				col = 0;
				sb = new StringBuilder();
				pattern = null;
				continue;
			}

			// match some errors
			if (r == '\n' && pattern != null) {
				throw new TokenDefinitionException(line, col, "missing rule");
			}

			if (r == '\n') {
				line++;
				col = 0;
				continue;
			}

			if (r == '\t') {
				col++;
				continue;
			}

			sb.append((char) r);
		}

		// read last line
		if (r == -1 && sb.length() > 0) {

			if (pattern == null)
				throw new TokenDefinitionException(line, col,
						"no pattern defined");

			// add the new rule
			String action = sb.substring(1, sb.length() - 1);
			IRule rule = new Rule(getTokenType(action), getTokenValue(action),
					pattern);
			rules.add(rule);

			// set back some control variables
			line++;
			col = 0;
		}
	}
}

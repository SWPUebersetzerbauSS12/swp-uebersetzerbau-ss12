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

package parsetokdef;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

import utils.IRule;
import utils.Rule;
import lexergen.Settings;

/**
 * 
 * @author Benjamin Weißenfels
 */
public class ReadTokDefinition {

	private List<IRule> rules;
	private HashMap<String, String> definitions;
	private int line = 0;

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
		if (file != null)
			readFile(file.getCanonicalPath());
		else
			readFile();
	}

	/**
	 * reads a token defintion file which should be defined in token settings
	 * definition is taken.
	 * 
	 * @throws FileNotFoundException
	 * @throws TokenDefinitionException
	 */
	public ReadTokDefinition() throws FileNotFoundException,
			TokenDefinitionException {
		readFile(null);
	}

	/**
	 * reads a token defintion file. If the path is null, the default token
	 * definition is taken.
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws TokenDefinitionException
	 */
	public ReadTokDefinition(String path) throws FileNotFoundException,
			TokenDefinitionException {
		readFile(path);
	}

	/**
	 * reads the default token definition, which is located in
	 * ./src/main/resources/def/tokendefinition
	 * 
	 * @throws FileNotFoundException
	 * @throws TokenDefinitionException
	 */
	public void readFile() throws FileNotFoundException,
			TokenDefinitionException {
		readFile(null);
	}

	/**
	 * reads a token defintion file. If the path is null, the default token
	 * definition is taken.
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws TokenDefinitionException
	 */
	public void readFile(String path) throws FileNotFoundException,
			TokenDefinitionException {

		path = (path == null) ? Settings.getDefaultTokenDef() : path;
		Scanner s = new Scanner(new File(path));

		// new delimeter for getting the tokens
		s.useDelimiter("(\\n+)|(\\t+\\{)");

		readDefinition(s);
		readRules(s);
	}

	/**
	 * Returns all rules.
	 * 
	 * @return is empty, if you do not execute read() method
	 */
	public List<IRule> getRules() {
		return rules;
	}

	private void readDefinition(Scanner s) throws TokenDefinitionException {

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

	private void readRules(Scanner s) throws TokenDefinitionException {

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

	/**
	 * Replaces the definitions in the pattern.
	 * 
	 * @param pattern
	 * @return returns something, which is only including regular expressions
	 */
	private String replaceDef(String pattern) {

		Stack<String> stack = new Stack<String>();

		int i = 0;
		while (i < pattern.length()) {
			if ('"' == pattern.charAt(i)) {
				while (i < pattern.length() && '"' != pattern.charAt(i)) {
					i++;
				}
			}

			if (i < pattern.length() && '{' == pattern.charAt(i)) {
				i++;
				String def = new String();

				while (i < pattern.length() && '}' != pattern.charAt(i)) {
					def = def + pattern.charAt(i);
					i++;
				}
				stack.push(def);
			}

			i++;
		}

		for (String def : stack) {

			if (!definitions.containsKey(def)) {
				continue;
			}

			String tmpPattern = definitions.get(def);
			pattern = pattern.replace("{" + def + "}", tmpPattern);
		}

		return pattern;
	}

	private String getTokenType(String action) {

		String tokenAttributes[] = action.split("\"");

		if (tokenAttributes.length > 1)
			return tokenAttributes[1];
		else
			return null;
	}

	private String getTokenValue(String action) {

		String tokenAttributes[] = action.split("\"");

		if (tokenAttributes.length > 3) {
			return tokenAttributes[3];
		}
		return null;
	}
}

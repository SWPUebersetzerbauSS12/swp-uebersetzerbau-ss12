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
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import de.fuberlin.bii.tokenmatcher.attributes.Attribute;
import de.fuberlin.bii.tokenmatcher.attributes.ParseBoolAttribute;
import de.fuberlin.bii.tokenmatcher.attributes.ParseDoubleAttribute;
import de.fuberlin.bii.tokenmatcher.attributes.ParseIntAttribute;
import de.fuberlin.bii.tokenmatcher.attributes.ParseStringAttribute;
import de.fuberlin.bii.tokenmatcher.attributes.StringAttribute;
import de.fuberlin.bii.utils.IRule;

public abstract class ReadTokDefAbstract {
	protected List<IRule> rules;
	protected HashMap<String, String> definitions;
	protected int line = 0;

	/**
	 * Liest eine Tokendefintionsdatei.
	 */
	public ReadTokDefAbstract(File file) throws IOException,
			TokenDefinitionException {
		readFile(file);
	}

	abstract public void readFile(File file) throws FileNotFoundException,
			TokenDefinitionException, IOException;

	/**
	 * Returniert alle gelesenen Regeln
	 * 
	 * @return wenn die Liste leer ist, ist wahrscheinlich nicht
	 *         {@link ReadTokDefAbstract#readFile(File)} ausgefuehrt worden
	 */
	public List<IRule> getRules() {
		return rules;
	}

	/**
	 * Ersetzt die Definitionen im linken Musterteil mit regulaeren Ausdruecken
	 * 
	 * @param pattern
	 * @return returns Gibt einen String zurueck, der nur noch regulaere
	 *         Ausdruecke enthaelt
	 */
	protected String replaceDef(String pattern) {

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

	protected String getTokenType(String action) {

		String tokenAttributes[] = action.split("\"");

		if (tokenAttributes.length > 1)
			return tokenAttributes[1];
		else
			return null;
	}

	protected Attribute getTokenValue(String action) {

		String tokenAttributes[] = action.split("\"");

		// if it containes more then three elements there must be more than two
		// quotes, that's why we do not have to parse the type
		if (tokenAttributes.length > 3) {
			return new StringAttribute(tokenAttributes[3]);
		}

		// if there are only three elements, there could be a function call like
		// parseInt() as value defined
		if (tokenAttributes.length == 3) {

			if (tokenAttributes[2].matches(",.*parseInt\\(\\s*.*")) {
				return new ParseIntAttribute();
			}

			if (tokenAttributes[2].matches(",.*parseDouble\\(\\s*.*")) {
				return new ParseDoubleAttribute();
			}

			if (tokenAttributes[2].matches(",.*parseString\\(\\s*.*")) {
				return new ParseStringAttribute();
			}

			if (tokenAttributes[2].matches(",.*parseBoolean\\(\\s*.*")) {
				return new ParseBoolAttribute();
			}
		}

		// something went totally wrong
		return null;
	}
}

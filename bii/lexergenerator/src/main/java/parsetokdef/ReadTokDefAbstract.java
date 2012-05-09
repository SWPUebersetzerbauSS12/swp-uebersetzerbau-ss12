package parsetokdef;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import lexergen.Settings;
import utils.IRule;
import utils.Rule;

public abstract class ReadTokDefAbstract {
	protected List<IRule> rules;
	protected HashMap<String, String> definitions;
	protected int line = 0;

	/**
	 * reads a token defintion file. If file is null we take the test token
	 * definition file definition is taken.
	 * 
	 * @param file
	 * @throws IOException
	 * @throws TokenDefinitionException
	 */

	public ReadTokDefAbstract(File file) throws IOException,
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
	public ReadTokDefAbstract() throws FileNotFoundException,
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
	public ReadTokDefAbstract(String path) throws FileNotFoundException,
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
	abstract public void readFile() throws FileNotFoundException,
			TokenDefinitionException;

	/**
	 * reads a token defintion file. If the path is null, the default token
	 * definition is taken.
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws TokenDefinitionException
	 */
	abstract public void readFile(String path) throws FileNotFoundException,
			TokenDefinitionException;

	/**
	 * Returns all rules.
	 * 
	 * @return is empty, if you do not execute read() method
	 */
	public List<IRule> getRules() {
		return rules;
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

	/**
	 * Replaces the definitions in the pattern.
	 * 
	 * @param pattern
	 * @return returns something, which is only including regular expressions
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

	protected String getTokenValue(String action) {

		String tokenAttributes[] = action.split("\"");

		if (tokenAttributes.length > 3) {
			return tokenAttributes[3];
		}
		return null;
	}
}

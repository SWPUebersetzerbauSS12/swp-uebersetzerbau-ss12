package parsetokdef;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

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

	/**
	 * reads a token defintion file. If the path is null, the default token
	 * definition is taken.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public ReadTokDefinition(File file) throws IOException {
		readFile(file.getCanonicalPath());
	}

	/**
	 * reads a token defintion file. If the path is null, the default token
	 * definition is taken.
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public ReadTokDefinition(String path) throws FileNotFoundException {
		readFile();
	}

	/**
	 * reads the default token definition, which is located in
	 * ./src/main/resources/def/tokendefinition
	 * 
	 * @throws FileNotFoundException
	 */
	public void readFile() throws FileNotFoundException {
		readFile(null);
	}

	/**
	 * reads a token defintion file. If the path is null, the default token
	 * definition is taken.
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 */
	public void readFile(String path) throws FileNotFoundException {

		path = (path == null) ? Settings.getDefaultTokenDef() : path;
		Scanner s = new Scanner(new File(path));

		// new delimeter for getting the tokens
		s.useDelimiter("(\\n+)|(\\s+\\{)");

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

	private void readDefinition(Scanner s) {

		HashMap<String, Boolean> seenPattern = new HashMap<String, Boolean>();
		definitions = (definitions == null) ? new HashMap<String, String>()
				: definitions;

		while (s.hasNextLine()) {
			String pattern = s.next();
			String name = null;

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

			// try to make a definition entry
			if (!definitions.containsKey(name)) {
				pattern = replaceDef(pattern);
				definitions.put(name, pattern);
			}
		}
	}

	private void readRules(Scanner s) {

		rules = (rules == null) ? new ArrayList<IRule>() : rules;

		while (s.hasNextLine()) {

			String pattern = null;
			String action = null;

			if (s.hasNext())
				pattern = s.next();
			else
				break;

			if (s.hasNext())
				action = s.next().replace("}", "");
			else
				break;

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

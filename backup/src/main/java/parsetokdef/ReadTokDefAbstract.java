package parsetokdef;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import utils.IRule;

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
	 * @throws TokenDefinitionException
	 * @throws IOException 
	 */
	public ReadTokDefAbstract() throws TokenDefinitionException, IOException {
		readFile(null);
	}

	/**
	 * reads a token defintion file. If the path is null, the default token
	 * definition is taken.
	 * 
	 * @param path
	 * @throws TokenDefinitionException
	 * @throws IOException 
	 */
	public ReadTokDefAbstract(String path) throws TokenDefinitionException, IOException {
		readFile(path);
	}

	/**
	 * reads the default token definition, which is located in
	 * ./src/main/resources/def/tokendefinition
	 * 
	 * @throws FileNotFoundException
	 * @throws TokenDefinitionException
	 * @throws IOException 
	 */
	abstract public void readFile() throws FileNotFoundException,
			TokenDefinitionException, IOException;

	/**
	 * reads a token defintion file. If the path is null, the default token
	 * definition is taken.
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws TokenDefinitionException
	 * @throws IOException 
	 */
	abstract public void readFile(String path) throws FileNotFoundException,
			TokenDefinitionException, IOException;

	/**
	 * Returns all rules.
	 * 
	 * @return is empty, if you do not execute read() method
	 */
	public List<IRule> getRules() {
		return rules;
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

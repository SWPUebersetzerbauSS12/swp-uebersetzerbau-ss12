package parsetokdef;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import lexergen.helper.Helper;
import lexergen.helper.Line;

/**
 * 
 * @author benjamin
 */
public class ReadTokDefinition {

	private List<Line> rules;
	private HashMap<String, String> definitions;

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

		path = (path == null) ? Helper.getDefaultTokenDef() : path;
		Scanner s = new Scanner(new File(path));

		// new delimeter for getting the tokens
		s.useDelimiter("(\\n+)|(\\s+\\{)");

		readDefinition(s);
		readRules(s);
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

			// check, if the end of defenition is reached
			if (pattern.equals("%%"))
				return;

			name = s.next().replace("{", "").replace("}", "");

			// check, if this pattern was already read
			if (seenPattern.containsKey(pattern))
				continue;

			// try to make a definition entry
			if (!definitions.containsKey(name)) {
				pattern = replaceDef(pattern);
				definitions.put(name, pattern);
			}
		}
	}

	private void readRules(Scanner s) {

		rules = (rules == null) ? new ArrayList<Line>() : rules;

		while (s.hasNextLine()) {
			String pattern = s.next();
			String action = s.next().replace("}", "");
			pattern = replaceDef(pattern);
			Line tpl = new Line(pattern, action);
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

		if (!pattern.matches("\".*\"") && pattern.matches(".*\\{.*\\}.*")) {

			String[] defs = pattern.split("(.*\\{)|(\\}.*)");

			for (int i = 0; i < defs.length; i++) {

				if (!definitions.containsKey(defs[i]))
					continue;

				String tmpPattern = definitions.get(defs[i]);
				return pattern.replace("{" + defs[i] + "}", tmpPattern);
			}
		}

		return pattern;
	}

}

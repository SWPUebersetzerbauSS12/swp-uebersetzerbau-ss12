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
		
		readDefinition(s);		
		readRules(s);
	}

	private void readDefinition(Scanner s) {
		// TODO Auto-generated method stub		
	}

	private void readRules(Scanner s) {

		rules = (rules == null) ? new ArrayList<Line>() : rules;

		// new delimeter for getting the tokens
		s.useDelimiter("(\\n+)|(\\s+\\{)");

		while (s.hasNextLine()) {
			String pattern = s.next();
			String action = s.next();
			Line tpl = new Line(pattern, action);
			rules.add(tpl);
		}

	}
}

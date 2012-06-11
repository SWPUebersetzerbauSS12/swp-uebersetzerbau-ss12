package de.fuberlin.projectcii.ParserGenerator.src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * Static class to get the Settings in the file
 * 
 * @author Patrick Schlott
 *
 */

public class Settings {
	private static String SettingsFilePath = "settings.ini";
	private static String EOF;
	private static String EPSILON;
	private static String GRAMMAR_PATH;
	
	/**
	 * Initialises the Settings-Variables
	 * 
	 * @author Patrick Schlott
	 *
	 * @throws IOException
	 */
	public static void initalize() throws IOException{
		try {
		    BufferedReader in = new BufferedReader(new FileReader(SettingsFilePath));
		    
		    String line;
		    while ((line = in.readLine()) != null) {
		    	//Split line in head and rump at ::=
		    	String[] KeyValue = line.split("=");
		    	String key = KeyValue[0];
		    	String value = KeyValue[1];
	
		    	if (key.equals("EOF")){
		    		EOF=value;
		    	}
		    	else if (key.equals("EPSILON")){
		    		EPSILON=value;
		    	}
		    	else if (key.equals("GRAMMAR_PATH")){
		    		GRAMMAR_PATH=value;
		    	}
		    }
		    in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @author Patrick Schlott
	 *
	 * @return eof Symbol for end of file
	 */
	public static String getEOF() {
		return EOF;
	}

	/**
	 * 
	 * @author Patrick Schlott
	 *
	 * @return epsilon Symbol for Epsilon
	 */
	public static String getEPSILON() {
		return EPSILON;
	}

	/**
	 * 
	 * @author Patrick Schlott
	 *
	 * @return grammar_path The path to the used grammar
	 */
	public static String getGRAMMAR_PATH() {
		return GRAMMAR_PATH;
	}
}

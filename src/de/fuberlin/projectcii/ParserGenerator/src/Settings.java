package de.fuberlin.projectcii.ParserGenerator.src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * Static class to get the Settings in the file
 */

public class Settings {
	private static String SettingsFilePath = "settings.ini";
	private static String EOF;
	private static String EPSILON;
	private static String GRAMMAR_PATH;
	
	private static Boolean GRAMMAR_READ;
	private static Boolean GRAMMAR_MOD;
	private static Boolean FIRSTSET;
	private static Boolean FOLLOWSET;
	private static Boolean PARSERTABLE;
	private static Boolean PARSING_STEPS;
	private static Boolean XMLTREE;
	
	/**
	 * Initialises the Settings-Variables
	 *
	 * @throws IOException
	 */
	public static void initalize() throws IOException{
		try {
		    BufferedReader in = new BufferedReader(new FileReader(SettingsFilePath));
		    
		    String line;
		    while ((line = in.readLine()) != null) {
		    	//Split line in head and rump at =
		        if (line.contains("=")){
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
		            else if (key.equals("GRAMMAR_READ")){
		                GRAMMAR_READ=Boolean.parseBoolean(value);
		            }
		            else if (key.equals("GRAMMAR_MOD")){
                        GRAMMAR_MOD=Boolean.parseBoolean(value);
                    }
		            else if (key.equals("FIRSTSET")){
                        FIRSTSET=Boolean.parseBoolean(value);
                    }
		            else if (key.equals("FOLLOWSET")){
		                FOLLOWSET=Boolean.parseBoolean(value);
                    }
		            else if (key.equals("PARSERTABLE")){
		                PARSERTABLE=Boolean.parseBoolean(value);
                    }
		            else if (key.equals("PARSING_STEPS")){
		                PARSING_STEPS=Boolean.parseBoolean(value);
                    }
		            else if (key.equals("XMLTREE")){
		                XMLTREE=Boolean.parseBoolean(value);
                    }
		        }
		    }
		    in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getEOF() {
		return EOF;
	}

	public static String getEPSILON() {
		return EPSILON;
	}

	public static String getGRAMMAR_PATH() {
		return GRAMMAR_PATH;
	}

    public static Boolean getGRAMMAR_READ() {
        return GRAMMAR_READ;
    }

    public static Boolean getGRAMMAR_MOD() {
        return GRAMMAR_MOD;
    }

    public static Boolean getFIRSTSET() {
        return FIRSTSET;
    }

    public static Boolean getFOLLOWSET() {
        return FOLLOWSET;
    }

    public static Boolean getPARSERTABLE() {
        return PARSERTABLE;
    }

    public static Boolean getPARSING_STEPS() {
        return PARSING_STEPS;
    }

    public static Boolean getXMLTREE() {
        return XMLTREE;
    }
}

package dfaprovider;

import java.io.File;

import regextodfaconverter.MinimalDfa;
import regextodfaconverter.fsm.StatePayload;

/**
 * Stellt einen rudimänteren DFA-Provider dar.
 * @author Maximilian Schröder
 *
 */
public class MinimalDfaProvider {

	/**
	 * Gibt den minimalen DFA für die angegebenen regulären Definitionen zurück.
	 * @param file Der absolute Pfad zu der Datei, die die regulären Definitionen enthalten.
	 * @return Den minimalen DFA für die angegebenen regulären Definitionen.
	 */
	public static MinimalDfa<Character,StatePayload> getMinimalDfa(File regularDefinitionFile)
	{
		//TODO: getMinimalDfa implementieren
		return null;
	}
}

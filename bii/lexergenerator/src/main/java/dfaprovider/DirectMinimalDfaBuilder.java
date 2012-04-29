package dfaprovider;

import java.io.File;

import regextodfaconverter.MinimalDfa;
import regextodfaconverter.fsm.StatePayload;

/**
 * Stellt einen MinimalDFA-Builder dar, der den DFA über den direkten Weg von
 * Regex zu DFA erstellt.
 * 
 * @author ?
 * 
 */
public class DirectMinimalDfaBuilder {

	/**
	 * Erstellt den minimalen DFA für die angegebenen regulären Definitionen und
	 * gibt ihn zurück.
	 * 
	 * @param regularDefinitionFile
	 *            Der absolute Pfad zu der Datei, die die regulären Definitionen
	 *            enthalten.
	 * @return Der minimalen DFA für die angegebenen regulären Definitionen.
	 * @throws MinimalDfaBuilderException
	 *             Wenn ein Fehler beim Erstellen des DFA's auftritt.
	 */
	public MinimalDfa<Character, StatePayload> buildMinimalDfa(
			File regularDefinitionFile) throws MinimalDfaBuilderException {
		// TODO: buildMinimalDfa implementieren.
		return null;
	}
}

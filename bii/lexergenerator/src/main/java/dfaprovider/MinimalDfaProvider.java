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
		MinimalDfa<Character,StatePayload> mDfa = null;
		//TODO: @Maximilian: getMinimalDfa implementieren.
		
		//@Max: So vorderst du die Erstellung eines DFA basierend auf der Eingabedatei an.
		//Das ist "orangene" Kasten in dem Übergangsdiagramm.
		//Diese drei Kommentarzeilen bitte anschließend löschen.
		mDfa = MinimalDfaBuilder.buildMinimalDfa(regularDefinitionFile);
		
		return mDfa;
	}
}

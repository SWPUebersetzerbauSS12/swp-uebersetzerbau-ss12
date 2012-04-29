package dfaprovider;

import java.io.File;

import regextodfaconverter.MinimalDfa;
import regextodfaconverter.fsm.StatePayload;

/**
 * Stellt einen MinimalDFA-Builder dar.
 * @author Daniel Rotar
 *
 */
public class MinimalDfaBuilder {
	
	/**
	 * Erstellt den minimalen DFA für die angegebenen regulären Definitionen und gibt ihn zurück.
	 * @param regularDefinitionFile Der absolute Pfad zu der Datei, die die regulären Definitionen enthalten.
	 * @return Der minimalen DFA für die angegebenen regulären Definitionen.
	 */
	public static MinimalDfa<Character,StatePayload> buildMinimalDfa(File regularDefinitionFile)
	{
		
		//-----------------------------------------------------------------------------
		//Dieser Teil soll später ersetzt werden.
		//Aktuell wird hier einfach irgendein ausgedachtes Beispiel "geladen" bzw. erzeugt, anstatt es aus der Datei auszulesen.
		//TODO: @Benjamin und Alexander: Diesen Bereich austauschen durch Inhalt der reg.def.-Datei.
		
		//-----------------------------------------------------------------------------
		return null;
	}
}

package dfaprovider;

import java.io.File;
import java.util.ArrayList;

import regextodfaconverter.ConvertExecption;
import regextodfaconverter.MinimalDfa;
import regextodfaconverter.RegexToNfaConverter;
import regextodfaconverter.fsm.FiniteStateMachine;
import regextodfaconverter.fsm.StatePayload;
import tokenmatcher.TokenType;

/**
 * Stellt einen MinimalDFA-Builder dar, der den DFA über den indirekten Weg von Regex zu NFA zu DFA erstellt.
 * @author Daniel Rotar
 * @author Benjamin Weißenfels
 *
 */
public class IndirectMinimalDfaBuilder implements MinimalDfaBuilder {
	
	/**
	 * Erstellt den minimalen DFA für die angegebenen regulären Definitionen und gibt ihn zurück.
	 * @param regularDefinitionFile Der absolute Pfad zu der Datei, die die regulären Definitionen enthalten.
	 * @return Der minimalen DFA für die angegebenen regulären Definitionen.
	 * @throws MinimalDfaBuilderException Wenn ein Fehler beim Erstellen des DFA's auftritt.
	 */
	public MinimalDfa<Character,StatePayload> buildMinimalDfa(File regularDefinitionFile) throws MinimalDfaBuilderException
	{
		RegexToNfaConverter<StatePayload> converter = new RegexToNfaConverter<StatePayload>();
		ArrayList<FiniteStateMachine<Character, StatePayload>> fsms = new ArrayList<FiniteStateMachine<Character, StatePayload>>();
		
		StatePayload payload = null;
		String regex = "";
		FiniteStateMachine<Character, StatePayload> fsm = null;
		
		//-----------------------------------------------------------------------------
		//Dieser Teil soll später ersetzt werden.
		//Aktuell wird hier einfach irgendein ausgedachtes Beispiel "geladen" bzw. erzeugt, anstatt es aus der Datei auszulesen.
		//TODO: @Benjamin und Alexander: Diesen Bereich austauschen durch Inhalt der reg.def.-Datei.
		
		//Hier würde quasi eine Schleife anfangen
		//1. if Token
		payload = new StatePayload( "KEYWORD", "IF",-1); //Diese Information muss irgendwo im Textfile stehen
		regex = "((i)(f))"; //Diese Information muss irgendwo im Textfile stehen, dass ist der regex zum Token
		
		// Aus Regex NFA machen.	
		try {
			fsm = converter.convertToNFA(regex, payload);
		} catch (ConvertExecption e) {
			throw new MinimalDfaBuilderException("Der reguläre Ausdruck '" + regex + "' kann nicht in einen Automaten umgewandelt werden: " + e.getMessage());
		}
		fsms.add(fsm);
		//Die nächsten Zeilen wären jetzt normalerweise ein nächster Schleifendurchlauf
		//2. Int Token (nur rudimentär, ka ob der regex alles abdekt)
		payload = new StatePayload("NUM","parseInt()",-2); //Diese Information muss irgendwo im Textfile stehen
		regex = "(((((((((((1))|(2))|(3))|(4))|(5))|(6))|(7))|(8))|(9))(((((((((((0)|(1))|(2))|(3))|(4))|(5))|(6))|(7))|(8))|(9))*))"; //Diese Information muss irgendwo im Textfile stehen
		
		// Aus Regex NFA machen.	
		try {
			fsm = converter.convertToNFA(regex, payload);
		} catch (ConvertExecption e) {
			throw new MinimalDfaBuilderException("Der reguläre Ausdruck '" + regex + "' kann nicht in einen Automaten umgewandelt werden: " + e.getMessage());
		}
		fsms.add(fsm);
		//-----------------------------------------------------------------------------
		
		//Alle FSMs vereinigen
		if (fsms.size()==0)
		{
			throw new MinimalDfaBuilderException("Die angegebene Datei enthält keine gültigen regulären Definitionen!");
		}
		else if (fsms.size()==1)
		{
			fsm = fsms.get(0);
		}
		else
		{
			fsm = fsms.get(0);
			for (int i = 1; i < fsms.size(); i++)
			{
				fsm.union(fsms.get(i));
			}
		}
		
		MinimalDfa<Character, StatePayload> mDfa = null;
		try {
			mDfa = new MinimalDfa<Character, StatePayload>(fsm);
		} catch (ConvertExecption e) {
			throw new MinimalDfaBuilderException("Fehler beim Erstellen des minimalen DFA's: " + e.getMessage());
		}
		
		return mDfa;
	}
}

package regextodfaconverter.fsm.excpetions;

/**
 * Stellt einen Fehler dar, der auftritt, wenn null als Wert für einen Zustand
 * �bergeben worden ist aber null keine g�ltige Eingabe darstellt.
 * 
 * @author Daniel Rotar
 * 
 */
public class NullStateException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1878770891398993283L;

	/**
	 * Erstellt ein neues NullStateException Objekt.
	 */
	public NullStateException() {
		super("Der Wert für diesen Zustand darf nicht null sein!");
	}

}
package regextodfaconverter.fsm.excpetions;

/**
 * Stellt einen Fehler dar, der auftritt, wenn versucht wird in einem Zustand einen �bergang hinzuzuf�gen, der bereits vorhanden ist.
 * @author Daniel Rotar
 *
 */
public class TransitionAlreadyExistsException extends Exception
{
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1444077926614652782L;

	
	
	/**
	 * Erstellt ein neues TransitionException Objekt.
	 */
	public TransitionAlreadyExistsException()
	{
		super("Der �bergang existiert bereits!");
	}
	
}

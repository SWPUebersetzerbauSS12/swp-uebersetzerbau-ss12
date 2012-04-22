package regextodfaconverter.fsm;

/**
 * Stellt einen Übergang eines endlicher Automaten (bzw. einer Zustandsmaschine)
 * dar.
 * 
 * @author Daniel Rotar
 * 
 * @param <ConditionType>
 *            Der Typ der Bedingung für den Zustandsübergang.
 * @param <StatePayloadType>
 *            Der Typ des Inhalts der Zustände.
 */
public class Transition<ConditionType extends Comparable<ConditionType>, StatePayloadType>
		implements Comparable<Transition<ConditionType, StatePayloadType>> {

	/**
	 * Die Bedingung für den Zustandsübergang (null für einen Epsilon-Übergang).
	 */
	private ConditionType _condition;
	/**
	 * Der Folgezustand.
	 */
	private State<ConditionType, StatePayloadType> _state;

	/**
	 * Gibt die Bedingung für den Zustandsübergang zurück.
	 * 
	 * @return Die Bedingung für den Zustandsübergang (null für einen
	 *         Epsilon-Übergang).
	 */
	public ConditionType getCondition() {
		return _condition;
	}

	/**
	 * Gibt den Folgezustand zurück.
	 * 
	 * @return Der Folgezustand.
	 */
	public State<ConditionType, StatePayloadType> getState() {
		return _state;
	}

	public int compareTo(Transition<ConditionType, StatePayloadType> o) {
		if (o.getCondition() == getCondition() && o.getState() == getState()) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * Erstellt ein neues Transition Objekt.
	 * 
	 * @param condition
	 *            Die Bedingung für den Zustandsübergang (null für einen
	 *            Epsilon-Übergang).
	 * @param state
	 *            Der Folgezustand.
	 */
	public Transition(ConditionType condition,
			State<ConditionType, StatePayloadType> state) {
		_condition = condition;
		_state = state;
	}

}

package regextodfaconverter.fsm;

import java.util.HashSet;
import java.util.UUID;

import regextodfaconverter.fsm.excpetions.TransitionAlreadyExistsException;

/**
 * Stellt einen Zustand eines endlicher Automaten (bzw. einer Zustandsmaschine)
 * dar.
 * 
 * @author Daniel Rotar
 * 
 * @param <TransitionConditionType>
 *            Der Typ der Bedingung für einen Zustandsübergang.
 * @param <PayloadType>
 *            Der Typ des Inhalts.
 */
public class State<TransitionConditionType extends Comparable<TransitionConditionType>, PayloadType>
		implements Comparable<State<TransitionConditionType, PayloadType>>,
		tokenmatcher.State<PayloadType> {

	/**
	 * Die eindetige UUID dieses Zustandes.
	 */
	private UUID _uuid;
	/**
	 * Der in diesem Zustand hinterlegte Inhalt.
	 */
	private PayloadType _payload;
	/**
	 * Die Übergänge, die von diesem Zustand möglich sind.
	 */
	private HashSet<Transition<TransitionConditionType, PayloadType>> _transitions;
	/**
	 * Der Zustandstyp.
	 */
	private StateType _type;

	/**
	 * Gibt die eindetige UUID dieses Zustandes zurück.
	 * 
	 * @return Die eindetige UUID dieses Zustandes.
	 */
	public UUID getUUID() {
		return _uuid;
	}

	/**
	 * Setzt die eindetige UUID dieses Zustandes fest.
	 * 
	 * @param uuid
	 *            Die eindetige UUID dieses Zustandes.
	 */
	private void setUUID(UUID uuid) {
		_uuid = uuid;
	}

	/**
	 * Generiert eine neue UUID für diesen Zustand.
	 */
	private void generateNewUUID() {
		setUUID(UUID.randomUUID());
	}

	/**
	 * Gibt den in diesem Zustand hinterlegte Inhalt zurück.
	 * 
	 * @return Der in diesem Zustand hinterlegte Inhalt.
	 */
	public PayloadType getPayload() {
		return _payload;
	}

	/**
	 * Setzt den in diesem Zustand zu hinterlegenden Inhalt fest.
	 * 
	 * @param payload
	 *            Der in diesem Zustand zu hinterlegende Inhalt.
	 */
	public void setPayload(PayloadType payload) {
		_payload = payload;
	}

	/**
	 * Gibt die Übergänge, die von diesem Zustand möglich sind zurück.
	 * 
	 * @return Die Übergänge, die von diesem Zustand möglich sind.
	 */
	public HashSet<Transition<TransitionConditionType, PayloadType>> getTransitions() {
		return _transitions;
	}

	/**
	 * Setzt die Übergänge, die von diesem Zustand möglich sind fest.
	 * 
	 * @param transitions
	 *            Die Übergänge, die von diesem Zustand möglich sind.
	 */
	private void setTransitiosn(
			HashSet<Transition<TransitionConditionType, PayloadType>> transitions) {
		_transitions = transitions;
	}

	/**
	 * Gibt den Zustandstyp zurück.
	 * 
	 * @return Der Zustandstyp.
	 */
	public StateType getType() {
		// // Ein Zustand ohne ausgehende Übergänge ist immer ein Endzustand, es
		// // sei den er ist ein Startzustand.
		// if (_type != StateType.INITIAL && _transitions.isEmpty()) {
		// return StateType.FINITE;
		// }
		return _type;
	}

	/**
	 * Legt den Zustandstyp fest.
	 * 
	 * @param type
	 *            Der Zustandstyp.
	 */
	protected void setType(StateType type) {
		_type = type;
	}

	/**
	 * Legt den Zustandstyp auf INITIAL fest.
	 */
	protected void SetTypeToInitial() {
		setType(StateType.INITIAL);
	}

	/**
	 * Legt den Zustandstyp auf FINITE fest.
	 */
	public void SetTypeToFinite() {
		setType(StateType.FINITE);
	}

	/**
	 * Legt den Zustandstyp auf DEFAULT fest.
	 */
	public void SetTypeToDefault() {
		setType(StateType.DEFAULT);
	}

	/**
	 * Gibt an, ob der Zustandtyp dieses Zustands FINITE ist.
	 */
	public boolean isFiniteState() {
		return (getType() == StateType.FINITE);
	}

	/**
	 * Gibt an, ob der Zustandtyp dieses Zustands INITIAL ist.
	 */
	public boolean isInitialState() {
		return (getType() == StateType.INITIAL);
	}

	/**
	 * Gibt an, ob der Zustandtyp dieses Zustands DEFAULT ist.
	 */
	public boolean isDefaultState() {
		return (getType() == StateType.DEFAULT);
	}

	/**
	 * Fügt dem aktuellen Zustand einen Nachfolgezustand hinzu.
	 * 
	 * @param condition
	 *            Die Bedingung für den Zustandsübergang.
	 * @param state
	 *            Der einzufügende Nachfolgezustand.
	 * @throws TransitionAlreadyExistsException
	 *             Wenn der Übergang bereits vorhanden ist.
	 */
	protected void addState(TransitionConditionType condition,
			State<TransitionConditionType, PayloadType> state)
			throws TransitionAlreadyExistsException {
		if (!getTransitions().add(
				new Transition<TransitionConditionType, PayloadType>(condition,
						state))) {
			throw new TransitionAlreadyExistsException();
		}
	}

	public int compareTo(State<TransitionConditionType, PayloadType> o) {
		if (o.getUUID() == getUUID()) {
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * Erstellt ein neues State Objekt.
	 */
	public State() {
		generateNewUUID();
		setPayload(null);
		setTransitiosn(new HashSet<Transition<TransitionConditionType, PayloadType>>());
		SetTypeToDefault();
	}

	/**
	 * Erstellt ein neues State Objekt.
	 * 
	 * @param payload
	 *            Der in diesem Zustand hinterlegte Inhalt.
	 */
	public State(PayloadType payload) {
		this();
		setPayload(payload);
	}

	/**
	 * Erstellt ein neues State Objekt.
	 * 
	 * @param isFinite
	 *            Gibt an, ob es sich bei diesem Zustand um einen Endzustand
	 *            handelt.
	 */
	public State(boolean isFinite) {
		this();
		if (isFinite)
			SetTypeToFinite();
	}

	/**
	 * Erstellt ein neues State Objekt.
	 * 
	 * @param payload
	 *            Der in diesem Zustand hinterlegte Inhalt.
	 * @param isFinite
	 *            Gibt an, ob es sich bei diesem Zustand um einen Endzustand
	 *            handelt.
	 */
	public State(PayloadType payload, boolean isFinite) {
		this();
		setPayload(payload);
		if (isFinite)
			SetTypeToFinite();
	}

}

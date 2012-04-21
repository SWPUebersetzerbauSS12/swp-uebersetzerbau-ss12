package tokenmatcher;



public interface DeterministicFiniteAutomata<E,Payload> {
	
	
	
	/**
	 * Wechselt in einen anderen Zustand, sofern 
	 * es einen Übergang in der Übergangstabelle ausgehend vom aktuellen Zustand in 
	 * den angegebenen Element gibt. 
	 * 
	 * @param element Das Element, welches den Übergang definiert.
	 * @return 	Den neuen Zustand oder null, falls es keinen 
	 * 					Übergang in der Übergangstabelle gibt.
	 */
	State<Payload> changeStateByElement( E element);
	
	
	
	/**
	 * Prüft, ob ein Übergang für das Lesen des 
	 * spezifizierten Elementes definiert ist. 
	 * @param element Das Element, für das geprüft werden soll, ob ein Übergang 
	 *               aus dem aktuellen Zustand durch Lesen des Elementes definiert ist.
	 * @return true, wenn es einen Übergang gibt, anderenfalls false.
	 */
	boolean canChangeStateByElement( E element);
	
	
	/**
	 * @return Liefert den aktuellen Zustand.
	 */
	State<Payload> getCurrentState();
	
	
	/**
	 * Setzt den DFA wieder in den Startzustand zurück.
	 */
	void resetToInitialState();
	
	
}

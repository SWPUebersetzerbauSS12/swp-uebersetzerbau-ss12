package de.fuberlin.projectci.parseTable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.fuberlin.commons.util.EasyComparableObject;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;



/**
 * Repräsention einer Parsetabelle mit Action- und Goto-Funktionen.
 *
 */
public class ParseTable extends EasyComparableObject{		
	public Map<State, ActionTable> state2ActionTable = new HashMap<State, ParseTable.ActionTable>();
	public Map<State, GotoTable> state2GotoTable = new HashMap<State, ParseTable.GotoTable>();
	private State initialState;
	
	@Override
	protected Object[] getSignificantFields() {
		return new Object[]{initialState,state2ActionTable,state2GotoTable};
	}
	
	/**
	 * 
	 * @return Startzustand
	 */
	public State getInitialState(){
		return initialState;
	}

	/**
	 * Liefert die {@link Action} zu einem {@link State} und einem {@link TerminalSymbol}
	 * @param state
	 * @param terminalSymbol
	 * @return
	 */
	public Action getAction(State state, TerminalSymbol terminalSymbol) {
		return getActionTableForState(state).getAction(terminalSymbol);
	}
	 
	/**
	 * Liefert den Wert der Goto-Funktion zu einem {@link State} und einem {@link NonTerminalSymbol}
	 * @param s
	 * @param nts
	 * @return
	 */
	public Goto getGoto(State s, NonTerminalSymbol nts) {
		return getGotoTableForState(s).getGoto(nts);
	}
	/** Setzt den Startzustand*/
	public void setInitialState(State initialState){
		this.initialState=initialState;
	}
	
	/**
	 * Liefert den internen {@link ActionTable} für einen {@link State}
	 * @param s
	 * @return
	 */
	public ActionTable getActionTableForState(State s){
		if (!state2ActionTable.containsKey(s)){
			state2ActionTable.put(s, new ActionTable());
		}
		return state2ActionTable.get(s);
	}
	/**
	 * Liefert den internen {@link GotoTable} für einen {@link State}
	 * @param s
	 * @return
	 */
	public GotoTable getGotoTableForState(State s){
		if (!state2GotoTable.containsKey(s)){
			state2GotoTable.put(s, new GotoTable());
		}
		return state2GotoTable.get(s);
	}
	
	/**
	 * Mappt ein {@link TerminalSymbol} auf eine {@link Action}
	 *
	 */
	public static class ActionTable extends EasyComparableObject{
		private static Action ERROR_ACTION=new ErrorAction();
		private Map<TerminalSymbol, Action> terminalSymbol2Action= new HashMap<TerminalSymbol, Action>();
		
		@Override
		protected Object[] getSignificantFields() {
			return new Object[]{terminalSymbol2Action, getClass()};
		}
		
		public Action getAction(TerminalSymbol nts) {
			Action a= terminalSymbol2Action.get(nts);
			return a!=null?a:ERROR_ACTION;
		}
		
		public void setActionForTerminalSymbol(Action action, TerminalSymbol terminalSymbol){
			terminalSymbol2Action.put(terminalSymbol, action);
		}
		
		@Override
		public String toString() {
			StringBuffer strBuf=new StringBuffer();
			Set<TerminalSymbol> terminalSymbols = terminalSymbol2Action.keySet();
			strBuf.append("[");
			int i=0;
			for (TerminalSymbol aTerminalSymbol : terminalSymbols) {
				if (i>0){
					strBuf.append(", ");
				}
				strBuf.append(aTerminalSymbol);
				strBuf.append(" → ");
				strBuf.append(getAction(aTerminalSymbol));
				i++;
			}
			strBuf.append("]");
			return strBuf.toString();
		}
		
		public List<TerminalSymbol> getAllLegalTerminals() {
			// Temporäre Liste
			List<TerminalSymbol> temp = new LinkedList<TerminalSymbol>();
			
			// Alle Terminale holen
			Set<TerminalSymbol> terminals = terminalSymbol2Action.keySet();
			
			// Über die Terminalsymbole iterieren und prüfen, ob diese wirklich erlaubt sind
			for(TerminalSymbol t : terminals) {
				Action a = terminalSymbol2Action.get(t);
				if(a != null && !(a instanceof ErrorAction)) {
					temp.add(t);
				}
			}
			return temp;
			
		}

		
		
	}
	
	/**
	 * 
	 * Mappt ein {@link NonTerminalSymbol} auf ein {@link Goto}
	 *
	 */
	public static class GotoTable extends EasyComparableObject{
		private Map<NonTerminalSymbol, Goto> nonTerminalSymbol2Goto= new HashMap<NonTerminalSymbol, Goto>();
		
		@Override
		protected Object[] getSignificantFields() {
			return new Object[]{nonTerminalSymbol2Goto, getClass()};
		}
		
		public Goto getGoto(NonTerminalSymbol nonTerminalSymbol){
			return nonTerminalSymbol2Goto.get(nonTerminalSymbol);
		}
		
		public void setGotoForNonTerminalSymbol(Goto _goto, NonTerminalSymbol nonTerminalSymbol){
			nonTerminalSymbol2Goto.put(nonTerminalSymbol, _goto);
		}
		
		@Override
		public String toString() {
			StringBuffer strBuf=new StringBuffer();
			Set<NonTerminalSymbol> nonTerminalSymbols = nonTerminalSymbol2Goto.keySet();
			strBuf.append("[");
			int i=0;
			for (NonTerminalSymbol aNonTerminalSymbol : nonTerminalSymbols) {
				if (i>0){
					strBuf.append(", ");
				}
				strBuf.append(aNonTerminalSymbol);
				strBuf.append(" → ");
				strBuf.append(getGoto(aNonTerminalSymbol).getTargetState().getId());
				i++;
			}
			strBuf.append("]");
			return strBuf.toString();
		}
	}
	
	@Override
	public String toString() {		
		StringBuffer strBuf=new StringBuffer();
		// Annahme: Es gibt keinen State zu dem es ein Goto aber keine Action gibt
		Set<State> states = sortedStates();
		for (State aState : states) {
			strBuf.append(aState);
			strBuf.append(": ");
			strBuf.append(getActionTableForState(aState));
			strBuf.append(" ");
			strBuf.append(getGotoTableForState(aState));
			strBuf.append("\n");
		}
		
		return strBuf.toString();
	}
	
	/** Zum Sortieren der States nach ihrer ID. */
	private static Comparator<State> StateComparator=new Comparator<State>() {
		@Override
		public int compare(State s1, State s2) {
			return s1.getId()-s2.getId();
		}
	};
	
	/** Liefert ein {@link SortedSet} der States.*/
	SortedSet<State> sortedStates(){
		SortedSet<State> result=new TreeSet<State>(StateComparator);
		result.addAll(state2ActionTable.keySet());
		return result;
	}
}
 

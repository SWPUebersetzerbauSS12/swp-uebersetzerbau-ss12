package de.fuberlin.projectci.parseTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.fuberlin.commons.util.EasyComparableObject;
import de.fuberlin.projectci.grammar.NonTerminalSymbol;
import de.fuberlin.projectci.grammar.TerminalSymbol;

/**
 * Repräsention der Action- und Goto-Tabellen.
 *
 */
public class ParseTable extends EasyComparableObject{		
	private Map<State, ActionTable> state2ActionTable = new HashMap<State, ParseTable.ActionTable>();
	private Map<State, GotoTable> state2GotoTable = new HashMap<State, ParseTable.GotoTable>();
	private State initialState;
	
	@Override
	protected Object[] getSignificantFields() {
		return new Object[]{initialState,state2ActionTable,state2GotoTable};
	}
	
	public State getInitialState(){
		return initialState;
	}

	public Action getAction(State s, TerminalSymbol ts) {
		return getActionTableForState(s).getAction(ts);
	}
	 
	public Goto getGoto(State s, NonTerminalSymbol nts) {
		return getGotoTableForState(s).getGoto(nts);
	}
	
	public void setInitialState(State initialState){
		this.initialState=initialState;
	}
	
	public ActionTable getActionTableForState(State s){
		if (!state2ActionTable.containsKey(s)){
			state2ActionTable.put(s, new ActionTable());
		}
		return state2ActionTable.get(s);
	}
	
	public GotoTable getGotoTableForState(State s){
		if (!state2GotoTable.containsKey(s)){
			state2GotoTable.put(s, new GotoTable());
		}
		return state2GotoTable.get(s);
	}
	
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

		
		
	}
	
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
				strBuf.append(getGoto(aNonTerminalSymbol).getTargetState().getName());
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
		Set<State> states = state2ActionTable.keySet();
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

	
}
 

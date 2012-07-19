/*
 * 
 * Copyright 2012 lexergen.
 * This file is part of lexergen.
 * 
 * lexergen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * lexergen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with lexergen.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * lexergen:
 * A tool to chunk source code into tokens for further processing in a compiler chain.
 * 
 * Projectgroup: bi, bii
 * 
 * Authors: Johannes Dahlke
 * 
 * Module:  Softwareprojekt Übersetzerbau 2012 
 * 
 * Created: Apr. 2012 
 * Version: 1.0
 *
 */

package de.fuberlin.bii.tokenmatcher.fsm;



/**
 * Modelliert einen Zustand der Finite State Machine. 
 * Ein Zustand kann als Endzustand und Startzustand gekennzeichnet sein 
 * und mit einem Objekt assoziert sein.
 * 
 * @author Johannes Dahlke
 * @deprecated
 *
 */
public class State {
	
	public static final int STATE_NORMAL = 0;
  public static final int STATE_FINAL = 1;
	public static final int STATE_START = 2;
	 
	private int kind;
	private Object payload;
	 
	 
	public State( int kind, Object payload) {
		super();
		setKind( kind);
	}
	
	private void setKind( int kind) {
		this.kind = kind;
	}
	
	public static State newFinalState( Object payload) {
		return new State( STATE_FINAL, payload);
	}
	
	public static State newStartState( Object payload) {
		return new State( STATE_START, payload);
	}
	
	public static State newNormalState( Object payload) {
		return new State( STATE_NORMAL, payload);
	}
	
	public void setFinal() {
		kind |= STATE_FINAL;
	}
	
	public void unsetFinal() {
		kind ^= STATE_FINAL;
	}
	
	public void setStart() {
		// set this state as start state
		kind |= STATE_START;
	}
	
	public void unsetStart() {
		kind ^= STATE_START;
	}
	
	 
	public boolean isFiniteState() {
		return ( kind & STATE_FINAL) == STATE_FINAL; 	 
	}
	 
	public boolean isStartState() {
		return ( kind & STATE_FINAL) == STATE_START; 	 		 
	}
	
	public void setPayload( Object payload) {
		this.payload = payload;
	}
	
	public Object getPayload() {
		return payload;
	}
	 
}

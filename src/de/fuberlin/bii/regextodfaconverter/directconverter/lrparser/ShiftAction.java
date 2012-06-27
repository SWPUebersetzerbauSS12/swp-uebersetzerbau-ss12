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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser;


import java.io.Serializable;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Closure;
import de.fuberlin.bii.utils.Test;

/**
 * 
 * @author Johannes Dahlke
 *
 * @param <Element>
 */
@SuppressWarnings("rawtypes")
public class ShiftAction<Element extends Symbol, SpecializedClosure extends Closure> extends Action<Element, SpecializedClosure> implements Serializable{

	private static final long serialVersionUID = -1737644534363947153L;
	private SpecializedClosure toClosure;
	private Terminal<Element> terminalToHandle;

	public ShiftAction( SpecializedClosure toClosure, Terminal<Element> theTerminalToHandle) {
		super();
		this.toClosure = toClosure;
		this.terminalToHandle = theTerminalToHandle;
	}

	public Object handleOnAutomat( ItemAutomatInterior<Element, SpecializedClosure> itemAutomat) throws ShiftException {
	  if (itemAutomat.getInputQueue().peek().equals(terminalToHandle.getSymbol())) {
			itemAutomat.getClosureStack().push( toClosure);
		  Element nextElement = itemAutomat.getInputQueue().poll();
		  itemAutomat.getSymbolStack().push( new Terminal( nextElement));
		} else
			throw new ShiftException(String.format("Missing terminal %s to handle at the top of the stack.", terminalToHandle));

		return toClosure;
	}
	
	public SpecializedClosure getToClosure() {
		return toClosure;
	}
	
	public Terminal<Element> getTerminalToHandle() {
		return terminalToHandle;
	}
	
	@Override
	public String toString() {
		return "Shift to " + toClosure.toString() + " by " + terminalToHandle.toString();
	}
	
	@Override
	public boolean equals( Object theOtherObject) {
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !(theOtherObject instanceof ShiftAction))
			return false;
		
		ShiftAction<Symbol, Closure> theOtherShiftAction = (ShiftAction<Symbol, Closure> ) theOtherObject;
		
		return this.toClosure.equals( theOtherShiftAction.toClosure) 
				    && this.terminalToHandle.equals( theOtherShiftAction.terminalToHandle);
	}

}

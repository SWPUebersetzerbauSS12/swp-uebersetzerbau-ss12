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

package regextodfaconverter.directconverter.lr0parser;


import regextodfaconverter.directconverter.EventHandler;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;

public class ShiftAction<Element extends Comparable<Element>> extends Action<Element> implements EventHandler {

	private Closure toClosure;
	private Terminal<Element> terminalToHandle;

	public ShiftAction(ItemAutomataInterior<Element> itemAutomata, Closure toClosure, Terminal<Element> theTerminalToHandle) {
		super( itemAutomata);
		this.toClosure = toClosure;
		this.terminalToHandle = theTerminalToHandle;
	}

	public Object handle(Object sender) throws ShiftException {
		if (itemAutomata.getInputQueue().peek().equals(terminalToHandle.getSymbol())) {
			itemAutomata.getClosureStack().push(toClosure);
		  Element nextElement = itemAutomata.getInputQueue().poll();
		  itemAutomata.getSymbolStack().push( new Terminal( nextElement));
		} else
			throw new ShiftException(String.format("Missing terminal %s to handle at the top of the stack.", terminalToHandle));

		return toClosure;
	}
	
	public Closure getToClosure() {
		return toClosure;
	}
	
	public Terminal<Element> getTerminalToHandle() {
		return terminalToHandle;
	}
	
	@Override
	public String toString() {
		return "Shift to " + toClosure.toString() + " by " + terminalToHandle.toString();
	}

}

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
import regextodfaconverter.directconverter.lr0parser.grammar.Nonterminal;
import regextodfaconverter.directconverter.lr0parser.grammar.Terminal;
import regextodfaconverter.directconverter.lr0parser.itemset.Closure;

/**
 * 
 * @author Johannes Dahlke
 *
 * @param <Element>
 */
public class Goto<Element extends Comparable<Element>> implements EventHandler {

	private Closure toClosure;
	private Nonterminal nonterminalToHandle;
	protected ItemAutomataInterior<Element> itemAutomata;
	
	public Goto(ItemAutomataInterior<Element> itemAutomata, Closure toClosure, Nonterminal theNonterminalToHandle) {
		super();
		this.itemAutomata = itemAutomata;
		this.toClosure = toClosure;
		this.nonterminalToHandle = theNonterminalToHandle;
	}

	public Object handle(Object sender) throws GotoException {
		if (itemAutomata.getSymbolStack().peek().equals( nonterminalToHandle)) {
			itemAutomata.getClosureStack().push(toClosure);
		} else
			throw new GotoException(String.format("Missing expected nonterminal %s.", nonterminalToHandle));

		return toClosure;
	}
	
	public Nonterminal getNonterminalToHandle() {
		return nonterminalToHandle;
	}
	
	public Closure getToClosure() {
		return toClosure;
	}
	
	@Override
	public String toString() {
		return "Goto " + toClosure.toString() + " by " + nonterminalToHandle.toString();
	}

}

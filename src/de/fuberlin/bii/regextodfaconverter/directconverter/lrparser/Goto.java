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

import de.fuberlin.bii.regextodfaconverter.directconverter.AutomatEventHandler;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Nonterminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Symbol;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.Terminal;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Closure;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset.Lr0Closure;

/**
 * 
 * @author Johannes Dahlke
 *
 * @param <Element>
 */
public class Goto<Element extends Symbol, SpecializedClosure extends Closure> implements AutomatEventHandler<Element, SpecializedClosure>, Serializable {

	private static final long serialVersionUID = 7847504444804006960L;
	private SpecializedClosure toClosure;
	private Nonterminal nonterminalToHandle;
	
	public Goto( SpecializedClosure toClosure, Nonterminal theNonterminalToHandle) {
		super();
		this.toClosure = toClosure;
		this.nonterminalToHandle = theNonterminalToHandle;
	}

	public Object handleOnAutomat( ItemAutomatInterior<Element, SpecializedClosure> itemAutomat) throws Exception {
  	if (itemAutomat.getSymbolStack().peek().equals( nonterminalToHandle)) {
			itemAutomat.getClosureStack().push(toClosure);
		} else
			throw new GotoException(String.format("Missing expected nonterminal %s.", nonterminalToHandle));

		return toClosure;
	}
	
	public Nonterminal getNonterminalToHandle() {
		return nonterminalToHandle;
	}
	
	public SpecializedClosure getToClosure() {
		return toClosure;
	}
	
	@Override
	public String toString() {
		return "Goto " + toClosure.toString() + " by " + nonterminalToHandle.toString();
	}

}

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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.itemset;

import java.util.Collection;
import java.util.HashSet;


/**
 * Eine Menge an {@link Lr0Item}s.
 * 
 * @author Johannes Dahlke
 *
 */
public class Lr0ItemSet extends ItemSet<Lr0Item> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2350428706693175887L;

	public Lr0ItemSet() {
		super();
	}
	
	public Lr0ItemSet( Collection<? extends Lr0Item> collection) {
		super( collection);
	}

	
}

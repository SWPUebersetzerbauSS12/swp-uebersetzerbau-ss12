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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;

import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionMap;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.ProductionRule;
import de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar.RuleElement;
import de.fuberlin.bii.utils.Test;


/**
 * Bildet die Items auf ihre Eigenschaft Kernelitem (true) oder Nichtkernelitem /false) ab.
 * 
 * @author Johannes Dahlke
 *
 */
public abstract class Closure<SpecializedItem extends Item> extends HashMap<SpecializedItem, Boolean> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7957752941028034850L;
	private int number = -1;
	
	public boolean isKernelItem( SpecializedItem item) {
		return this.get( item);
	}
	
	public boolean addAsKernelItem( SpecializedItem item) {
		return this.put( item, true) == null;
	}
	
	public boolean addAsNonkernelItem( SpecializedItem item) {
		return this.put( item, false) == null;
	}
	
	public boolean putAsKernelItem( SpecializedItem item) {
		return this.put( item, true);
	}
	
	public boolean putAsNonkernelItem( SpecializedItem item) {
		return this.put( item, false);
	}
	
	
	@Override
	public abstract boolean containsKey(Object key);
	
	
	public ProductionMap toProductionMap() {
		ProductionMap result = new ProductionMap();
		for ( SpecializedItem item : this.keySet()) {
			result.addProduction( item.toProduction());
		}
		return result;
	}
	
	@Override
	public abstract boolean equals( Object theOtherObject);
		
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	
	public Integer getNumber() {
		return number;
	}
	
	public String getName() {
		return "I" + number;
	}
	
	@Override
	public String toString() {
		return number > -1 ? getName() : toItemsString();
	}

	public String toItemsString() {
		return number > -1 ? getName() + ": " + super.toString() : super.toString();
	}
	
}

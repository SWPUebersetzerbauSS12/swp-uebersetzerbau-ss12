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
	
	/**
	 * Test auf Kernel-Item.
	 * @param item
	 * @return
	 */
	public boolean isKernelItem( SpecializedItem item) {
		return this.get( item);
	}
	
	/** 
	 * Fügt ein Item als Kernel-Item zur Closure hinzu. 
	 * @param item
	 * @return True, wenn das Item zuvor noch nicht in der Closure war, sonst False.
	 */
	public boolean addAsKernelItem( SpecializedItem item) {
		return this.put( item, true) == null;
	}
	
	/** 
	 * Fügt ein Item als Nichtkernel-Item zur Closure hinzu. 
	 * @param item
	 * @return True, wenn das Item zuvor noch nicht in der Closure war, sonst False.
	 */
	public boolean addAsNonkernelItem( SpecializedItem item) {
		return this.put( item, false) == null;
	}

	/** 
	 * Fügt ein Item als Kernel-Item zur Closure hinzu. 
	 * @param item
	 * @return True, wenn das Item zuvor schon als KernelItem geführt wurde. 
	 *         False, falls das Item zuvor als NichtkernelItem geführt wurde.
	 *         Null, falss das Item zuvor noch nicht zur Closure gehört hat.
	 */
	public boolean putAsKernelItem( SpecializedItem item) {
		return this.put( item, true);
	}

	/** 
	 * Fügt ein Item als Nichtkernel-Item zur Closure hinzu. 
	 * @param item
	 * @return True, wenn das Item zuvor als KernelItem geführt wurde. 
	 *         False, falls das Item zuvor schon als NichtkernelItem geführt wurde.
	 *         Null, falss das Item zuvor noch nicht zur Closure gehört hat.
	 */
	public boolean putAsNonkernelItem( SpecializedItem item) {
		return this.put( item, false);
	}
	
	
	@Override
	public abstract boolean containsKey(Object key);
	
	/**
	 * Liefert die Closure als {@link ProductionMap}.
	 * @return
	 */
	public ProductionMap toProductionMap() {
		ProductionMap result = new ProductionMap();
		for ( SpecializedItem item : this.keySet()) {
			result.addProduction( item.toProduction());
		}
		return result;
	}
	
	@Override
	public abstract boolean equals( Object theOtherObject);
	
	protected boolean superEquals( Object theOtherObject) {
		return super.equals( theOtherObject);
	}
	
		
	/**
	 * Weist der Closure eine Nummer zu.
	 * 
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	
	
	/**
	 * Liefert die Kennung der Closure.
	 * 
	 * @return
	 */
	public Integer getNumber() {
		return number;
	}
	
	/**
	 * Liefert den Namen der Closure.
	 * 
	 * @return
	 */
	public String getName() {
		return "I" + number;
	}
	
	@Override
	public String toString() {
		return number > -1 ? getName() : toItemsString();
	}

	/**
	 * Gibt einen String unter Berücksichtigung aller Itemelemente zurück. 
	 * 
	 * @return
	 */
	public String toItemsString() {
		return number > -1 ? getName() + ": " + super.toString() : super.toString();
	}
	
}

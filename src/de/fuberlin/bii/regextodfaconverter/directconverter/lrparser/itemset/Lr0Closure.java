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
public class Lr0Closure extends Closure<Lr0Item> {
	

	public Lr0ItemSet getItemSet() {
		Lr0ItemSet itemSet = new Lr0ItemSet();
		itemSet.addAll( this.keySet());	
		return itemSet;
	}
	
	@Override
	public boolean containsKey(Object key) {
		if ( key == null)
			return false;
		if ( !( key instanceof Lr0Item))
			return false;
		for (Lr0Item item : this.keySet()) {
			if (item.equals( (Lr0Item) key))
			  return true;
		}
		return false;
	}
		
	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof Lr0Closure))
			return false;
		
		
		Lr0Closure theOtherClosure = (Lr0Closure) theOtherObject;
		
		if ( theOtherClosure.size() != this.size())
			return false;
		
		if ( !theOtherClosure.keySet().equals( this.keySet()))
			return false;
		
		for ( Lr0Item thisItem : this.keySet()) {
			for ( Lr0Item theOtherItem : theOtherClosure.keySet()) {
				
				if ( thisItem.equals(theOtherItem) && 
					 !this.get( thisItem).equals( theOtherClosure.get( theOtherItem)))			
				return false;
		    }
		}
		
		// theOtherClosure equals this 
		if ( this.getNumber() == -1)
			this.setNumber(  theOtherClosure.getNumber()); 
		else if ( theOtherClosure.getNumber() == -1)
			theOtherClosure.setNumber( this.getNumber());
			
		
		return true;
	}


	
}

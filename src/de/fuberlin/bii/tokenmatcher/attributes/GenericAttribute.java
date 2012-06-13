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

package de.fuberlin.bii.tokenmatcher.attributes;

/**
 * Generische Basisklasse für Attribute, deren Wert 
 * bereits in der Tokendefinitionsdatei angeben ist.
 * 
 * @author Johannes Dahlke
 *
 * @param <T>
 */
public class GenericAttribute<T> implements Attribute {

	private final T value;
	private final Class<T> valueType;
	
	public GenericAttribute( Class<T> valueType, T value) {
	  super();
	  this.valueType = valueType;
	  this.value = value;
	}
	
	public Object lexemToValue( String lexem) {
		// ignore lexem
	  return value;	
	}

	public Class getValueType() {
		return valueType;
	}

	@Override
	public String toString() {
		return value.toString();
	}
	
}

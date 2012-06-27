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

package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar;

import java.io.Serializable;

import de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree.RegularExpressionElement;
import de.fuberlin.bii.tokenmatcher.StatePayload;
import de.fuberlin.bii.utils.Test;

/**
 * Symbol kapselt Value-Objekte und bietet die Möglichkeit einen {@link StatePayload Payload} an diese zu binden.
 * Symbol-Objekte werden an {@link Terminal}-Objekte übergeben.
 * Symbol ist Basisklasse für {@link RegularExpressionElement}.
 * 
 * @author Johannes Dahlke
 *
 * @param <Value>
 * @param <Payload>
 */
public class Symbol<Value extends Comparable<Value>, Payload> implements Comparable<Symbol<Value,Payload>>, Serializable{
	
	private static final long serialVersionUID = -6760212067935299676L;

private Value value;
	
	private Payload payload;

	public Symbol( Value value) {
		this( value, null);
	}
	
	public Symbol( Value value, Payload payload) {
		super();
		this.value = value;
		this.payload = payload;
	}
	
	public int compareTo( Symbol<Value,Payload> theOtherSymbol) {
		return this.value.compareTo( theOtherSymbol.value);
	}
	
  @Override
  public String toString() {
  	String result = "( " + value.toString();
  	result += Test.isAssigned( payload) ? ", " + payload.toString() +")": ")";
  	return result;
  }
  
  public boolean equalsTotally( Object obj) {
  	if ( !equals( obj))
  		return false;
  
  	Symbol<Value,Payload> theOtherSymbol = (Symbol<Value,Payload>) obj;
  	
  	return Test.isAssigned( this.payload) 
  			? this.payload.equals( theOtherSymbol.payload)
  			:	Test.isUnassigned( theOtherSymbol.payload);
  }

  
  @Override
  public boolean equals( Object obj) {
  	if ( Test.isUnassigned( obj))
  		return false;
  	
  	if ( !( obj instanceof Symbol))
  		return false;
  	
  	Symbol theOtherSymbol = (Symbol) obj;
  	
  	return this.value.equals( theOtherSymbol.value);
  }
  
  @Override
  public int hashCode() {
  	int hashCode = 5;
    hashCode = 37 * hashCode + (Test.isAssigned( value) ? value.hashCode() : 0);
    //hashCode = 37 * hashCode + (Test.isAssigned( payload) ? payload.hashCode() : 0);
    return hashCode;
  }

  
	
	public Value getValue() {
		return value;
	}
	
	public Payload getPayload() {
		return payload;
	}
	
	
	public void setPayload( Payload payload) {
		this.payload = payload;
	}
  
}

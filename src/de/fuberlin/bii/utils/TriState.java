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


package de.fuberlin.bii.utils;


/**
 * Erweiterung des boolschen Typs um einen undefinierten Wert. 
 *
 * @author Johannes Dahlke
 *
 */
public enum TriState {

	FALSE,
	TRUE,
	AMBIGUOUS;
	
  public boolean isAmbiguous() {
  	return this == AMBIGUOUS;
  }
  
  public boolean isObvious() {
  	return this != AMBIGUOUS;  	
  }
  
  public boolean isTrue() {
  	return this == TRUE;
  }
  
  public boolean isntTrue() {
  	return this != TRUE;
  }
  
  public boolean isFalse() {
  	return this == FALSE;
  }
  
  public boolean isntFalse() {
  	return this != FALSE;
  }  
  
  @Override
  public String toString() {
  	switch (this) {
  		case FALSE : return "false";
  		case TRUE : return "true";
  		default: return "ambiguous";
  	}
  }
  
	
}

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

import java.util.HashSet;

import de.fuberlin.bii.utils.Test;

/**
 * TerminalSet verkörpert eine Menge von {@link Terminal}-Objekten.
 *
 * @author Johannes Dahlke
 *
 */
public class TerminalSet extends HashSet<Terminal> {
	
  @Override
  public boolean contains( Object obj) {
  	
  	if ( Test.isUnassigned( obj))
  		return false;
  	
  	if ( !( obj instanceof Terminal))
  		return false;
  	
  	Terminal lookupTerminal = (Terminal) obj;
  	
  	for ( Terminal terminalOfSet : this) {
			if ( terminalOfSet.equals( lookupTerminal))
				return true;
		}
  	
  	return false;
  }
  
  
  @Override
  public boolean remove( Object obj) {
  	
  	if ( Test.isUnassigned( obj))
  		return false;
  	

  	if ( !( obj instanceof Terminal))
  		return false;
  	
  	Terminal lookupTerminal = (Terminal) obj;
  	
  	for ( Terminal terminalOfSet : this) {
			if ( terminalOfSet.equals( lookupTerminal))
				return super.remove( terminalOfSet);
		}
  	
  	return false;
  }
  
  
  @Override
  public boolean add( Terminal e) {
  	if ( !contains( e))
    	return super.add( e);
  	else
  		return false;
  }
  
	
}

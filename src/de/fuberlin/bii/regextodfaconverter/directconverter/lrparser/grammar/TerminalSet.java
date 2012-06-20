package de.fuberlin.bii.regextodfaconverter.directconverter.lrparser.grammar;

import java.util.HashSet;

import de.fuberlin.bii.utils.Test;


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
	
}

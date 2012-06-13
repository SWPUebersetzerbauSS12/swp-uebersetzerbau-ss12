package de.fuberlin.bii.regextodfaconverter.directconverter.lr0parser.grammar;

import de.fuberlin.bii.utils.Test;


public class Symbol<Value extends Comparable<Value>, Payload> implements Comparable<Symbol<Value,Payload>>{
	
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

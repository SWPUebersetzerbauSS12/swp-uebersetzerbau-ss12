package de.fuberlin.bii.utils;


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

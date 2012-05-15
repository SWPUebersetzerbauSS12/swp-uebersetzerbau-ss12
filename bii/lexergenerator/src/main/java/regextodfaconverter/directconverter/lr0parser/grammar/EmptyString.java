package regextodfaconverter.directconverter.lr0parser.grammar;

import utils.Test;


public class EmptyString extends Terminal  {
	
	
	public EmptyString() {
		super("ɛ");
	}
	
	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof EmptyString))
			return false;
		
		return true;
	}
	

}

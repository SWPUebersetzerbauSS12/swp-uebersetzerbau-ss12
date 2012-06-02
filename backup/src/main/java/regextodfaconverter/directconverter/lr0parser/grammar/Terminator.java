package regextodfaconverter.directconverter.lr0parser.grammar;

import utils.Test;


public class Terminator extends Terminal  {
	
	
	public Terminator() {
		super('$');
	}
	
	@Override
	public boolean equals( Object theOtherObject) {
		
		if ( Test.isUnassigned( theOtherObject))
			return false;
		
		if ( !( theOtherObject instanceof Terminator))
			return false;
		
		return true;
	}
	

}

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


package regextodfaconverter.directconverter.regex.operatortree;

/**
 * 
 * @author Johannes Dahlke
 *
 */
public class RepetitionRange {

	private static final Integer MIN_VALUE = 0;
	private static final Integer MAX_VALUE = Integer.MAX_VALUE;
	
	
	private Integer lowerBound = MIN_VALUE;
	private Integer upperBound = MAX_VALUE;
	 
	
	public RepetitionRange() {
		super();
	}
	
	public RepetitionRange( int lowerBound, int upperBound) {
		super();
		setLowerBound( lowerBound);
		setUpperBound( upperBound);
	}
	
	private int ensureValidRangeValue( Integer value) {
		if ( value < MIN_VALUE)
			value = MIN_VALUE;
		if ( value > MAX_VALUE)
			value = MAX_VALUE;
		return value;
	}
	
	public void setLowerBound( int lowerBound) {
		this.lowerBound = ensureValidRangeValue( lowerBound);
	}
	
	
	public void setUpperBound( int upperBound) {
		this.upperBound = ensureValidRangeValue( upperBound);
	}
	
	
	public int getLowerBound() {
		return lowerBound;
	}
	
	
	public int getUpperBound() {
		return upperBound;
	}
	
	@Override
	public String toString() {
		return "[" + lowerBound.toString() + ".." + upperBound.toString()+"]";
	}
	
}

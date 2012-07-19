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


package de.fuberlin.bii.regextodfaconverter.directconverter.regex.operatortree;

import junit.framework.Assert;

import org.junit.Test;


/**
 * Testet der {@link RegexOperatorTree Operatorbaum} zum matchen von Eingaben entsprechend gegebener regulärer Ausdrücke.
 * 
 * @author Johannes Dahlke
 *
 */
public class RegexOperatorTreeTest {

	@SuppressWarnings({ "rawtypes", "unchecked", "static-method"})
	@Test
	public void test() {

		String regex = "(a|b)*abb";
		int regexLength = regex.length();

		RegularExpressionElement[] regularExpression = new RegularExpressionElement[regexLength];
		for ( int i = 0; i < regexLength; i++) {
			regularExpression[i] = new RegularExpressionElement( regex.charAt( i), null);
		}

		RegexOperatorTree regexTree = null;
		Boolean noExceptionsOccur = true;
		try {
			regexTree = new RegexOperatorTree( regularExpression);
		} catch ( Exception e) {
			noExceptionsOccur = false;
		}
		Assert.assertTrue( noExceptionsOccur);
		
		System.out.println( regexTree);
	}

}

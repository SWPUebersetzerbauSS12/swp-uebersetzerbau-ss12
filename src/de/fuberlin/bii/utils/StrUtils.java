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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;




public class StrUtils {

	 
		public static List<String> split( String s, char c) {
			int beginIndex = 0;
			int  endIndex;
			ArrayList<String>  findings = new ArrayList<String>();
			while ( ( endIndex = s.indexOf( c, beginIndex)) > -1) {
				findings.add( s.substring( beginIndex, endIndex));
				beginIndex = endIndex +1;
			}
			if ( s.length() > 0)
				findings.add( s.substring( beginIndex, s.length()));
			return findings;
		}
		
		
		
		
		public static String join( String[] strings, String seperator) {
			String result = "";
			for ( String string : strings) {
				if ( result.length() > 0)
					result += seperator;
				result += string; 
			}
			return result;
		}
		
		public static String join( String[] strings) {
			return join( strings, "");
		}
		
		public static String join( List<String> strings, String seperator) {
			String[] stringArray = new String[strings.size()];
			return join( strings.toArray( stringArray));
		}


		public static String join( List<String> strings) {
			return join( strings, "");
		}

		
		
		
		
		public static String concat( String delimiter, List<String> stringList) {
			return concat(delimiter, stringList.toArray( new String[ stringList.size()]));
		}
			
		public static String concat( String delimiter, String ...strings) {
			if ( strings.length > 0) {
				String result = strings[0];
				for (int i = 1; i < strings.length; i++) {
					result += delimiter + strings[i];
				}
				return result;
			} else return "";
		}

	

	/**
	 * Konvertiert eine Collection in einen String, indem die String-Entsprechungen aller Elemente der 
	 * Liste über die eigene toString-Funktion ermittelt und durch den Separator getrennt in einen 
	 * String aneinander gereiht werden.  
	 * @param collection
	 * @param seperator
	 * @return
	 */
	public static String collectionToString( Collection <? extends Object> collection, String seperator) {
		String result = ""; 
		for ( Iterator<? extends Object> iterator = collection.iterator(); iterator.hasNext();) {
			String string = iterator.next().toString();
      if ( result.length() == 0)
      	result += string; else
      	result += seperator + string;	
		}
		return result;
	}
	
	
	/**
	 * Test, if a string occur in an array
	 * @param needle the string that should be find in the haystack
	 * @param haystack the set of strings we want to know if the needle is in there
	 * @return
	 */
	public static boolean inArray(String needle, String ... haystack) {
		for (String string : haystack) {
			if ( string.equals( needle)) 
			  return true;
		}
		return false;
	}
	
	
	public static String[] arrayFromString( String stringRepresentationOfArray) {
		String s = stringRepresentationOfArray;
		s = s.substring( 1, s.length() -1);
    return s.split( ", ");
	}
	
	
	
	

}

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.SysexMessage;

/**
 * Verarbeitet Kommandozeilenparameter und bereitet diese zur weiteren Verwendung auf.
 * 
 * @author Johannes Dahlke
 *
 */
public class ApplicationArgumentProcessor {

	private HashSet<ApplicationArgument> validArguments = new HashSet<ApplicationArgument>();
	
	public ApplicationArgumentProcessor( ApplicationArgument ...arguments) throws InvalidArgumentException {
		super();
		for ( ApplicationArgument argument : arguments) {		
			validArguments.add( argument);	
		}
	}
	

	private static List<String> splitArgumentString( String argumentString) 
	{ 
		List<String> result = new ArrayList<String>();
		List<MatchResult> matchResults = new ArrayList<MatchResult>(); 
	 
		String pattern = "(^-|\\s-)[\\w\\d]+(\\s\"[^\"]*\"|\\s[^-][\\S]+)*|--[\\w\\d]+(\\s\"[^\"]*\"|\\s+(?!-)[\\S]+)*"; 
	  
	  for ( Matcher matcher = Pattern.compile(pattern).matcher(argumentString); matcher.find();) 
	    matchResults.add( matcher.toMatchResult() ); 
	 
	  for ( MatchResult matchResult : matchResults) {
	  	result.add( matchResult.group().trim());
    }
	  
	  if ( result.size() == 0) 
	  	result.add( argumentString);
	  	
	  return result; 
	}
	

	
	public void processArguments( String[] args) {
		
		String joinedArguments = StrUtils.join( args, " ");
		// split long argument names
		List<String> givenArgumentStrings = splitArgumentString( joinedArguments);
		Boolean givenArgumentAccepted = false;
		for ( String givenArgumentString : givenArgumentStrings) {
			for ( ApplicationArgument validArgument : validArguments) {
				if ( validArgument.isResposibleForArgumentString( givenArgumentString)) {
					validArgument.loadFromArgumentString( givenArgumentString);
					givenArgumentAccepted = true;
					break;
				}
				givenArgumentAccepted = false;
			}
			if ( !givenArgumentAccepted)
        System.err.println( String.format( "Argument '%s' not accepted.", givenArgumentString));   
		}
		
	}
	

}

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Datensatz eines Argumentes.
 * 
 * @author Johannes Dahlke
 *
 */
public class ApplicationArgument {

	private String shortArgumentName;
	private String longArgumentName;
	
	private boolean keyValueParams = false;
	
	private List<String> paramList = new ArrayList<String>();
	private HashMap<String, String> paramMap = new HashMap<String, String>();
	
	
	public ApplicationArgument( String shortArgumentName, String longArgumentName) {
		this( shortArgumentName, longArgumentName, false);
	}

	public ApplicationArgument( String shortArgumentName, String longArgumentName, boolean expectKeyValueParams) {
		super();
		this.longArgumentName = longArgumentName;
		this.shortArgumentName = shortArgumentName;
		this.keyValueParams = expectKeyValueParams;
	}

	
	private String getMatchString() {
		if ( keyValueParams)
			return String.format( "^(-%s|--%s)(\\s+[\\w\\d]+=(\"[^\"]*\"|[\\S]+))*", shortArgumentName, longArgumentName);
		else
			return String.format( "^(-%s|--%s)(\\s+[\\S]+)*", shortArgumentName, longArgumentName);
	}
	
	public boolean isResposibleForArgumentString( String givenArgumentString) {
		return givenArgumentString.matches( getMatchString());
	}

	public void loadFromArgumentString( String givenArgumentString) {
		extractParams( givenArgumentString);	
		execute();
	}

	protected void execute() {
		// implement in descendant classes
	}

	private void extractParams( String givenArgumentString) {
		if ( keyValueParams)
			fillParamMap( givenArgumentString);
		else 
			fillParamList( givenArgumentString);	
	}
	
	private void fillParamList( String argumentString) {
		
		paramList.clear();
		
		List<MatchResult> matchResults = new ArrayList<MatchResult>(); 
	 
		String pattern = "(\\s([\\S]+|\"[^\"]*\"))";
	  for ( Matcher matcher = Pattern.compile(pattern).matcher(argumentString); matcher.find();) 
	    matchResults.add( matcher.toMatchResult() ); 
	 
	  for ( MatchResult matchResult : matchResults) {
	  	String resultString = matchResult.group().trim();
	  	if ( acceptParam( resultString))
	  	  paramList.add( resultString);
	  	else
	    	System.err.println( String.format( "Parameter '%s' of argument '%s' not accepted.", resultString, getName()));   		
	  }	
	}

	private void fillParamMap( String argumentString) { 
		
		paramMap.clear();
		
		List<MatchResult> matchResults = new ArrayList<MatchResult>(); 
	 
		String pattern = "(\\s[\\w\\d]+=(\"[^\"]*\"|[\\S]+))";
	  
	  for ( Matcher matcher = Pattern.compile(pattern).matcher(argumentString); matcher.find();) 
	    matchResults.add( matcher.toMatchResult() ); 
	 
	  for ( MatchResult matchResult : matchResults) { 
	  	String resultString = matchResult.group().trim();
	  	String[] keyValue = resultString.split( "=");
	  	if ( keyValue.length > 1) {
	  		if ( acceptKeyValue( keyValue[0], keyValue[1]))
	  	    paramMap.put( keyValue[0], keyValue[1]); 
	  	  else
	  	  	System.err.println( String.format( "Key-value parameter '%s' of argument '%s' not accepted.", resultString, getName()));  	
	  	} else {
	  		System.err.println( String.format( "Invalid parameter '%s' of argument '%s'.", resultString, getName()));
	  	}
	  }
	}
	
	protected boolean acceptKeyValue( String key, String value) {
		return true;
	}
	

	protected boolean acceptParam( String param) {
		return false;
	}
	
	
	protected List<String> getParamList() {
		return paramList;
	}
	
	
	protected HashMap<String, String> getParamMap() {
		return paramMap;
	}
	
	private String getName() {
		if ( Test.isAssigned( longArgumentName) &&
		   ( longArgumentName.length() > 0))
		  return longArgumentName;
		else
			return shortArgumentName;
	}
	
	

}

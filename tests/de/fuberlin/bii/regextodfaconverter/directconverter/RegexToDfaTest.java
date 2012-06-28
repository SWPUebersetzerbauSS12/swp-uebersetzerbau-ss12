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

package de.fuberlin.bii.regextodfaconverter.directconverter;

import org.junit.Assert;
import org.junit.Test;

import de.fuberlin.bii.bufferedreader.BufferedLexemeReader;
import de.fuberlin.bii.bufferedreader.LexemeReader;
import de.fuberlin.bii.regextodfaconverter.MinimalDfa;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.RegexToDfaConverter;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.RegexToPayloadMap;
import de.fuberlin.bii.regextodfaconverter.fsm.FiniteStateMachine;
import de.fuberlin.bii.regextodfaconverter.fsm.StatePayload;
import de.fuberlin.bii.tokenmatcher.LexemIdentificationException;
import de.fuberlin.bii.tokenmatcher.Token;
import de.fuberlin.bii.tokenmatcher.Tokenizer;
import de.fuberlin.bii.tokenmatcher.attributes.ParseIntAttribute;
import de.fuberlin.bii.tokenmatcher.attributes.ParseStringAttribute;
import de.fuberlin.bii.tokenmatcher.attributes.StringAttribute;
import de.fuberlin.bii.utils.Notification;

/**
 * 
 * Testet die Erkennung von Token mittels eines Dfa's, der direkt als DFA aufgebaut wurde.
 * 
 * @author Johannes Dahlke
 *
 */
public class RegexToDfaTest {

	@Test
	public void testDeterminism() throws Exception {
		
		Notification.enableDebugPrinting();
		Notification.enableDebugInfoPrinting();
		
		RegexToPayloadMap<StatePayload> regexToPayloadMap = new RegexToPayloadMap<StatePayload>();
		regexToPayloadMap.put( "(a|b)*abb", new StatePayload( "NUM", new ParseIntAttribute()));
	 // regexToPayloadMap.put( "c(1|2)*3", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "OP", new ParseStringAttribute()));
		
		
		FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> fsm = new RegexToDfaConverter()
		.convert( regexToPayloadMap);

		LexemeReader lexemeReader = new BufferedLexemeReader("tests/resources/de/fuberlin/bii/source/tokenmatcher/regex.fun");// new SimpleLexemeReader(
		Notification.printDebugInfoMessage( fsm.toString());
		Notification.printDebugInfoMessage( (new MinimalDfa( fsm)).toString());
		Notification.printDebugInfoMessage( "Deterministic: " + fsm.isDeterministic());

		Assert.assertTrue( fsm.isDeterministic());		
		
	}
	
	@Test
	public void testSubTokenRecognition() throws Exception {
		
		Notification.enableDebugPrinting();
		Notification.enableDebugInfoPrinting();
		
		
		RegexToPayloadMap<StatePayload> regexToPayloadMap = new RegexToPayloadMap<StatePayload>();
		regexToPayloadMap.put( "def", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "SYM", new StringAttribute( "DEF"), 2));
		regexToPayloadMap.put( "if", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "SYM", new StringAttribute( "IF"), 1));
		regexToPayloadMap.put( "[ifdef]*", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "ID", new ParseStringAttribute(),0));
				
		FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> fsm = new RegexToDfaConverter()
		.convert( regexToPayloadMap);

		LexemeReader lexemeReader = new BufferedLexemeReader("tests/resources/de/fuberlin/bii/source/tokenmatcher/regex2.fun");// new SimpleLexemeReader(
	
		Notification.printDebugInfoMessage( fsm.toString());
		Notification.printDebugInfoMessage( (new MinimalDfa( fsm)).toString());
		Notification.printDebugInfoMessage( "Deterministic: " + fsm.isDeterministic());
		
		Tokenizer tokenizer = new Tokenizer( lexemeReader, new MinimalDfa( fsm));
	
		Token currentToken = null;
		
		Boolean tokenIdentificationFailed = false;
		
		// recognize symbol if
		currentToken = tokenizer.getNextToken();
		Assert.assertEquals( "<SYM, IF>",currentToken.toString());

		// recognize identifier ide
		currentToken = tokenizer.getNextToken();
		Assert.assertEquals( "<ID, ide>",currentToken.toString());

		// recognize symbol def
		currentToken = tokenizer.getNextToken();
		Assert.assertEquals( "<SYM, DEF>",currentToken.toString());



	}


	@Test
	public void testTokenRecognition() throws Exception {
		
		Notification.enableDebugPrinting();
		Notification.enableDebugInfoPrinting();
		
		RegexToPayloadMap<StatePayload> regexToPayloadMap = new RegexToPayloadMap<StatePayload>();
		regexToPayloadMap.put( "(1|2)*3", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "NUM", new ParseIntAttribute(), 2));
		regexToPayloadMap.put( "c[12]{0,}3", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "ID", new ParseStringAttribute(), 1));
		regexToPayloadMap.put( "[1-5-A]", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "CHAR", new ParseStringAttribute(),0));
				
		FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> fsm = new RegexToDfaConverter()
		.convert( regexToPayloadMap);

		LexemeReader lexemeReader = new BufferedLexemeReader("tests/resources/de/fuberlin/bii/source/tokenmatcher/regex.fun");// new SimpleLexemeReader(
	
		Notification.printDebugInfoMessage( fsm.toString());
		Notification.printDebugInfoMessage( (new MinimalDfa( fsm)).toString());
		Notification.printDebugInfoMessage( "Deterministic: " + fsm.isDeterministic());
		
		Tokenizer tokenizer = new Tokenizer( lexemeReader, new MinimalDfa( fsm));
	
		Token currentToken = null;
		
		Boolean tokenIdentificationFailed = false;
		// do not match first "c"
		try {
		 tokenizer.getNextToken();
		} catch (LexemIdentificationException e) {
			tokenIdentificationFailed = true;
		}
		Assert.assertTrue( tokenIdentificationFailed);
		
		// recognize number 123
		currentToken = tokenizer.getNextToken();
		Assert.assertEquals( "<NUM, 123>",currentToken.toString());
		// it must be an integer
		Assert.assertTrue( currentToken.getAttribute() instanceof Integer);
		
	  // recognize string c123
		currentToken = tokenizer.getNextToken();
		Assert.assertEquals( "<ID, c123>",currentToken.toString());
		// it must be a string
		Assert.assertTrue( currentToken.getAttribute() instanceof String);

		// do not match "b"
		try {
			tokenizer.getNextToken();
		} catch (LexemIdentificationException e) {
			tokenIdentificationFailed = true;
		}
		Assert.assertTrue( tokenIdentificationFailed);

		// recognize string c3
		currentToken = tokenizer.getNextToken();
		Assert.assertEquals( "<ID, c3>",currentToken.toString());
		// it must be a string
		Assert.assertTrue( currentToken.getAttribute() instanceof String);

	  // recognize string 5
		currentToken = tokenizer.getNextToken();
		Assert.assertEquals( "<CHAR, 5>",currentToken.toString());
	  // recognize string -
		currentToken = tokenizer.getNextToken();
		Assert.assertEquals( "<CHAR, ->",currentToken.toString());
	  // recognize string A
		currentToken = tokenizer.getNextToken();
		Assert.assertEquals( "<CHAR, A>",currentToken.toString());


		
	}






}

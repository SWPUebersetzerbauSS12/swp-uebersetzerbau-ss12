package de.fuberlin.bii.regextodfaconverter.directconverter;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import bufferedreader.BufferedLexemeReader;
import bufferedreader.LexemeReader;

import regextodfaconverter.MinimalDfa;
import regextodfaconverter.directconverter.regex.RegexSpecialChars;
import regextodfaconverter.directconverter.regex.RegexToDfaConverter;
import regextodfaconverter.directconverter.regex.RegexToPayloadMap;
import regextodfaconverter.directconverter.regex.operatortree.RegularExpressionElement;
import regextodfaconverter.fsm.FiniteStateMachine;
import regextodfaconverter.fsm.StatePayload;
import tokenmatcher.LexemIdentificationException;
import tokenmatcher.Token;
import tokenmatcher.Tokenizer;
import utils.Notification;
import tokenmatcher.attributes.ParseIntAttribute;
import tokenmatcher.attributes.ParseStringAttribute;
import tokenmatcher.attributes.StringAttribute;


public class RegexToDfaTest {

	@Test
	public void testDeterminism() throws Exception {
		
		Notification.enableDebugPrinting();
		
		RegexToPayloadMap<tokenmatcher.StatePayload> regexToPayloadMap = new RegexToPayloadMap<tokenmatcher.StatePayload>();
		regexToPayloadMap.put( "(1|2)*3", new regextodfaconverter.fsm.StatePayload( "NUM", new ParseIntAttribute()));
		regexToPayloadMap.put( "c(1|2)*3", new regextodfaconverter.fsm.StatePayload( "OP", new ParseStringAttribute()));
				
		FiniteStateMachine<Character, ? extends tokenmatcher.StatePayload> fsm = new RegexToDfaConverter()
		.convert( regexToPayloadMap);

		LexemeReader lexemeReader = new BufferedLexemeReader("src/test/resources/source/tokenmatcher/regex.fun");// new SimpleLexemeReader(
		
		// TODO @Daniel: fsm.isDeterministic() ist hier false, da fsm.addTransition() auch gleiche Übergänge mehrfach aufnimmt. Benötige Funktionalität, mit der ich das Vorhandensein eines Übergangs abfragen kann, bevor ich den Übergang ggf. dann hinzufüge.
		Assert.assertTrue( fsm.isDeterministic());		
	}
	
	
	@Test
	public void testTokenRecognition() throws Exception {
		
		Notification.enableDebugPrinting();
		
		RegexToPayloadMap<tokenmatcher.StatePayload> regexToPayloadMap = new RegexToPayloadMap<tokenmatcher.StatePayload>();
		regexToPayloadMap.put( "(1|2)*3", new regextodfaconverter.fsm.StatePayload( "NUM", new ParseIntAttribute()));
		regexToPayloadMap.put( "c(1|2)*3", new regextodfaconverter.fsm.StatePayload( "ID", new ParseStringAttribute()));
				
		FiniteStateMachine<Character, ? extends tokenmatcher.StatePayload> fsm = new RegexToDfaConverter()
		.convert( regexToPayloadMap);

		LexemeReader lexemeReader = new BufferedLexemeReader("src/test/resources/source/tokenmatcher/regex.fun");// new SimpleLexemeReader(
	
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

	}






}

package de.fuberlin.bii.regextodfaconverter.directconverter;

import org.junit.Assert;
import org.junit.Test;

import de.fuberlin.bii.bufferedreader.BufferedLexemeReader;
import de.fuberlin.bii.bufferedreader.LexemeReader;
import de.fuberlin.bii.regextodfaconverter.MinimalDfa;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.RegexToDfaConverter;
import de.fuberlin.bii.regextodfaconverter.directconverter.regex.RegexToPayloadMap;
import de.fuberlin.bii.regextodfaconverter.fsm.FiniteStateMachine;
import de.fuberlin.bii.tokenmatcher.LexemIdentificationException;
import de.fuberlin.bii.tokenmatcher.Token;
import de.fuberlin.bii.tokenmatcher.Tokenizer;
import de.fuberlin.bii.tokenmatcher.attributes.ParseIntAttribute;
import de.fuberlin.bii.tokenmatcher.attributes.ParseStringAttribute;
import de.fuberlin.bii.utils.Notification;

public class RegexToDfaTest {

	@Test
	public void testDeterminism() throws Exception {
		
		Notification.enableDebugPrinting();
		
		RegexToPayloadMap<de.fuberlin.bii.tokenmatcher.StatePayload> regexToPayloadMap = new RegexToPayloadMap<de.fuberlin.bii.tokenmatcher.StatePayload>();
		regexToPayloadMap.put( "(1|2)*3", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "NUM", new ParseIntAttribute()));
		regexToPayloadMap.put( "c(1|2)*3", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "OP", new ParseStringAttribute()));
				
		FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> fsm = new RegexToDfaConverter()
		.convert( regexToPayloadMap);

		LexemeReader lexemeReader = new BufferedLexemeReader("tests/resources/de/fuberlin/bii/source/tokenmatcher/regex.fun");// new SimpleLexemeReader(
		
		Assert.assertTrue( fsm.isDeterministic());		
	}
	
	
	@Test
	public void testTokenRecognition() throws Exception {
		
		Notification.enableDebugPrinting();
		
		RegexToPayloadMap<de.fuberlin.bii.tokenmatcher.StatePayload> regexToPayloadMap = new RegexToPayloadMap<de.fuberlin.bii.tokenmatcher.StatePayload>();
		regexToPayloadMap.put( "(1|2)*3", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "NUM", new ParseIntAttribute()));
		regexToPayloadMap.put( "c(1|2)*3", new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload( "ID", new ParseStringAttribute()));
				
		FiniteStateMachine<Character, ? extends de.fuberlin.bii.tokenmatcher.StatePayload> fsm = new RegexToDfaConverter()
		.convert( regexToPayloadMap);

		LexemeReader lexemeReader = new BufferedLexemeReader("tests/resources/de/fuberlin/bii/source/tokenmatcher/regex.fun");// new SimpleLexemeReader(
	
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

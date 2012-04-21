package lexergen;

import java.io.IOException;

import bufferedreader.LexemeReader;
import bufferedreader.SimpleLexemeReader;
import regextodfaconverter.ConvertExecption;
import regextodfaconverter.DfaMinimizer;
import regextodfaconverter.MinimalDfa;
import regextodfaconverter.TokenType;
import regextodfaconverter.fsm.FiniteStateMachine;
import tokenmatcher.StatePayload;
import tokenmatcher.Token;
import tokenmatcher.Tokenizer;
import utils.Notification;

/**
 * Hello world!
 *
 */
public class Lexer 
{
    public static void main( String[] args ) throws ConvertExecption, Exception
    {
    	
  		Notification.enableDebugPrinting();
  		
        System.out.println( "Hello World!" );
        System.out.println( "Hallo Welt!" );

        
      FiniteStateMachine<Character,StatePayload> fsm = new FiniteStateMachine<Character, StatePayload>();  
      
      LexemeReader lexemeReader = new SimpleLexemeReader( "test.fun");  
      Tokenizer tokenizer = new Tokenizer( lexemeReader, new MinimalDfa<Character, StatePayload>( fsm));
      
      Token currentToken;
      while ( true) {
      	currentToken = tokenizer.getNextToken();
      	System.out.println( currentToken.lexem);
      }
    }
}

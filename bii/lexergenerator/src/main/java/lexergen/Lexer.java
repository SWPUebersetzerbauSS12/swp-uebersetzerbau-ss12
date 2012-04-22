package lexergen;

import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import bufferedreader.LexemeReader;
import bufferedreader.SimpleLexemeReader;
import regextodfaconverter.ConvertExecption;
import regextodfaconverter.DfaMinimizer;
import regextodfaconverter.MinimalDfa;
import regextodfaconverter.fsm.FiniteStateMachine;
import regextodfaconverter.fsm.State;
import tokenmatcher.StatePayload;
import tokenmatcher.Token;
import tokenmatcher.TokenType;
import tokenmatcher.Tokenizer;
import utils.Notification;


/**
 * Hello world!
 * 
 */
public class Lexer {

	/**
	 * Erstellt einen Automaten für Wörter, die gültige Zahlen darstellen.
	 * 
	 * @return Ein endlicher Automat der die Wörter
	 *         (1|2|3|4|5|6|7|8|9)(0|1|2|3|4|5|6|7|8|9)* erkennt.
	 */
	public static FiniteStateMachine<Character, TokenType> generateNumberFSM() {
		FiniteStateMachine<Character, TokenType> fsm = new FiniteStateMachine<Character, TokenType>();

		try {
			State<Character, TokenType> state1;
			State<Character, TokenType> state2;

			state1 = fsm.getCurrentState();
			state2 = new State<Character, TokenType>( TokenType.NUMBER, true);

			fsm.addTransition( state1, state2, '1');
			fsm.addTransition( state1, state2, '2');
			fsm.addTransition( state1, state2, '3');
			fsm.addTransition( state1, state2, '4');
			fsm.addTransition( state1, state2, '5');
			fsm.addTransition( state1, state2, '6');
			fsm.addTransition( state1, state2, '7');
			fsm.addTransition( state1, state2, '8');
			fsm.addTransition( state1, state2, '9');

			fsm.addTransition( state2, state2, '0');
			fsm.addTransition( state2, state2, '1');
			fsm.addTransition( state2, state2, '2');
			fsm.addTransition( state2, state2, '3');
			fsm.addTransition( state2, state2, '4');
			fsm.addTransition( state2, state2, '5');
			fsm.addTransition( state2, state2, '6');
			fsm.addTransition( state2, state2, '7');
			fsm.addTransition( state2, state2, '8');
			fsm.addTransition( state2, state2, '9');

		} catch ( Exception e) {
			e.printStackTrace();
		}

		return fsm;
	}


	/**
	 * Erstellt einen Automaten für Wörter, die Wörter darstellen.
	 * 
	 * @return Ein endlicher Automat der die Wörter
	 *         (a|b|c|...|z|A|B|C|...|Z)(a|b|c|...|z|A|B|C|...|Z)* erkennt.
	 */
	public static FiniteStateMachine<Character, StatePayload> generateWordFSM() {
		FiniteStateMachine<Character, StatePayload> fsm = new FiniteStateMachine<Character, StatePayload>();

		try {
			State<Character, StatePayload> state1;
			State<Character, StatePayload> state2;

			state1 = fsm.getCurrentState();
			state2 = new State<Character, StatePayload>( 
					new regextodfaconverter.fsm.StatePayload( TokenType.WORD, 1), true);
			

			ArrayList<Character> validChars = new ArrayList<Character>(); 
			for ( char c = 'a'; c <= 'z'; c++) {
				validChars.add( c);
			}
			for ( char c = 'A'; c <= 'Z'; c++) {
				validChars.add( c);
			}
			
			for ( Character c : validChars) {
				fsm.addTransition( state1, state1, c);	
			}
		
			for ( char c = 0x00; c < 0xFF; c++) {
				if ( !validChars.contains( c))
				  fsm.addTransition( state1, state2, c);
			}

		} catch ( Exception e) {
			e.printStackTrace();
		}

		return fsm;
	}


	public static void main( String[] args) throws ConvertExecption, Exception {

		Notification.enableDebugPrinting();

		System.out.println( "Hello World!");
		System.out.println( "Hallo Welt!");

		FiniteStateMachine<Character, StatePayload> fsm = generateWordFSM();
		
		LexemeReader lexemeReader = new SimpleLexemeReader( "testwords.fun");
		Tokenizer tokenizer = new Tokenizer( lexemeReader,
				new MinimalDfa<Character, StatePayload>( fsm));

		Token currentToken;
		while ( true) {
			currentToken = tokenizer.getNextToken();
			System.out.println( "s");
			System.out.println( currentToken.lexem);
		}
	}
}

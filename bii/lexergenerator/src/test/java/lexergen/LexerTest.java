package lexergen;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import regextodfaconverter.ConvertExecption;
import regextodfaconverter.MinimalDfa;
import regextodfaconverter.NfaToDfaConverter;
import regextodfaconverter.fsm.FiniteStateMachine;
import regextodfaconverter.fsm.State;
import tokenmatcher.StatePayload;
import tokenmatcher.Token;
import tokenmatcher.TokenType;
import tokenmatcher.Tokenizer;
import bufferedreader.BufferedLexemeReader;
import bufferedreader.LexemeReader;

public class LexerTest {

	@Test
	public void test() throws ConvertExecption, Exception {
		runTest();
	}

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
			state2 = new State<Character, TokenType>(TokenType.INT, true);

			fsm.addTransition(state1, state2, '1');
			fsm.addTransition(state1, state2, '2');
			fsm.addTransition(state1, state2, '3');
			fsm.addTransition(state1, state2, '4');
			fsm.addTransition(state1, state2, '5');
			fsm.addTransition(state1, state2, '6');
			fsm.addTransition(state1, state2, '7');
			fsm.addTransition(state1, state2, '8');
			fsm.addTransition(state1, state2, '9');

			fsm.addTransition(state2, state2, '0');
			fsm.addTransition(state2, state2, '1');
			fsm.addTransition(state2, state2, '2');
			fsm.addTransition(state2, state2, '3');
			fsm.addTransition(state2, state2, '4');
			fsm.addTransition(state2, state2, '5');
			fsm.addTransition(state2, state2, '6');
			fsm.addTransition(state2, state2, '7');
			fsm.addTransition(state2, state2, '8');
			fsm.addTransition(state2, state2, '9');

		} catch (Exception e) {
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
					new regextodfaconverter.fsm.StatePayload("ID",
							"parseString()"), true);

			ArrayList<Character> validChars = new ArrayList<Character>();
			for (char c = 'a'; c <= 'z'; c++) {
				validChars.add(c);
			}
			for (char c = 'A'; c <= 'Z'; c++) {
				validChars.add(c);
			}

			for (Character c : validChars) {
				fsm.addTransition(state1, state2, c);
			}
			for (Character c : validChars) {
				fsm.addTransition(state2, state2, c);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return fsm;
	}

	/**
	 * Erstellt einen Automaten zur Erkennung von Block- und Zeilenkommentaren.
	 * 
	 * @return Ein endlicher Automat der die Wörter (/*|* /|{-|-})|//|--)
	 *         erkennt.
	 */
	public static FiniteStateMachine<Character, StatePayload> generateCommentFSM() {
		FiniteStateMachine<Character, StatePayload> fsm = new FiniteStateMachine<Character, StatePayload>();

		try {
			State<Character, StatePayload> state1, state2, state3, state4, state5, state6, state7, state8, state9, state10, state11, state12;

			state1 = fsm.getCurrentState();

			state2 = new State<Character, StatePayload>();
			state3 = new State<Character, StatePayload>(
					new regextodfaconverter.fsm.StatePayload("COMMENT", "LINE",
							0), true);
			state4 = new State<Character, StatePayload>(
					new regextodfaconverter.fsm.StatePayload("COMMENT",
							"BLOCK_BEGIN", 0), true);

			fsm.addTransition(state1, state2, '/');
			fsm.addTransition(state2, state3, '/');
			fsm.addTransition(state2, state4, '*');

			state5 = new State<Character, StatePayload>();
			state6 = new State<Character, StatePayload>(
					new regextodfaconverter.fsm.StatePayload("COMMENT",
							"BLOCK_END", 0), true);

			fsm.addTransition(state1, state5, '*');
			fsm.addTransition(state5, state6, '/');

			state7 = new State<Character, StatePayload>();
			state8 = new State<Character, StatePayload>(
					new regextodfaconverter.fsm.StatePayload("COMMENT",
							"BLOCK_BEGIN", 0), true);

			fsm.addTransition(state1, state7, '{');
			fsm.addTransition(state7, state8, '-');

			state9 = new State<Character, StatePayload>();
			state10 = new State<Character, StatePayload>(
					new regextodfaconverter.fsm.StatePayload("COMMENT",
							"BLOCK_END", 0), true);

			fsm.addTransition(state1, state9, '-');
			fsm.addTransition(state9, state10, '}');

			state11 = new State<Character, StatePayload>();
			state12 = new State<Character, StatePayload>(
					new regextodfaconverter.fsm.StatePayload("COMMENT", "LINE",
							0), true);

			fsm.addTransition(state1, state11, '-');
			fsm.addTransition(state11, state12, '-');

		} catch (Exception e) {
			e.printStackTrace();
		}

		return fsm;
	}

	/**
	 * Erstellt einen Automaten für relationale Operatoren.
	 * 
	 * @return Ein endlicher Automat der die Wörter (<|<=|<>) erkennt.
	 */
	public static FiniteStateMachine<Character, StatePayload> generateRelopFSM() {
		FiniteStateMachine<Character, StatePayload> fsm = new FiniteStateMachine<Character, StatePayload>();

		try {
			State<Character, StatePayload> state1, state2, state3, state4;

			state1 = fsm.getCurrentState();
			state2 = new State<Character, StatePayload>(
					new regextodfaconverter.fsm.StatePayload("OP", "LT", 0),
					true);
			state3 = new State<Character, StatePayload>(
					new regextodfaconverter.fsm.StatePayload("OP", "LE", 0),
					true);
			state4 = new State<Character, StatePayload>(
					new regextodfaconverter.fsm.StatePayload("OP", "NE", 0),
					true);

			fsm.addTransition(state1, state2, '<');
			fsm.addTransition(state2, state3, '=');
			fsm.addTransition(state2, state4, '>');

		} catch (Exception e) {
			e.printStackTrace();
		}

		return fsm;
	}

	public static void runTest() throws ConvertExecption, Exception {

		FiniteStateMachine<Character, StatePayload> fsm = generateRelopFSM();
		// FiniteStateMachine<Character, StatePayload> fsm =
		// generateCommentFSM();
		fsm.union(generateCommentFSM());
		NfaToDfaConverter<Character, StatePayload> nfaToDfaConverter = new NfaToDfaConverter<Character, StatePayload>();
		fsm = nfaToDfaConverter.convertToDfa(fsm);

		LexemeReader lexemeReader = new BufferedLexemeReader("testrelop.fun");// new
																				// SimpleLexemeReader(
																				// "testrelop.fun");
		Tokenizer tokenizer = new Tokenizer(lexemeReader,
				new MinimalDfa<Character, StatePayload>(fsm));

		Token currentToken;
		while (true) {
			currentToken = tokenizer.getNextToken();
			System.out.print(currentToken.getType());
			System.out.println("  " + currentToken.getAttribute());
		}
	}

}

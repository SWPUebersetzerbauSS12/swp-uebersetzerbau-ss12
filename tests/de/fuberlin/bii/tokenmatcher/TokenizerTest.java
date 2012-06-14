package de.fuberlin.bii.tokenmatcher;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import de.fuberlin.bii.regextodfaconverter.MinimalDfa;
import de.fuberlin.bii.regextodfaconverter.NfaToDfaConverter;
import de.fuberlin.bii.regextodfaconverter.fsm.FiniteStateMachine;
import de.fuberlin.bii.regextodfaconverter.fsm.State;
import de.fuberlin.bii.tokenmatcher.StatePayload;
import de.fuberlin.bii.tokenmatcher.Token;
import de.fuberlin.bii.tokenmatcher.TokenType;
import de.fuberlin.bii.tokenmatcher.Tokenizer;
import de.fuberlin.bii.tokenmatcher.attributes.ParseStringAttribute;
import de.fuberlin.bii.tokenmatcher.attributes.StringAttribute;
import de.fuberlin.bii.bufferedreader.*;

/**
 * Test-Klasse für die Tokenizer-Klasse.
 * 
 * @author Johannes Dahlke
 * 
 */
public class TokenizerTest {

	/**
	 * Test of getNextToken method, of class Tokenizer.
	 */
	@Test
	public void testGetNextToken() throws Exception {
		String sourceFilename = "tests/resources/de/fuberlin/bii/source/tokenmatcher/testrelop.fun";

		FiniteStateMachine<Character, StatePayload> fsm = generateRelopFSM();
		fsm.union(generateCommentFSM());
		NfaToDfaConverter<Character, StatePayload> nfaToDfaConverter = new NfaToDfaConverter<Character, StatePayload>();
		fsm = nfaToDfaConverter.convertToDfa(fsm);

		LexemeReader lexemeReader = new BufferedLexemeReader(sourceFilename);
//		LexemeReader lexemeReader = new SimpleLexemeReader(sourceFile);

		Tokenizer tokenizer = new Tokenizer(lexemeReader,
				new MinimalDfa<Character, StatePayload>(fsm));

		Token currentToken;
		String tokenString;
		String[] tokensToFind = { "<OP, LE>", "<OP, LT>", "<OP, NE>",
				"<OP, LT>", "<OP, NE>", "<OP, LT>", "<OP, NE>", "<OP, LE>",
				"<OP, LT>", "<OP, LT>" };
		int i = 0;
		while (!Token.isEofToken(currentToken = tokenizer.getNextToken())) {
			tokenString = "<" + currentToken.getType() + ", "
					+ currentToken.getAttribute().toString() + ">";
			Assert.assertEquals(tokensToFind[i], tokenString);
			System.out.println(tokenString);
			i++;
		}

		Assert.assertEquals(i, tokensToFind.length);
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
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"ID", new ParseStringAttribute()), true);

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
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"COMMENT", new StringAttribute("LINE"), 0), true);
			state4 = new State<Character, StatePayload>(
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"COMMENT", new StringAttribute("BLOCK_BEGIN"), 0),
					true);

			fsm.addTransition(state1, state2, '/');
			fsm.addTransition(state2, state3, '/');
			fsm.addTransition(state2, state4, '*');

			state5 = new State<Character, StatePayload>();
			state6 = new State<Character, StatePayload>(
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"COMMENT", new StringAttribute("BLOCK_END"), 0),
					true);

			fsm.addTransition(state1, state5, '*');
			fsm.addTransition(state5, state6, '/');

			state7 = new State<Character, StatePayload>();
			state8 = new State<Character, StatePayload>(
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"COMMENT", new StringAttribute("BLOCK_BEGIN"), 0),
					true);

			fsm.addTransition(state1, state7, '{');
			fsm.addTransition(state7, state8, '-');

			state9 = new State<Character, StatePayload>();
			state10 = new State<Character, StatePayload>(
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"COMMENT", new StringAttribute("BLOCK_END"), 0),
					true);

			fsm.addTransition(state1, state9, '-');
			fsm.addTransition(state9, state10, '}');

			state11 = new State<Character, StatePayload>();
			state12 = new State<Character, StatePayload>(
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"COMMENT", new StringAttribute("LINE"), 0), true);

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
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"OP", new StringAttribute("LT"), 0), true);
			state3 = new State<Character, StatePayload>(
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"OP", new StringAttribute("LE"), 0), true);
			state4 = new State<Character, StatePayload>(
					new de.fuberlin.bii.regextodfaconverter.fsm.StatePayload(
							"OP", new StringAttribute("NE"), 0), true);

			fsm.addTransition(state1, state2, '<');
			fsm.addTransition(state2, state3, '=');
			fsm.addTransition(state2, state4, '>');

		} catch (Exception e) {
			e.printStackTrace();
		}

		return fsm;
	}

}

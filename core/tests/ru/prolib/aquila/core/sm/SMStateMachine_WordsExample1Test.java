package ru.prolib.aquila.core.sm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.KW;

/**
 * Реализация КА для разбора предложения на лексемы.
 * <p>
 * В данном случае используется вариант с внешней подачей данных.
 * Все состояния имеют только один вход.
 * У состояний нет входных и выходных действий.
 */
public class SMStateMachine_WordsExample1Test {
	static final String
		src = "Thus a Days object\rcan only \t store a number of days.";
	
	/**
	 * Данные автомата - аккумулятор лексем.
	 */
	static class Words {
		private final List<String> result = new Vector<String>();
		private String currentWord = "";
	}
	
	/**
	 * Состояние ожидания начала слова.
	 */
	static class StartingWord extends SMState {
		private final SMExit onWordStarted;
		StartingWord(final Words words) {
			super();
			onWordStarted = registerExit("WordStarted");
			registerInput(new SMInputAction() {
				@Override public SMExit input(Object data) {
					Character c = (Character) data;
					if ( Character.isLetterOrDigit(c) ) {
						words.currentWord += c;
						return onWordStarted;
					} else {
						return null;
					}
				}
			});
		}
	}

	/**
	 * Состояние накопления символов слова.
	 */
	static class Word extends SMState {
		private final SMExit onClauseEnd, onWordEnd;
		Word(final Words words) {
			super(new SMExitAction() {
				@Override public void exit() { words.currentWord = ""; }
			});
			onClauseEnd = registerExit("CauseEnd");
			onWordEnd = registerExit("WordEnd");
			registerInput(new SMInputAction() {
				@Override public SMExit input(Object data) {
					Character c = (Character)data;
					if ( c == '.' ) {
						words.result.add(words.currentWord);
						return onClauseEnd;
					} else if ( Character.isLetterOrDigit(c) ) {
						words.currentWord += c;
						return null;
					} else {
						words.result.add(words.currentWord);
						return onWordEnd;
					}
				}
			});
		}
	}
	
	private Words words;
	private Map<KW<SMExit>, SMState> transitions;
	private SMStateMachine automat;
	
	@Before
	public void setUp() throws Exception {
		transitions = new HashMap<KW<SMExit>, SMState>();
		words = new Words();
		StartingWord sStartingWord = new StartingWord(words);
		Word sWord = new Word(words);
		transitions.put(new KW<SMExit>(sStartingWord.onWordStarted), sWord);
		transitions.put(new KW<SMExit>(sWord.onWordEnd), sStartingWord);
		transitions.put(new KW<SMExit>(sWord.onClauseEnd), SMState.FINAL);
		automat = new SMStateMachine(sStartingWord, transitions);
	}

	@Test
	public void testJob() throws Exception {
		assertFalse(automat.started());
		assertFalse(automat.finished());
		automat.start();
		for ( int i = 0; i < src.length(); i ++ ) {
			automat.input(src.charAt(i));
		}
		assertTrue(automat.started());
		assertTrue(automat.finished());
		assertSame(SMState.FINAL, automat.getCurrentState());
		
		List<String> expected = new Vector<String>();
		expected.add("Thus");
		expected.add("a");
		expected.add("Days");
		expected.add("object");
		expected.add("can");
		expected.add("only");
		expected.add("store");
		expected.add("a");
		expected.add("number");
		expected.add("of");
		expected.add("days");
		assertEquals(expected, words.result);
	}

}

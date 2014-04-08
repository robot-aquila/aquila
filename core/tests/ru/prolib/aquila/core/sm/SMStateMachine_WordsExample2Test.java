package ru.prolib.aquila.core.sm;

import static org.junit.Assert.*;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
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
 * Пример демонстрирует принцип организации автомата с внутренним обращением к
 * данным (состояние само опрашивает источник данных). Данный метод
 * обеспечивается посредством реализации исключительно входных действий. Выход
 * из состояния выполняется в результате завершения работы процедуры входа. В
 * данном примере состояния не имеют входов и выходных действий.
 */
public class SMStateMachine_WordsExample2Test {
	static final String
		src = "Thus a Days object\rcan only \t store a number of days.";

	/**
	 * Данные автомата. Помимо аккумулятора лексем, теперь необходимо так же 
	 * обеспечить доступ к исходным данным.
	 */
	static class Data {
		private final Reader reader = new CharArrayReader(src.toCharArray());
		private final List<String> result = new Vector<String>();
		private String currentWord = "";
		
		/**
		 * Прочитать очередной символ исходной строки.
		 * <p>
		 * @return символ или null, в случае конца данных
		 */
		public Character read() {
			try {
				int r = reader.read();
				return r == -1 ? null : new Character((char) r);
			} catch ( IOException e ) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Состояние ожидания начала слова.
	 */
	static class StartingWord extends SMState implements SMEnterAction {
		private final SMExit onWordStarted, onDataEnd;
		private final Data data;
		StartingWord(final Data data) {
			super();
			this.data = data;
			onWordStarted = registerExit("WordStarted");
			onDataEnd = registerExit("DataEnd");
			setEnterAction(this);
		}
		
		@Override
		public SMExit enter(SMTriggerRegistry unused) {
			Character c;
			data.currentWord = "";
			while ( (c = data.read()) != null ) {
				if ( Character.isLetterOrDigit(c) ) {
					data.currentWord += c;
					return onWordStarted;
				}
			}
			return onDataEnd;
		}
		
	}
	
	/**
	 * Состояние накопления символов слова.
	 */
	static class Word extends SMState implements SMEnterAction {
		private final SMExit onClauseEnd, onWordEnd, onDataEnd;
		private final Data data;
		Word(final Data data) {
			super();
			this.data = data;
			onClauseEnd = registerExit("CauseEnd");
			onWordEnd = registerExit("WordEnd");
			onDataEnd = registerExit("DataEnd");
			setEnterAction(this);
		}
		
		@Override
		public SMExit enter(SMTriggerRegistry unused) {
			Character c;
			while ( (c = data.read()) != null ) {
				if ( c == '.' ) {
					data.result.add(data.currentWord);
					return onClauseEnd;
				} else if ( Character.isLetterOrDigit(c) ) {
					data.currentWord += c;
				} else {
					data.result.add(data.currentWord);
					return onWordEnd;
				}
			}
			return onDataEnd;
		}
	}
	
	private Data data;
	private Map<KW<SMExit>, SMState> transitions;
	private SMStateMachine automat;
	
	@Before
	public void setUp() throws Exception {
		transitions = new HashMap<KW<SMExit>, SMState>();
		data = new Data();
		StartingWord sStartingWord = new StartingWord(data);
		Word sWord = new Word(data);
		transitions.put(new KW<SMExit>(sStartingWord.onWordStarted), sWord);
		transitions.put(new KW<SMExit>(sStartingWord.onDataEnd), SMState.FINAL);
		transitions.put(new KW<SMExit>(sWord.onWordEnd), sStartingWord);
		transitions.put(new KW<SMExit>(sWord.onClauseEnd), SMState.FINAL);
		transitions.put(new KW<SMExit>(sWord.onDataEnd), SMState.FINAL);
		automat = new SMStateMachine(sStartingWord, transitions);
	}

	@Test
	public void testJob() throws Exception {
		assertFalse(automat.started());
		assertFalse(automat.finished());
		automat.start();
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
		assertEquals(expected, data.result);
	}

}

package ru.prolib.aquila.core.fsm;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.fsm.FSMAutomat;
import ru.prolib.aquila.core.fsm.FSMEventType;
import ru.prolib.aquila.core.fsm.FSMStateActor;
import ru.prolib.aquila.core.fsm.FSMTransitionExistsException;

/**
 * Тест на примере разбиения текста на лексемы.
 */
public class FSMAutomatTest {
	private EventSystem es;
	private FSMAutomat automat;
	private Words words;
	private StartingWord aStartWord;
	private Word aWord;
	private Stub aStub;
	private Error aError;
	private EventDispatcher charDispatcher;
	private EventType onChar;
	
	/**
	 * Аккумулятор лексем.
	 */
	static class Words {
		private final List<String> result;
		private String currentWord = "";
		Words() {
			result = new Vector<String>();
		}
	}
	
	/**
	 * Событие в связи с поступлением нового символа.
	 */
	static class CharEvent extends EventImpl {
		private final char c;
		public CharEvent(EventTypeSI type, char c) {
			super(type);
			this.c = c;
		}
	}

	/**
	 * Актор состояния сборки лексемы.
	 */
	static class Word extends FSMStateActor implements EventListener {
		/**
		 * Внешнее событие.
		 */
		private final EventType onChar;
		private final Words words;
		private final FSMEventType onWordEnd, onClauseEnd;
		
		Word(EventSystem es, EventType onChar, Words words) {
			super(es.getEventQueue());
			this.onChar = onChar;
			this.words = words;
			onWordEnd = createType("WordEnd");
			onClauseEnd = createType("ClauseEnd");
		}
		
		/**
		 * Получить тип события: конец слова.
		 * <p>
		 * @return тип события
		 */
		public FSMEventType OnWordEnd() {
			return onWordEnd;
		}
		
		/**
		 * Получить тип события: конец предложения.
		 * <p>
		 * @return тип события
		 */
		public FSMEventType OnClauseEnd() {
			return onClauseEnd;
		}

		@Override
		public void enter() {
			onChar.addListener(this);
		}

		@Override
		public void exit() {
			onChar.removeListener(this);
			words.currentWord = "";
		}

		@Override
		public void onEvent(Event event) {
			CharEvent e = (CharEvent) event;
			if ( e.c == '.' ) {
				words.result.add(words.currentWord);
				dispatcher.dispatch(new EventImpl(onClauseEnd));
			} else if ( Character.isLetterOrDigit(e.c) ) {
				words.currentWord += e.c;
			} else {
				words.result.add(words.currentWord);
				dispatcher.dispatch(new EventImpl(onWordEnd));
			}
		}
		
	}
	
	/**
	 * Актор состояния ожидания начала лексемы.
	 */
	static class StartingWord extends FSMStateActor implements EventListener {
		/**
		 * Внешнее событие.
		 */
		private final EventType onChar;
		private final Words words;
		private final FSMEventType onWordStarted;
		
		StartingWord(EventSystem es, EventType onChar, Words words) {
			super(es.getEventQueue());
			this.onChar = onChar;
			this.words = words;
			onWordStarted = createType("WordStarted");
		}


		@Override
		public void onEvent(Event event) {
			CharEvent e = (CharEvent) event;
			if ( Character.isLetterOrDigit(e.c) ) {
				words.currentWord += e.c;
				dispatcher.dispatch(new EventImpl(onWordStarted));
			}
		}

		@Override
		public void enter() {
			onChar.addListener(this);
		}

		@Override
		public void exit() {
			onChar.removeListener(this);
		}
		
		/**
		 * Получить тип события: начало слова.
		 * <p>
		 * @return тип события
		 */
		public FSMEventType OnWordStarted() {
			return onWordStarted;
		}
		
	}
	
	/**
	 * Актор-заглушка.
	 */
	static class Stub extends FSMStateActor {
		private final FSMEventType onSomeExit;
		
		public Stub(EventSystem es) {
			super(es.getEventQueue());
			onSomeExit = createType("SomeExit");
		}
		
		/**
		 * Получить тип события: выход.
		 * <p>
		 * @return тип события
		 */
		public FSMEventType OnSomeExit() {
			return onSomeExit;
		}

		@Override
		public void enter() {

		}

		@Override
		public void exit() {
		
		}
		
	}
	
	/**
	 * Актор, генерирующий выходное событие в процессе инициализации. 
	 */
	static class Error extends FSMStateActor {
		private final FSMEventType onError;
		
		public Error(EventSystem es) {
			super(es.getEventQueue());
			onError = createType("Error");
		}
		
		/**
		 * Получить тип события: ошибка.
		 * <p>
		 * @return тип события
		 */
		public FSMEventType OnError() {
			return onError;
		}

		@Override
		public void enter() {
			dispatcher.dispatch(new EventImpl(onError));
		}

		@Override
		public void exit() {
			
		}

	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		es = new EventSystemImpl();
		es.getEventQueue().start();
		automat = new FSMAutomat();
		charDispatcher = es.createEventDispatcher();
		onChar = charDispatcher.createSyncType();
		words = new Words();
		aStartWord = new StartingWord(es, onChar, words);
		aWord = new Word(es, onChar, words);
		aStub = new Stub(es);
		aError = new Error(es);
	}
	
	@After
	public void tearDown() throws Exception {
		es.getEventQueue().stop();
	}
	
	@Test
	public void testTransit() throws Exception {
		automat.transit(aStartWord.OnWordStarted(), aWord);
		assertTrue(automat.isActorRegistered(aStartWord));
		assertTrue(automat.isActorRegistered(aWord));
		assertFalse(automat.isActorRegistered(aStub));
		assertTrue(automat.isTransitionRegistered(aStartWord.OnWordStarted()));
		assertFalse(automat.isTransitionRegistered(aWord.OnClauseEnd()));
		assertFalse(automat.isTransitionRegistered(aWord.OnWordEnd()));
		assertFalse(automat.isTransitionRegistered(aStub.OnSomeExit()));
	}
	
	@Test (expected=FSMTransitionExistsException.class)
	public void testTransit_ThrowsIfExists() throws Exception {
		automat.transit(aStartWord.OnWordStarted(), aWord);
		automat.transit(aStartWord.OnWordStarted(), aStub);
	}
	
	@Test
	public void testTransitExit() throws Exception {
		automat.transitExit(aWord.OnWordEnd());
		assertTrue(automat.isActorRegistered(aWord));
		assertFalse(automat.isActorRegistered(aStartWord));
		assertFalse(automat.isActorRegistered(aStub));
		assertTrue(automat.isTransitionRegistered(aWord.OnWordEnd()));
		assertFalse(automat.isTransitionRegistered(aWord.OnClauseEnd()));
		assertFalse(automat.isTransitionRegistered(aStub.OnSomeExit()));
		assertFalse(automat.isTransitionRegistered(aStartWord.OnWordStarted()));
	}
	
	@Test (expected=FSMTransitionExistsException.class)
	public void testTransitExit_ThrowsIfExists() throws Exception {
		automat.transitExit(aWord.OnWordEnd());
		automat.transit(aWord.OnWordEnd(), aStub);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testStart_ThrowsIfStarted() throws Exception {
		automat.transitExit(aStartWord.OnWordStarted());
		automat.start(aStartWord);
		automat.start(aStartWord);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testStart_ThrowsIfUnknownActor() throws Exception {
		automat.transit(aStartWord.OnWordStarted(), aWord);
		automat.transit(aWord.OnWordEnd(), aStartWord);
		automat.transitExit(aWord.OnClauseEnd());
		automat.start(aStub);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testStart_ThrowsIfInconsistenceGraph() throws Exception {
		automat.transit(aStartWord.OnWordStarted(), aWord);
		automat.transit(aWord.OnWordEnd(), aStartWord);
		automat.start(aStartWord);
	}

	@Test
	public void testWorkflow_SimpleAutomat() throws Exception {
		automat.transit(aStartWord.OnWordStarted(), aWord);
		automat.transit(aWord.OnWordEnd(), aStartWord);
		automat.transitExit(aWord.OnClauseEnd());
		assertNull(automat.getCurrentState());
		automat.start(aStartWord);
		assertSame(aStartWord, automat.getCurrentState());
		
		String source = "Thus a Days object can only store a number of days.";
		for ( int i = 0; i < source.length(); i ++ ) {
			charDispatcher.dispatch(new CharEvent((EventTypeSI) onChar, source.charAt(i)));
		}
		assertNull(automat.getCurrentState());
		
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

	@Test
	public void testWorkflow_TransitionFromEntering() throws Exception {
		automat.transit(aStartWord.OnWordStarted(), aError);
		automat.transit(aError.OnError(), aStub);
		automat.transitExit(aStub.OnSomeExit());
		automat.start(aStartWord);
		
		charDispatcher.dispatch(new CharEvent((EventTypeSI) onChar, 'a'));
		
		assertSame(aStub, automat.getCurrentState());
	}

}

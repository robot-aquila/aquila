package ru.prolib.aquila.core.sm;

import static org.junit.Assert.*;

import java.util.*;

import org.apache.log4j.BasicConfigurator;
import org.junit.*;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.KW;

public class SMStateMachine_TriggersExampleTest {
	private EventSystem es;
	private EventDispatcher dispatcher;
	private Map<KW<SMExit>, SMState> transitions;
	private List<Event> events;
	private EventType s1exit, s1skip, s2back, s2exit;
	private SMStateMachine automat;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	/**
	 * Первое тестовое состояние имеет один вход и один выход. Сигналом на выход
	 * из состояния служит поступление события типа s1exit. События типа s1skip
	 * просто сохраняются в стеке событий для последующей проверки. 
	 */
	class State1 extends SMState implements SMInputAction, SMEnterAction {
		public State1() {
			super();
			setEnterAction(this);
			registerInput(this);
			registerExit("EXIT");
		}
		
		@Override
		public SMExit enter(SMTriggerRegistry triggers) {
			triggers.add(new SMTriggerOnEvent(s1skip));
			triggers.add(new SMTriggerOnEvent(s1exit));
			return null;
		}

		@Override
		public SMExit input(Object data) {
			Event e = (Event) data;
			events.add(e);
			return e.isType(s1exit) ? getExit("EXIT") : null;
		}
	}
	
	/**
	 * Второе тестовое состояние имеет один вход и два выхода. Сигналом на выход
	 * из состояния служит поступление типа s2exit. События типа s2back служат
	 * сигналом на возврат в первое тестовое состояние. Все события сохраняются
	 * в стеке событий для последующей проверки.
	 */
	class State2 extends SMState implements SMInputAction, SMEnterAction {
		private final SMInput in1;
		public State2() {
			super();
			setEnterAction(this);
			in1 = registerInput(this);
			registerExit("EXIT");
			registerExit("BACK");
		}

		@Override
		public SMExit enter(SMTriggerRegistry triggers) {
			triggers.add(new SMTriggerOnEvent(s2back, in1));
			triggers.add(new SMTriggerOnEvent(s2exit));
			return null;
		}

		@Override
		public SMExit input(Object data) {
			Event e = (Event) data;
			events.add(e);
			return getExit(e.isType(s2back) ? "BACK" : "EXIT");
		}
	}

	@Before
	public void setUp() throws Exception {
		events = new LinkedList<Event>();
		es = new EventSystemImpl(new SimpleEventQueue());
		dispatcher = es.createEventDispatcher();
		s1exit = dispatcher.createType("s1exit");
		s1skip = dispatcher.createType("s1skip");
		s2back = dispatcher.createType("s2back");
		s2exit = dispatcher.createType("s2exit");
		SMState s1 = new State1(), s2 = new State2();
		transitions = new HashMap<KW<SMExit>, SMState>();
		transitions.put(new KW<SMExit>(s1.getExit("EXIT")), s2);
		transitions.put(new KW<SMExit>(s2.getExit("BACK")), s1);
		transitions.put(new KW<SMExit>(s2.getExit("EXIT")), SMState.FINAL);
		automat = new SMStateMachine(s1, transitions);
		automat.setDebug(true);
	}
	
	@Test
	public void testJob() throws Exception {
		assertFalse(automat.started());
		assertFalse(automat.finished());
		automat.start();
		EventType src[] = {
				s1skip,
				s1exit,
				s1skip, // д.б. проигнорировано, т.к. триггер деактивирован
				s2back,
				s2exit, // д.б. проигнорировано, т.к. триггер деактивирован
				s1exit,
				s2exit
		};
		for ( EventType t: src ) {
			dispatcher.dispatch(new EventImpl(t));
		}
		assertTrue(automat.started());
		assertTrue(automat.finished());
		assertSame(SMState.FINAL, automat.getCurrentState());
		
		EventType exp[] = {
				s1skip,
				s1exit,
				s2back,
				s1exit,
				s2exit,
		};
		//assertEquals(exp.length, events.size());
		for ( int i = 0; i < exp.length; i ++ ) {
			assertEquals(exp[i], events.get(i).getType());
		}
	}

}

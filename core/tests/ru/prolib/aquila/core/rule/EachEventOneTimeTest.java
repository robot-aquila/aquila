package ru.prolib.aquila.core.rule;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.LinkedHashMap;
import org.junit.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.IMocksControl;

import ru.prolib.aquila.core.CompositeEventRule;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.rule.EachEventOneTime;

/**
 * 2012-04-24
 * $Id: EachEventOneTimeTest.java 219 2012-05-20 12:16:45Z whirlwind $
 */
public class EachEventOneTimeTest {
	private IMocksControl control;
	private EventType type1,type2,type3;
	private CompositeEventRule rule;
	private LinkedHashMap<EventType, Event> state;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ALL);
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		type3 = control.createMock(EventType.class);
		state = new LinkedHashMap<EventType, Event>();
		state.put(type1, null);
		state.put(type2, null);
		state.put(type3, null);
		rule = new EachEventOneTime();
	}

	@Test
	public void testTestNewEvent() throws Exception {
		Event e1 = new EventImpl(type2);
		assertTrue(rule.testNewEvent(e1, state));
		
		// Ошибка, если событие одного типа приходит дважды
		Event e2 = new EventImpl(type2);
		state.put(type2, e2);
		assertTrue(rule.testNewEvent(e2, state));
	}
	
	@Test
	public void testTestNewState() throws Exception {
		assertFalse(rule.testNewState(state));
		
		state.put(type3, new EventImpl(type3));
		assertFalse(rule.testNewState(state));
		
		state.put(type1, new EventImpl(type1));
		assertFalse(rule.testNewState(state));
		
		state.put(type2, new EventImpl(type2));
		assertTrue(rule.testNewState(state));
	}
	
}

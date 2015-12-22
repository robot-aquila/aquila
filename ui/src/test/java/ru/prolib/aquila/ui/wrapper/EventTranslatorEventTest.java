package ru.prolib.aquila.ui.wrapper;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;

/**
 * $Id: DataSourceEventTranslatorEventTest.java 577 2013-03-14 23:17:54Z huan.kaktus $
 */
public class EventTranslatorEventTest {

	private static IMocksControl control;
	
	private EventType type;
	private Event source;
	private EventTranslatorEvent e;
	
	@Before
	public void setUp() {	
		control = createStrictControl();
		
		type = control.createMock(EventType.class);
		source = control.createMock(Event.class);
		
		e = new EventTranslatorEvent(type, source);
	}
	
	@Test
	public void testConstructor() {			
		assertTrue(e.isType(type));
		assertEquals(source, e.getSource());
	}
	
	@Test
	public void testEquals_TrueWithSameObject() {
		assertTrue(e.equals(e));
	}
	
	@Test
	public void testEquals_TrueWithSameTypeAndSource() {
		EventTranslatorEvent e2 = new EventTranslatorEvent(type, source);
		assertTrue(e.equals(e2));
	}
	
	@Test
	public void testEquals_FalseWithNull() {
		assertFalse(e.equals(null));
	}
	
	@Test
	public void testEquals_FalseWithAnotherType() {
		EventType t = control.createMock(EventType.class);
		EventTranslatorEvent e2 = new EventTranslatorEvent(t, source);
		assertFalse(e.equals(e2));
	}
	
	@Test
	public void testEquals_FalseWithAnotherSource() {
		Event source2 = control.createMock(Event.class);
		EventTranslatorEvent e2 = new EventTranslatorEvent(type, source2);
		assertFalse(e.equals(e2));
	}

}

package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;

/**
 * 2012-09-05<br>
 * $Id$
 */
public class PortfolioEventTest {
	private IMocksControl control;
	private EventTypeSI eventType1,eventType2;
	private Portfolio port1,port2;
	private PortfolioEvent event;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventType1 = control.createMock(EventTypeSI.class);
		eventType2 = control.createMock(EventTypeSI.class);
		port1 = control.createMock(Portfolio.class);
		port2 = control.createMock(Portfolio.class);
		event = new PortfolioEvent(eventType1, port1);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(eventType1, event.getType());
		assertSame(port1, event.getPortfolio());
	}
	
	@Test
	public void testEquals_FalseIfNull() throws Exception {
		assertFalse(event.equals(null));
	}
	
	@Test
	public void testEquals_TrueIfSameObject() throws Exception {
		assertTrue(event.equals(event));
	}

	@Test
	public void testEquals_FalseIfDifferentClass() throws Exception {
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals_Ok() throws Exception {
		PortfolioEvent event2 = new PortfolioEvent(eventType2, port1);
		PortfolioEvent event3 = new PortfolioEvent(eventType1, port2);
		PortfolioEvent event4 = new PortfolioEvent(eventType2, port2);
		PortfolioEvent event5 = new PortfolioEvent(eventType1, port1);
		
		assertFalse(event.equals(event2));
		assertFalse(event.equals(event3));
		assertFalse(event.equals(event4));
		assertTrue(event.equals(event5));
	}

}

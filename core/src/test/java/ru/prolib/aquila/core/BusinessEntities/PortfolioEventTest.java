package ru.prolib.aquila.core.BusinessEntities;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.*;

/**
 * 2012-09-05<br>
 * $Id$
 */
public class PortfolioEventTest {
	private IMocksControl control;
	private EventType eventType1,eventType2;
	private Portfolio port1,port2;
	private Instant time1, time2;
	private Set<Integer> tokens1, tokens2;
	private PortfolioEvent event;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventType1 = control.createMock(EventType.class);
		eventType2 = control.createMock(EventType.class);
		port1 = control.createMock(Portfolio.class);
		port2 = control.createMock(Portfolio.class);
		time1 = Instant.parse("2017-08-04T03:00:00Z");
		time2 = Instant.parse("2017-08-04T03:05:00Z");
		tokens1 = new HashSet<>();
		tokens1.add(PortfolioField.BALANCE);
		tokens2 = new HashSet<>();
		tokens2.add(PortfolioField.ASSETS);
		event = new PortfolioEvent(eventType1, port1, time1);
		event.setUpdatedTokens(tokens1);
	}
	
	@Test
	public void testAccessors() throws Exception {
		assertSame(eventType1, event.getType());
		assertSame(port1, event.getPortfolio());
		assertEquals(tokens1, event.getUpdatedTokens());
		assertEquals(time1, event.getTime());
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
		PortfolioEvent event2 = new PortfolioEvent(eventType2, port1, time1);
		PortfolioEvent event3 = new PortfolioEvent(eventType1, port2, time1);
		PortfolioEvent event4 = new PortfolioEvent(eventType2, port2, time1);
		PortfolioEvent event5 = new PortfolioEvent(eventType1, port1, time1);
		event5.setUpdatedTokens(tokens1);
		
		assertFalse(event.equals(new PortfolioEvent(eventType1, port1, time2)));
		assertFalse(event.equals(event2));
		assertFalse(event.equals(event3));
		assertFalse(event.equals(event4));
		assertTrue(event.equals(event5));
		
		event5.setUpdatedTokens(tokens2);
		assertFalse(event.equals(event5));
	}

}

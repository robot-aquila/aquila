package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.util.Date;

import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;

/**
 * $Id$
 */
public class TradeReportEventTest {

	private static IMocksControl control;
	
	private EventType onEvent;
	private SecurityDescriptor descr;
	private TradeReport report; 
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		descr = new SecurityDescriptor(
				"Foo", "FooClass", "Bar", SecurityType.UNK);
		onEvent = control.createMock(EventType.class);
		report = new TradeReport(PositionType.LONG, descr, new Date(), null,
				100L, 0L, 100.00d, 0.00d, 2.00d, 0.00d);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	public void testEquals_True() {
		TradeReportEvent evt1 = new TradeReportEvent(onEvent, report);
		TradeReportEvent evt2 = new TradeReportEvent(onEvent, report);
		assertTrue(evt1.equals(evt2));
	}
	
	public void testEquals_False() {
		TradeReportEvent evt1 = new TradeReportEvent(onEvent, report);
		TradeReportEvent evt2 = new TradeReportEvent(
				control.createMock(EventType.class), report);
		assertFalse(evt1.equals(evt2));
		
		evt2 = new TradeReportEvent(
				onEvent,control.createMock(TradeReport.class)); 
		assertFalse(evt1.equals(evt2));
		
		Event evt3 = control.createMock(Event.class);
		assertFalse(evt1.equals(evt3));
	}

	@Test
	public void test() {
		TradeReportEvent evt = new TradeReportEvent(onEvent, report);
		IsInstanceOf.instanceOf(EventImpl.class).matches(evt);
		
		assertEquals(onEvent, evt.getType());
		assertEquals(report, evt.getTradeReport());
	}

}

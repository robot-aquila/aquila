package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

/**
 * $Id$
 */
public class TradeReportEventTest {

	private static IMocksControl control;
	
	private EventType onEvent;
	private TradeReport report; 
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		onEvent = control.createMock(EventType.class);
		report = control.createMock(TradeReport.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		TradeReportEvent evt = new TradeReportEvent(onEvent, report);
		IsInstanceOf.instanceOf(EventImpl.class).matches(evt);
		
		assertEquals(onEvent, evt.getType());
		assertEquals(report, evt.getTradeReport());
	}

}

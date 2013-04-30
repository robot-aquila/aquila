package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import java.util.Date;

import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.PositionType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.utils.Variant;

/**
 * $Id$
 */
public class TradeReportEventTest {

	private static IMocksControl control;
	
	private EventType onEvent;
	private EventType onEvent2;
	private SecurityDescriptor descr;
	private TradeReport report;
	private TradeReport report2;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		
		descr = new SecurityDescriptor(
				"Foo", "FooClass", "Bar", SecurityType.UNK);
		onEvent = control.createMock(EventType.class);
		onEvent2 = control.createMock(EventType.class);
		report = new TradeReport(PositionType.LONG, descr, new Date(), null,
				100L, 0L, 100.00d, 0.00d, 2.00d, 0.00d);
		report2 = new TradeReport(PositionType.SHORT, descr, new Date(), null,
				100L, 0L, 100.00d, 0.00d, 2.00d, 0.00d);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testEquals() {
		TradeReportEvent et = new TradeReportEvent(onEvent, report);
		
		Variant<EventType> types = new Variant<EventType>()
				.add(onEvent)
				.add(onEvent2);
		Variant<TradeReport> reports = new Variant<TradeReport>(types)
				.add(report)
				.add(report2);
		Variant<?> iterator = reports;
		int foundCnt = 0;
		EventType foundType = null, x; TradeReport foundRep = null, y;
		do {
			x = types.get();
			y = reports.get();
			TradeReportEvent event = new TradeReportEvent(x, y);
			if(et.equals(event)) {
				foundCnt++;
				foundType = x;
				foundRep = y;
			}
		}while(iterator.next());
		assertEquals(1, foundCnt);
		assertEquals(onEvent, foundType);
		assertEquals(report, foundRep);
	}

	@Test
	public void test() {
		TradeReportEvent evt = new TradeReportEvent(onEvent, report);
		IsInstanceOf.instanceOf(EventImpl.class).matches(evt);
		
		assertEquals(onEvent, evt.getType());
		assertEquals(report, evt.getTradeReport());
	}

}

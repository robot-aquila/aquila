package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.Variant;

public class TradeReportEventTest {
	private IMocksControl control;
	private EventType type1, type2;
	private TradeReportImpl report1, report2;
	private TradeReportEvent event;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventType.class);
		type2 = control.createMock(EventType.class);
		report1 = control.createMock(TradeReportImpl.class);
		report2 = control.createMock(TradeReportImpl.class);
		event = new TradeReportEvent(type1, report1);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vType = new Variant<EventType>()
				.add(type1)
				.add(type2);
		Variant<TradeReportImpl> vRep = new Variant<TradeReportImpl>(vType)
				.add(report1)
				.add(report2);
		Variant<?> iterator = vRep;
		int foundCnt = 0;
		TradeReportEvent x = null, found = null;
		do {
			x = new TradeReportEvent(vType.get(), vRep.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		}while(iterator.next());
		assertEquals(1, foundCnt);
		assertSame(type1, found.getType());
		assertSame(report1, found.getReport());
	}

}

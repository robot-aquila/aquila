package ru.prolib.aquila.core.report;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.report.trades.RTradeImpl;
import ru.prolib.aquila.core.utils.Variant;

public class TradeReportEventTest {
	private IMocksControl control;
	private EventTypeSI type1, type2;
	private RTradeImpl report1, report2;
	private TradeReportEvent event;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		type1 = control.createMock(EventTypeSI.class);
		type2 = control.createMock(EventTypeSI.class);
		report1 = control.createMock(RTradeImpl.class);
		report2 = control.createMock(RTradeImpl.class);
		event = new TradeReportEvent(type1, report1, 8);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventTypeSI> vType = new Variant<EventTypeSI>()
			.add(type1)
			.add(type2);
		Variant<RTradeImpl> vRep = new Variant<RTradeImpl>(vType)
			.add(report1)
			.add(report2);
		Variant<Integer> vIdx = new Variant<Integer>(vRep)
			.add(8)
			.add(null);
		Variant<?> iterator = vIdx;
		int foundCnt = 0;
		TradeReportEvent x = null, found = null;
		do {
			x = new TradeReportEvent(vType.get(), vRep.get(), vIdx.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		}while(iterator.next());
		assertEquals(1, foundCnt);
		assertSame(type1, found.getType());
		assertSame(report1, found.getReport());
		assertEquals(new Integer(8), found.getIndex());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		Event expected = new TradeReportEvent(type1, report1, null);
		assertEquals(expected, new TradeReportEvent(type1, report1));
	}

}

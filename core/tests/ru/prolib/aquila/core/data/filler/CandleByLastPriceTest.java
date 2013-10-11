package ru.prolib.aquila.core.data.filler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

public class CandleByLastPriceTest {
	private IMocksControl control;
	private Terminal terminal;
	private EditableCandleSeries candles;
	private Security security;
	private EventType type;
	private CandleByLastPrice updater;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
		candles = control.createMock(EditableCandleSeries.class);
		security = control.createMock(Security.class);
		type = control.createMock(EventType.class);
		updater = new CandleByLastPrice(security, candles);
		
		expect(security.OnChanged()).andStubReturn(type);
		expect(security.getTerminal()).andStubReturn(terminal);
	}
	
	@Test
	public void testStart() throws Exception {
		type.addListener(same(updater));
		control.replay();
		
		updater.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		type.removeListener(same(updater));
		control.replay();
		
		updater.stop();
		
		control.verify();
	}
	
	@Test
	public void testOnEvent() throws Exception {
		DateTime time = new DateTime(2013, 6, 2, 13, 45, 23);
		expect(terminal.getCurrentTime()).andReturn(time);
		expect(security.getLastPrice()).andReturn(85.23d);
		candles.aggregate(eq(new Tick(time, 85.23d)), eq(true));
		control.replay();
		
		updater.onEvent(null);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(updater.equals(updater));
		assertFalse(updater.equals(null));
		assertFalse(updater.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<Security> vSec = new Variant<Security>()
			.add(security)
			.add(control.createMock(Security.class));
		Variant<EditableCandleSeries> vAggr =
				new Variant<EditableCandleSeries>(vSec)
			.add(candles)
			.add(control.createMock(EditableCandleSeries.class));
		Variant<?> iterator = vAggr;
		int foundCnt = 0;
		CandleByLastPrice x, found = null;
		do {
			x = new CandleByLastPrice(vSec.get(), vAggr.get());
			if ( updater.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(security, found.getSecurity());
		assertSame(candles, found.getCandles());
	}

}

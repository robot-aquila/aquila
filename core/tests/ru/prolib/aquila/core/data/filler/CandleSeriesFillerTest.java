package ru.prolib.aquila.core.data.filler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterStub;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.CandleSeries;
import ru.prolib.aquila.core.data.CandleSeriesImpl;
import ru.prolib.aquila.core.utils.Variant;

public class CandleSeriesFillerTest {
	private IMocksControl control;
	private CandleSeries candles;
	private Starter updater, flusher;
	private CandleSeriesFiller filler;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		candles = new CandleSeriesImpl();
		updater = control.createMock(Starter.class);
		flusher = control.createMock(Starter.class);
		filler = new CandleSeriesFiller(candles, updater, flusher);
	}
	
	@Test
	public void testStart() throws Exception {
		flusher.start();
		updater.start();
		control.replay();
		
		filler.start();
		
		control.verify();
	}
	
	@Test
	public void testStop() throws Exception {
		updater.stop();
		flusher.stop();
		
		control.replay();
		
		filler.stop();
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(filler.equals(filler));
		assertFalse(filler.equals(null));
		assertFalse(filler.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<CandleSeries> vCndl = new Variant<CandleSeries>()
			.add(candles)
			.add(control.createMock(CandleSeries.class));
		Variant<Starter> vUpd = new Variant<Starter>(vCndl)
			.add(updater)
			.add(control.createMock(Starter.class));
		Variant<Starter> vFlu = new Variant<Starter>(vUpd)
			.add(flusher)
			.add(control.createMock(Starter.class));
		Variant<?> iterator = vFlu;
		int foundCnt = 0;
		CandleSeriesFiller x, found = null;
		do {
			x = new CandleSeriesFiller(vCndl.get(), vUpd.get(), vFlu.get());
			if ( filler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(candles, found.getCandles());
		assertSame(updater, found.getUpdater());
		assertSame(flusher, found.getFlusher());
	}
	
	@Test
	public void testConstruct3_SecurityPeriodAutoflush() throws Exception {
		Security security = control.createMock(Security.class);
		Terminal terminal = control.createMock(Terminal.class);
		expect(security.getTerminal()).andStubReturn(terminal);
		CandleAggregator aggregator = new CandleAggregator(15);
		CandleSeriesFiller expected = new CandleSeriesFiller(
				new CandleSeriesImpl(),
				new CandleByTrades(security, aggregator),
				new CandleFlusher(aggregator, terminal));
		control.replay();
		
		assertEquals(expected, new CandleSeriesFiller(security, 15, true));
		
		control.verify();
	}
	
	@Test
	public void testConstruct3_SecurityPeriodNoAutoflush() throws Exception {
		Security security = control.createMock(Security.class);
		Terminal terminal = control.createMock(Terminal.class);
		expect(security.getTerminal()).andStubReturn(terminal);
		CandleAggregator aggregator = new CandleAggregator(15);
		CandleSeriesFiller expected = new CandleSeriesFiller(
				new CandleSeriesImpl(),
				new CandleByTrades(security, aggregator),
				new StarterStub());
		control.replay();
		
		assertEquals(expected, new CandleSeriesFiller(security, 15, false));
		
		control.verify();
	}

}

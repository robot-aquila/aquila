package ru.prolib.aquila.core.data;

import static org.junit.Assert.*;

import java.time.Instant;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.utils.Variant;

public class CSLastTradeFillerTest {
	private static Symbol symbol1, symbol2;
	private IMocksControl control;
	private CSUtils utilsMock;
	private EditableTerminal terminal;
	private Security security1, security2;
	private ObservableSeriesImpl<Candle> series1, series2;
	private CSLastTradeFiller filler;

	@BeforeClass
	public static void setUpBeforeClass() {
		symbol1 = new Symbol("SBER");
		symbol2 = new Symbol("GAZP");
	}
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		utilsMock = control.createMock(CSUtils.class);
		series1 = control.createMock(ObservableSeriesImpl.class);
		series2 = control.createMock(ObservableSeriesImpl.class);
		terminal = new BasicTerminalBuilder()
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		security1 = terminal.getEditableSecurity(symbol1);
		security2 = terminal.getEditableSecurity(symbol2);
		filler = new CSLastTradeFiller(security1, ZTFrame.M1, series1, utilsMock);
	}
	
	@Test
	public void testCtor() {
		assertSame(security1, filler.getSecurity());
		assertEquals(ZTFrame.M1, filler.getTF());
		assertSame(series1, filler.getSeries());
		assertSame(utilsMock, filler.getUtils());
	}
	
	@Test
	public void testStart() {
		assertFalse(filler.isStarted());
		
		filler.start();
		
		assertTrue(filler.isStarted());
		assertTrue(security1.onLastTrade().isListener(filler));
	}
	
	@Test
	public void testStart_DoNothingIfStarted() {
		filler.start();
		security1.onLastTrade().removeListener(filler);
		
		filler.start();
		
		assertTrue(filler.isStarted());
		assertFalse(security1.onLastTrade().isListener(filler));
	}
	
	@Test
	public void testStop() {
		filler.start();
		
		filler.stop();
		
		assertFalse(filler.isStarted());
		assertFalse(security1.onLastTrade().isListener(filler));
	}
	
	@Test
	public void testStop_DoNothingIfNotStarted() {
		filler.stop();
		
		assertFalse(filler.isStarted());
		assertFalse(security1.onLastTrade().isListener(filler));
	}
	
	@Test
	public void testOnEvent() throws Exception {
		Instant time = Instant.parse("2017-08-04T21:20:00Z");
		Tick trade = Tick.ofTrade(Instant.EPOCH, CDecimalBD.of("24.15"), CDecimalBD.of(100L));
		filler.start();
		expect(utilsMock.aggregate(same(series1), eq(ZTFrame.M1), eq(trade)))
			.andReturn(true);
		control.replay();
		
		filler.onEvent(new SecurityTickEvent(security1.onLastTrade(), security1, time, trade));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_SkipIfNotStarted() {
		Instant time = Instant.parse("2017-08-04T21:20:00Z");
		control.replay();
		
		Tick trade = Tick.ofTrade(Instant.EPOCH, CDecimalBD.of("24.15"), CDecimalBD.of(100L));
		filler.onEvent(new SecurityTickEvent(security1.onLastTrade(), security1, time, trade));
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(filler.equals(filler));
		assertFalse(filler.equals(null));
		assertFalse(filler.equals(this));
	}
	
	@Test
	public void testEquals() {
		filler.start();
		Variant<Security> vSec = new Variant<>(security1, security2);
		Variant<ZTFrame> vTF = new Variant<>(vSec, ZTFrame.M1, ZTFrame.M10);
		Variant<ObservableSeriesImpl<Candle>> vSer = new Variant<>(vTF, series1, series2);
		Variant<CSUtils> vUt = new Variant<>(vSer, utilsMock, control.createMock(CSUtils.class));
		Variant<Boolean> vStart = new Variant<>(vUt, true, false);
		Variant<?> iterator = vStart;
		int foundCnt = 0;
		CSLastTradeFiller x, found = null;
		do {
			x = new CSLastTradeFiller(vSec.get(), vTF.get(), vSer.get(), vUt.get());
			if ( vStart.get() ) {
				x.start();
			}
			if ( filler.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(security1, found.getSecurity());
		assertEquals(ZTFrame.M1, found.getTF());
		assertSame(series1, found.getSeries());
		assertSame(utilsMock, found.getUtils());
		assertTrue(found.isStarted());
	}

}
